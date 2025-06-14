package org.example.transferservice.service;

import org.example.transferservice.reponse.CurrencyRateResponse;
import org.example.transferservice.reponse.HttpResponseDto;
import org.example.transferservice.request.AccountOperationRequest;
import org.example.transferservice.request.BlockerRequest;
import org.example.transferservice.request.TransferOperationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransferService {

    @Autowired
    private AccountApplicationService accountApplicationService;

    @Autowired
    private BlockerApplicationService blockerApplicationService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ExchangeApplicationService exchangeApplicationService;


    public void transferOperation(TransferOperationRequest request) {
        BlockerRequest blockerDto = BlockerRequest.builder()
                .currency(request.getFromCurrency())
                .login(request.getFromLogin())
                .action("TRANSFER")
                .amount(request.getAmount())
                .build();

        HttpResponseDto blockerResponse = blockerApplicationService.checkBlocker(blockerDto);
        if (!blockerResponse.getStatusCode().equals("0")) {
            throw new RuntimeException(blockerResponse.getStatusMessage());
        }

        blockerDto = BlockerRequest.builder()
                .currency(request.getToCurrency())
                .login(request.getToLogin())
                .action("TRANSFER")
                .amount(request.getAmount())
                .build();

        blockerResponse = blockerApplicationService.checkBlocker(blockerDto);
        if (!blockerResponse.getStatusCode().equals("0")) {
            throw new RuntimeException(blockerResponse.getStatusMessage());
        }

        String fromCurrency = request.getFromCurrency();
        String toCurrency = request.getToCurrency();
        Double amount = request.getAmount();

        //Списание средств со счета отправителя
        AccountOperationRequest cashOutDto = AccountOperationRequest.builder()
                .currency(fromCurrency)
                .login(request.getFromLogin())
                .amount(amount)
                .build();

        HttpResponseDto cashOutResponse = accountApplicationService.cashOut(cashOutDto);
        if (!cashOutResponse.getStatusCode().equals("0")) {
            throw new RuntimeException(cashOutResponse.getStatusMessage());
        }

        //Конвертация суммы, если валюты различаются
        double convertedAmount = convertAmount(fromCurrency, toCurrency, amount);;

        //Зачисление средств на счет получателя
        AccountOperationRequest cashInDto = AccountOperationRequest.builder()
                .currency(toCurrency)
                .login(request.getToLogin())
                .amount(convertedAmount)
                .build();

        HttpResponseDto cashInResponse = accountApplicationService.cashIn(cashInDto);
        if (!cashInResponse.getStatusCode().equals("0")) {
            AccountOperationRequest refundDto = AccountOperationRequest.builder()
                    .currency(fromCurrency)
                    .login(request.getFromLogin())
                    .amount(amount)
                    .build();

            HttpResponseDto refundResponse = accountApplicationService.cashIn(refundDto);
            if (!refundResponse.getStatusCode().equals("0")) {
                throw new RuntimeException(refundResponse.getStatusMessage());
            }
            throw new RuntimeException(cashInResponse.getStatusMessage());
        }

        String emailFrom = cashOutDto.getLogin();
        String messageFrom = "Со счета списано " + cashOutDto.getAmount() + " " + cashOutDto.getCurrency();
        notificationService.sendNotification(emailFrom, messageFrom);

        String emailTo = cashInDto.getLogin();
        String messageTo = "Ваш счет пополнен на " + cashInDto.getAmount() + " " + cashInDto.getCurrency();
        notificationService.sendNotification(emailTo, messageTo);
    }

    private double convertAmount(String fromCurrency, String toCurrency, double amount) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        List<CurrencyRateResponse> rates = exchangeApplicationService.getRates();

        Optional<CurrencyRateResponse> fromRate = rates.stream()
                .filter(rate -> rate.getCurrency().equals(fromCurrency))
                .findFirst();

        Optional<CurrencyRateResponse> toRate = rates.stream()
                .filter(rate -> rate.getCurrency().equals(toCurrency))
                .findFirst();

        if (fromRate.isEmpty() || toRate.isEmpty()) {
            throw new IllegalArgumentException("Не удалось получить курсы валют для конвертации.");
        }

        double fromBuyRate = fromRate.get().getBuy(); // Курс продажи fromCurrency
        double toSaleRate = toRate.get().getSale();       // Курс покупки toCurrency

        return (amount * fromBuyRate) / toSaleRate;
    }
}
