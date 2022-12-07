package com.codecool.quokka.service.account;

import com.codecool.quokka.dao.role.AccountRoleDao;
import com.codecool.quokka.model.account.Account;
import com.codecool.quokka.dao.account.AccountDao;
import com.codecool.quokka.model.account.AccountDto;
import com.codecool.quokka.model.role.AccountRole;
import com.google.common.hash.Hashing;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
public class AccountService  implements UserDetailsService {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", Pattern.CASE_INSENSITIVE);

    private final AccountDao accountDao;
    private final AccountRoleDao accountRoleDao;


    @Autowired
    public AccountService(AccountDao accountDao, AccountRoleDao accountRoleDao) {
        this.accountDao = accountDao;
        this.accountRoleDao = accountRoleDao;
    }

    public AccountDto addAccount(Account account) {
        account.encryptPassword();
        Optional<AccountRole> role = accountRoleDao.getAccountRoleByName("TRADER");
        if (role.isPresent()) {
            account.addRole(role.get());
        }
        else {
            AccountRole newRole = new AccountRole("TRADER", "Trading");
            account.addRole(newRole);

        }
        Account accnt = accountDao.saveAndFlush(account);
        return AccountDto.from(accnt);
    }

    public Set<AccountDto> getAllAccount() {
        return StreamSupport.stream(accountDao.findAll().spliterator(), false).map(AccountDto::from).collect(Collectors.toSet());
    }

    public Optional<AccountDto> getAccount(UUID id) {
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
                case "email_address":
                    acc.setEmailAddress(fields.get("email_address"));
                    break;
                case "full_name":
                    acc.setFullName(fields.get("full_name"));
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountDao.findAccountByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
