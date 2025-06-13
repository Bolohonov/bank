package org.example.cashservice.service;

import org.example.cashservice.dto.AccountOperationDto;
import org.example.cashservice.dto.BlockerDto;
import org.example.cashservice.dto.HttpResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CashService {

    @Autowired
    private AccountService accountApplicationService;

    @Autowired
    private BlockerService blockerService;

    @Autowired
    private NotificationService notificationService;


    public void cashIn(AccountOperationDto accountOperationDto) {
        BlockerDto blockerDto = BlockerDto.builder()
                .currency(accountOperationDto.getCurrency())
                .login(accountOperationDto.getLogin())
                .action("CASH_IN")
                .amount(accountOperationDto.getAmount())
                .build();

        HttpResponseDto blockerResponse = blockerService.checkBlocker(blockerDto);
        if (!blockerResponse.getStatusCode().equals("0")) {
            throw new RuntimeException(blockerResponse.getStatusMessage());
        }

        HttpResponseDto accountResponse = accountApplicationService.cashIn(accountOperationDto);
        if (!accountResponse.getStatusCode().equals("0")) {
            throw new RuntimeException(accountResponse.getStatusMessage());
        }

        String email = accountOperationDto.getLogin();
        String message = "Ваш счет пополнен на " + accountOperationDto.getAmount() + " " + accountOperationDto.getCurrency();
        notificationService.sendNotification(email, message);
    }

    public void cashOut(AccountOperationDto accountOperationDto) {
        BlockerDto blockerDto = BlockerDto.builder()
                .currency(accountOperationDto.getCurrency())
                .login(accountOperationDto.getLogin())
                .action("CASH_OUT")
                .amount(accountOperationDto.getAmount())
                .build();

        HttpResponseDto blockerResponse = blockerService.checkBlocker(blockerDto);
        if (!blockerResponse.getStatusCode().equals("0")) {
            throw new RuntimeException(blockerResponse.getStatusMessage());
        }

        HttpResponseDto accountResponse = accountApplicationService.cashOut(accountOperationDto);
        if (!accountResponse.getStatusCode().equals("0")) {
            throw new RuntimeException(accountResponse.getStatusMessage());
        }

        String email = accountOperationDto.getLogin();
        String message = "С вашего счета выполнено списание на " + accountOperationDto.getAmount() + " " + accountOperationDto.getCurrency();
        notificationService.sendNotification(email, message);
    }
}
