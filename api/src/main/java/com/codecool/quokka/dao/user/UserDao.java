package com.codecool.quokka.dao.user;

import com.codecool.quokka.model.account.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserDao extends CrudRepository<Account, Long> {

//     UserDto addUser(String name, String userName, String emailAddress, String passWord);
//
//     UserDto addUser(Account account);
//     Set<UserDto> getAllUser();
//
//     Optional<UserDto> getUser(UUID id);
//
//     void deleteUser(UUID id);
//
//     Optional<UserDto> updateUser(UUID id, HashMap<String, String> fields);

    Optional<Account> findAccountByUserName(String userName);
    Optional<Account> findAccountByEmailAddress(String emailAddress);
    void deleteAccountByUserId(UUID id);
    Optional<Account> findAccountByUserId(UUID id);
//    boolean getUserByUserName(String userName);

//     boolean getUserByEmail(String emailAddress);
}
