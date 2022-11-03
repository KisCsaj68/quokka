package com.codecool.quokka.service.account;

import com.codecool.quokka.dao.account.AccountDao;
import com.codecool.quokka.model.account.Account;
import com.codecool.quokka.model.account.AccountDto;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class AccountServiceTest {
    private static Account account;
    private static Account accountWithoutId;
    private static Account updatedAccountFullName;
    private static Account updatedAccountUEmail;
    private static AccountDto dto;
    private static AccountDto updatedFullNameDto;
    private static AccountDto updatedEmailDto;
    private static UUID id;
    private static UUID fakeId;
    private static HashMap<String, String> data;
    private static HashMap<String, String> dataEmail;
    private static HashMap<String, String> dataPassword;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDao accountDao;

    @BeforeClass
    public static void setUp() {
        id = UUID.fromString("b462290f-4006-4d71-8a39-e956e245ede8");
        fakeId = UUID.fromString("b462290f-4008-4d71-8a39-e956e245ede8");

        account = new Account("Test User", "test@asd.com", "TestUser", "asd", id);
        accountWithoutId = new Account("Test User", "test@asd.com", "TestUser", "asd");
        updatedAccountFullName = new Account("User3456", "test@asd.com", "TestUser", "asd", id);
        updatedAccountUEmail = new Account("Test User", "User3456@codecool.com", "TestUser", "asd", id);

        dto = AccountDto.from(account);
        updatedFullNameDto = new AccountDto(account.getUserName(), account.getId(), "User3456", account.getEmailAddress());
        updatedEmailDto = new AccountDto(account.getUserName(), account.getId(), account.getFullName(), "User3456@codecool.com");

        data = new HashMap<>();
        data.put("fullName", "User3456");
        dataEmail = new HashMap<>();
        dataEmail.put("emailAddress", "User3456@codecool.com");
        dataPassword = new HashMap<>();
        dataPassword.put("password", "qwe");
    }

    @Test
    public void addAccount() {
        Mockito.when(accountDao.saveAndFlush(accountWithoutId)).thenReturn(account);
        AccountDto resultDto = accountService.addAccount(accountWithoutId);
        assertEquals(dto, resultDto);
    }

    @Test
    public void getAllAccount() {
        Mockito.when(accountDao.findAll()).thenReturn((List.of(account)));
        Set<AccountDto> dtos = accountService.getAllAccount();
        assertEquals(dtos, Set.of(dto));
    }

    @Test
    public void getAccount() {
        Mockito.when(accountDao.findAccountById(id)).thenReturn(Optional.of(account));
        Mockito.when(accountDao.findAccountById(fakeId)).thenReturn(Optional.empty());
        Optional<AccountDto> resultDto = accountService.getAccount(id);
        Optional<AccountDto> fakeDto = accountService.getAccount(fakeId);
        assertEquals(resultDto, Optional.of(dto));
        assertEquals(fakeDto, Optional.empty());
    }

    @Test
    public void updateUserFullName() {
        Mockito.when(accountDao.findAccountById(id)).thenReturn(Optional.ofNullable(accountWithoutId));
        Mockito.when(accountDao.save(accountWithoutId)).thenReturn(updatedAccountFullName);
        Optional<AccountDto> resultDto = accountService.updateAccount(id, data);
        assertEquals(resultDto, Optional.of(updatedFullNameDto));
    }

    @Test
    public void updateEmail() {
        Mockito.when(accountDao.findAccountById(id)).thenReturn(Optional.ofNullable(accountWithoutId));
        Mockito.when(accountDao.save(accountWithoutId)).thenReturn(updatedAccountUEmail);
        Optional<AccountDto> resultDto = accountService.updateAccount(id, dataEmail);
        assertEquals(resultDto, Optional.of(updatedEmailDto));
    }

    @Test
    public void updatePassword() {
        Mockito.when(accountDao.findAccountById(id)).thenReturn(Optional.ofNullable(accountWithoutId));
        Mockito.when(accountDao.save(accountWithoutId)).thenReturn(account);
        Optional<AccountDto> resultDto = accountService.updateAccount(id, dataPassword);
        assertEquals(resultDto, Optional.of(dto));
    }

    @Test
    public void updateNonExistingId() {
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