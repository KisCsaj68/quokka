package com.codecool.quokka.service;

import com.codecool.quokka.dao.implementation.UserDaoMem;
import com.codecool.quokka.model.User;
import com.codecool.quokka.model.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
class UserServiceTest {
    static User user;
    static UserDto dto;
    static UUID id;
    static UUID fakeId;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDaoMem userDaoMem;

    @BeforeAll
    public static void setUp() {
         id = UUID.fromString("b462290f-4006-4d71-8a39-e956e245ede8");
         fakeId = UUID.fromString("b462290f-4008-4d71-8a39-e956e245ede8");
         user = new User("Test User", "test@asd.com", "TestUser", "asd", id);
         dto = new UserDto(user.getUserName(), user.getId(), user.getFullName(), user.getEmailAddress());
    }


    @Test
    void addUser() {
        Mockito.when(userDaoMem.addUser(user)).thenReturn(dto);
        UserDto resultDto = userService.addUser(user);
        assertEquals(dto,resultDto);
    }

    @Test
    void getAllUser() {
        Mockito.when(userDaoMem.getAllUser()).thenReturn(Set.of(dto));
        Set<UserDto> dtos = userService.getAllUser();
        assertEquals(dtos, Set.of(dto));
    }

    @Test
    void getUser() {
        Mockito.when(userDaoMem.getUser(id)).thenReturn(Optional.of(dto));
        Mockito.when(userDaoMem.getUser(fakeId)).thenReturn(Optional.empty());
        Optional<UserDto> resultDto = userService.getUser(id);
        Optional<UserDto> fakeDto = userService.getUser(fakeId);
        assertEquals(resultDto, Optional.of(dto));
        assertEquals(fakeDto, Optional.empty());
    }

    @Test
    void deleteUser() {

    }

    @Test
    void updateUser() {
    }




}