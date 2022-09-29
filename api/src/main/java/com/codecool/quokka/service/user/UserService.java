package com.codecool.quokka.service.user;

import com.codecool.quokka.model.user.Account;
import com.codecool.quokka.dao.user.UserDao;
import com.codecool.quokka.model.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", Pattern.CASE_INSENSITIVE);

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
//    public UserService(@Qualifier("inMemoUserDao") UserDao userDao) {
//        this.userDao = userDao;
//    }

    public UserDto addUser(Account account) {
        account.hashPassword();
//        return userDao.addUser(account);
        userDao.save(account);
        UserDto data = new UserDto(account.getUserName(), account.getId(), account.getFullName(), account.getEmailAddress())
;        return data;
    }

    public Set<UserDto> getAllUser() {
        Set<Account> accounts = (Set<Account>) userDao.findAll();
        return accounts.stream()
                .map(e -> new UserDto(e.getUserName(), e.getId(), e.getFullName(), e.getEmailAddress()))
                .collect(Collectors.toSet());
    }

    public Optional<UserDto> getUser(UUID id) {
        Optional<Account> account = userDao.findAccountByUserId(id);
        if (account.isPresent()) {
            var acc = account.get();
            return Optional.of(new UserDto(acc.getUserName(), acc.getId(), acc.getFullName(), acc.getEmailAddress()));
        }
        return Optional.empty();
    }

    public void deleteUser(UUID id) {
        userDao.deleteAccountByUserId(id);
//        userDao.deleteUser(id);
    }

//    public Optional<UserDto> updateUser(UUID id, HashMap<String, String> fields) {
//        return userDao.updateUser(id, fields);
//    }

    public boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public boolean getUserByUserName(String userName) {
        return userDao.findAccountByUserName(userName).isPresent();
//        return userDao.getUserByUserName(userName);
    }

    public boolean getUserByEmail(String emailAddress) {
        return userDao.findAccountByEmailAddress(emailAddress).isPresent();
    }
}
