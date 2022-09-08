package com.codecool.quokka.service;

import com.codecool.quokka.dao.user.UserDao;
import com.codecool.quokka.dao.user.implementation.UserDaoMem;
import com.codecool.quokka.model.user.User;
import com.codecool.quokka.model.user.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
class UserServiceTest {
    static User user;
    static UserDto dto;
    static UserDto updatedDto;
    static UUID id;
    static UUID fakeId;
    static HashMap<String, String> data;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @BeforeAll
    public static void setUp() {
         id = UUID.fromString("b462290f-4006-4d71-8a39-e956e245ede8");
         fakeId = UUID.fromString("b462290f-4008-4d71-8a39-e956e245ede8");
         user = new User("Test User", "test@asd.com", "TestUser", "asd", id);
         dto = new UserDto(user.getUserName(), user.getId(), user.getFullName(), user.getEmailAddress());
         updatedDto = new UserDto("User3456", user.getId(), user.getFullName(), user.getEmailAddress());
         data = new HashMap<>();
         data.put("fullName", "User3456");
    }


    @Test
    void addUser() {
        Mockito.when(userDao.addUser(user)).thenReturn(dto);
        UserDto resultDto = userService.addUser(user);
        assertEquals(dto,resultDto);
    }

    @Test
    void getAllUser() {
        Mockito.when(userDao.getAllUser()).thenReturn(Set.of(dto));
        Set<UserDto> dtos = userService.getAllUser();
        assertEquals(dtos, Set.of(dto));
    }

    @Test
    void getUser() {
        Mockito.when(userDao.getUser(id)).thenReturn(Optional.of(dto));
        Mockito.when(userDao.getUser(fakeId)).thenReturn(Optional.empty());
        Optional<UserDto> resultDto = userService.getUser(id);
        Optional<UserDto> fakeDto = userService.getUser(fakeId);
        assertEquals(resultDto, Optional.of(dto));
        assertEquals(fakeDto, Optional.empty());
    }

    @Test
    void updateUser() {
        Mockito.when(userDao.updateUser(id, data)).thenReturn(Optional.of(updatedDto));
        Mockito.when(userDao.updateUser(fakeId, data)).thenReturn(Optional.empty());
        Optional<UserDto> resultDto = userService.updateUser(id,data);
        Optional<UserDto> fakeResultDto = userService.updateUser(fakeId,data);
        assertEquals(resultDto, Optional.of(dto));
        assertEquals(fakeResultDto, Optional.empty());
    }

    @Test
    void deleteUser1() {
        userService.deleteUser(id);
        Mockito.verify(userDao, Mockito.times(1)).deleteUser(any());

    }




}