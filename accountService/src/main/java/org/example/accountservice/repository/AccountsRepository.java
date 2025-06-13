package org.example.accountservice.repository;

import org.example.accountservice.model.Account;
import org.example.accountservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountsRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByUser(User user);

    Optional<Account> findAccountByUserAndCurrency(User user, String currency);
}
