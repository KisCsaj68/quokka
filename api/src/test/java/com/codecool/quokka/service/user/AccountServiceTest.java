package com.codecool.quokka.service.user;

import com.codecool.quokka.dao.user.UserDao;
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

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class AccountServiceTest {
    static Account account;
    static AccountDto dto;
    static AccountDto updatedDto;
    static UUID id;
    static UUID fakeId;
    static HashMap<String, String> data;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @BeforeClass
    public static void setUp() {
         id = UUID.fromString("b462290f-4006-4d71-8a39-e956e245ede8");
         fakeId = UUID.fromString("b462290f-4008-4d71-8a39-e956e245ede8");
         account = new Account("Test User", "test@asd.com", "TestUser", "asd", id);
         dto = AccountDto.from(account);
         updatedDto = new AccountDto("User3456", account.getId(), account.getFullName(), account.getEmailAddress());
         data = new HashMap<>();
         data.put("fullName", "User3456");
    }


    @Test
    public void addUser() {
        Mockito.when(userDao.save(account)).thenReturn(account);
        AccountDto resultDto = userService.addUser(account);
        assertEquals(dto,resultDto);
    }

    @Test
    public void getAllUser() {
        Mockito.when(userDao.findAll()).thenReturn(Set.of(account));
        Set<AccountDto> dtos = userService.getAllUser();
        assertEquals(dtos, Set.of(dto));
    }

    @Test
    public void getUser() {
        Mockito.when(userDao.findAccountByUserId(id)).thenReturn(Optional.of(account));
        Mockito.when(userDao.findAccountByUserId(fakeId)).thenReturn(Optional.empty());
        Optional<AccountDto> resultDto = userService.getUser(id);
        Optional<AccountDto> fakeDto = userService.getUser(fakeId);
        assertEquals(resultDto, Optional.of(dto));
        assertEquals(fakeDto, Optional.empty());
    }

//    @Test
//    public void updateUser() {
//        Mockito.when(userDao.updateUser(id, data)).thenReturn(Optional.of(updatedDto));
//        Mockito.when(userDao.updateUser(fakeId, data)).thenReturn(Optional.empty());
//        Optional<UserDto> resultDto = userService.updateUser(id,data);
//        Optional<UserDto> fakeResultDto = userService.updateUser(fakeId,data);
//        assertEquals(resultDto, Optional.of(dto));
//        assertEquals(fakeResultDto, Optional.empty());
//    }

    @Test
    public void deleteUser() {
        userService.deleteUser(id);
        Mockito.verify(userDao, Mockito.times(1)).deleteAccountByUserId(any());

    }




}