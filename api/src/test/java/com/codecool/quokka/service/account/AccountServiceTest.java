package com.codecool.quokka.service.account;

import com.codecool.quokka.dao.account.AccountDao;
import com.codecool.quokka.model.account.Account;
import com.codecool.quokka.model.account.AccountDto;
import com.codecool.quokka.model.role.AccountRole;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {
    private static final UUID id = UUID.fromString("b462290f-4006-4d71-8a39-e956e245ede8");
    private static AccountRole role = new AccountRole("Trader", "Trading");
    private static final UUID fakeId = UUID.fromString("b462290f-4008-4d71-8a39-e956e245ede8");
    private static final Account account = new Account("Test User", "test@asd.com", "TestUser", "asd", id, true, true, true, true, new HashSet<>(Arrays.asList(role)));
    private static final Account accountWithoutId = new Account("Test User", "test@asd.com", "TestUser", "asd", true, true, true, true, new HashSet<>(Arrays.asList(role)));

    private AccountService accountService;

    @Mock
    private AccountDao accountDao;

    @Before
    public void setUp() {
        accountService = new AccountService(accountDao);
    }

    @Test
    public void addAccount() {
        AccountDto dto = AccountDto.from(account);
        Mockito.when(accountDao.saveAndFlush(accountWithoutId)).thenReturn(account);
        AccountDto resultDto = accountService.addAccount(accountWithoutId);
        assertEquals(dto, resultDto);
    }

    @Test
    public void getAllAccount() {
        AccountDto dto = AccountDto.from(account);
        Mockito.when(accountDao.findAll()).thenReturn((List.of(account)));
        Set<AccountDto> dtos = accountService.getAllAccount();
        assertEquals(dtos, Set.of(dto));
    }

    @Test
    public void getAccount() {
        AccountDto dto = AccountDto.from(account);
        Mockito.when(accountDao.findAccountById(id)).thenReturn(Optional.of(account));
        Mockito.when(accountDao.findAccountById(fakeId)).thenReturn(Optional.empty());
        Optional<AccountDto> resultDto = accountService.getAccount(id);
        Optional<AccountDto> fakeDto = accountService.getAccount(fakeId);
        assertEquals(resultDto, Optional.of(dto));
        assertEquals(fakeDto, Optional.empty());
    }

    @Test
    public void updateUserFullName() {
        AccountDto updatedFullNameDto = new AccountDto(account.getUserName(), account.getId(), "User3456", account.getEmailAddress());
        Account updatedAccountFullName = new Account("User3456", "test@asd.com", "TestUser", "asd", id, true, true, true, true, new HashSet<>(Arrays.asList(role)));
        Map data = new HashMap<>();
        data.put("fullName", "User3456");
        Mockito.when(accountDao.findAccountById(id)).thenReturn(Optional.ofNullable(accountWithoutId));
        Mockito.when(accountDao.save(accountWithoutId)).thenReturn(updatedAccountFullName);
        Optional<AccountDto> resultDto = accountService.updateAccount(id, data);
        assertEquals(resultDto, Optional.of(updatedFullNameDto));
    }

    @Test
    public void updateEmail() {
        Account updatedAccountEmail = new Account("Test User", "User3456@codecool.com", "TestUser", "asd", id, true, true, true, true, new HashSet<>(Arrays.asList(role)));
        AccountDto updatedEmailDto = new AccountDto(account.getUserName(), account.getId(), account.getFullName(), "User3456@codecool.com");
        Map dataEmail = new HashMap<>();
        dataEmail.put("emailAddress", "User3456@codecool.com");
        Mockito.when(accountDao.findAccountById(id)).thenReturn(Optional.ofNullable(accountWithoutId));
        Mockito.when(accountDao.save(accountWithoutId)).thenReturn(updatedAccountEmail);
        Optional<AccountDto> resultDto = accountService.updateAccount(id, dataEmail);
        assertEquals(resultDto, Optional.of(updatedEmailDto));
    }

    @Test
    public void updatePassword() {
        Map dataPassword = new HashMap<>();
        dataPassword.put("password", "qwe");
        AccountDto dto = AccountDto.from(account);
        Mockito.when(accountDao.findAccountById(id)).thenReturn(Optional.ofNullable(accountWithoutId));
        Mockito.when(accountDao.save(accountWithoutId)).thenReturn(account);
        Optional<AccountDto> resultDto = accountService.updateAccount(id, dataPassword);
        assertEquals(resultDto, Optional.of(dto));
    }

    @Test
    public void updateNonExistingId() {
        Map dataPassword = new HashMap<>();
        dataPassword.put("password", "qwe");
        Mockito.when(accountDao.findAccountById(fakeId)).thenReturn(Optional.ofNullable(null));
        Optional<AccountDto> resultDto = accountService.updateAccount(fakeId, dataPassword);
        assertEquals(resultDto, Optional.empty());
    }

    @Test
    public void deleteAccount() {
        accountService.deleteAccount(id);
        Mockito.verify(accountDao, Mockito.times(1)).deleteAccountById(any());
    }

    @Test
    public void emailValidatorWithValidEmail() {
        assertTrue(accountService.validate("test@asd.com"));
    }

    @Test
    public void getAccountByUserName() {
        Mockito.when(accountDao.findAccountByUserName("TestUser")).thenReturn(Optional.ofNullable(account));
        assertTrue(accountService.getAccountByUserName("TestUser"));
    }

    @Test
    public void getAccountByEmail() {
        Mockito.when(accountDao.findAccountByEmailAddress("test@asd.com")).thenReturn(Optional.ofNullable(account));
        assertTrue(accountService.getAccountByEmail("test@asd.com"));
    }
}