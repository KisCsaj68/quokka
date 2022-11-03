package com.codecool.quokka.service.account;

import com.codecool.quokka.model.account.Account;
import com.codecool.quokka.dao.account.AccountDao;
import com.codecool.quokka.model.account.AccountDto;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AccountService {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", Pattern.CASE_INSENSITIVE);

    private final AccountDao accountDao;

    @Autowired
    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public AccountDto addAccount(Account account) {
        account.hashPassword();
        Account accnt = accountDao.saveAndFlush(account);
        System.out.println(accnt);
        return AccountDto.from(accnt);
    }

    public Set<AccountDto> getAllAccount() {
        return StreamSupport.stream(accountDao.findAll().spliterator(), false).map(AccountDto::from).collect(Collectors.toSet());
    }

    public Optional<AccountDto> getAccount(UUID id) {
        System.out.println(id);
        Optional<Account> account = accountDao.findAccountById(id);
        if (account.isPresent()) {
            return Optional.of(AccountDto.from(account.get()));
        }
        return Optional.empty();
    }

    @Transactional
    public void deleteAccount(UUID id) {
        accountDao.deleteAccountById(id);
    }

    public Optional<AccountDto> updateAccount(UUID id, Map<String, String> fields) {
        Optional<Account> account = accountDao.findAccountById(id);
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
                    String password = Hashing.sha256().hashString(fields.get("password"), StandardCharsets.UTF_8).toString();
                    acc.setPassword(password);
                    break;
            }
        }
        return Optional.of(AccountDto.from(accountDao.save(acc)));
    }

    public boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public boolean getAccountByUserName(String userName) {
        return accountDao.findAccountByUserName(userName).isPresent();
    }

    public boolean getAccountByEmail(String emailAddress) {
        return accountDao.findAccountByEmailAddress(emailAddress).isPresent();
    }
}
