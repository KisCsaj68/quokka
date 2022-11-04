package com.codecool.quokka.dao.account;

import com.codecool.quokka.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountDao extends JpaRepository<Account, UUID> {

    Optional<Account> findAccountByUserName(String userName);

    Optional<Account> findAccountByEmailAddress(String emailAddress);

    void deleteAccountById(UUID id);

    Optional<Account> findAccountById(UUID id);
}