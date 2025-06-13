package org.example.accountservice.repository;

import org.example.accountservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByLogin(String login);
}
