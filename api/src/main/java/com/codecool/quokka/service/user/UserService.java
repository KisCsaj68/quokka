package com.codecool.quokka.service.user;

import com.codecool.quokka.model.account.Account;
import com.codecool.quokka.dao.user.UserDao;
import com.codecool.quokka.model.account.AccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", Pattern.CASE_INSENSITIVE);

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public AccountDto addUser(Account account) {
        account.hashPassword();
        return AccountDto.from(userDao.save(account));
    }

    public Set<AccountDto> getAllUser() {
        return StreamSupport.stream(userDao.findAll().spliterator(), false)
                .map(AccountDto::from)
                .collect(Collectors.toSet());
    }

    public Optional<AccountDto> getUser(UUID id) {
        Optional<Account> account = userDao.findAccountByUserId(id);
        if (account.isPresent()) {
            return Optional.of(AccountDto.from(account.get()));
        }
        return Optional.empty();
    }

    @Transactional
    public void deleteUser(UUID id) {
        userDao.deleteAccountByUserId(id);
    }

    public Optional<AccountDto> updateUser(UUID id, Map<String, String> fields) {
        Optional<Account> account = userDao.findAccountByUserId(id);
        if (account.isEmpty()) {
            return Optional.empty();
        }
        Account acc = account.get();
        for (String item : fields.keySet()) {
            switch (item) {
                case "emailAddress":
                    acc.setEmailAddress(fields.get("emailAddress"));
                    break;
                case "fullName":
                    acc.setFullName(fields.get("fullName"));
                    break;
                case "password":
                    acc.setPassword(fields.get("password"));
                    break;
            }
        }
        return Optional.of(AccountDto.from(userDao.save(acc)));
    }

    public boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public boolean getUserByUserName(String userName) {
        return userDao.findAccountByUserName(userName).isPresent();
    }

    public boolean getUserByEmail(String emailAddress) {
        return userDao.findAccountByEmailAddress(emailAddress).isPresent();
    }
}
