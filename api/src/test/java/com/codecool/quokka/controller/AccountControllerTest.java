package com.codecool.quokka.controller;

import com.codecool.quokka.QuokkaApplication;
import com.codecool.quokka.model.account.Account;
import com.codecool.quokka.model.account.AccountDto;
import com.codecool.quokka.model.role.AccountRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = QuokkaApplication.class)
@AutoConfigureMockMvc
public class AccountControllerTest {
    private AccountDto accountDto;
    private AccountRole role = new AccountRole("Trader", "Trading");

    @Autowired
    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        Account account = new Account("Test User", "user" + UUID.randomUUID() + "@asd.com", "UserAdded" + UUID.randomUUID(), "asd", true, true, true, true, new HashSet<>(Arrays.asList(role)));
        MvcResult response = mvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(account)))
                .andReturn();
        String actualJson = response.getResponse().getContentAsString();

        accountDto = new ObjectMapper().readValue(actualJson, AccountDto.class);
    }

    @After
    public void cleanUp() throws Exception {
        HashMap data = new HashMap<>();
        data.put("id", accountDto.getId());
        mvc.perform(delete("/api/v1/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(data)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUsersEndPoint()
            throws Exception {
        MvcResult response = mvc.perform(get("/api/v1/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String actualJson = response.getResponse().getContentAsString();
        assertTrue(actualJson.contains(accountDto.getId().toString()));
    }

    @Test
    public void testDeleteUserById() throws Exception {
        HashMap data = new HashMap<>();
        data.put("id", accountDto.getId());
        mvc.perform(delete("/api/v1/user").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(data)));
        mvc.perform(get("/api/v1/user/" + accountDto.getId().toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testSpecificUserById() throws Exception {
        mvc.perform(get("/api/v1/user/" + accountDto.getId().toString()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountDto.getId().toString()))
                .andExpect(jsonPath("$.userName").value(accountDto.getUserName()))
                .andExpect(jsonPath("$.fullName").value(accountDto.getFullName()))
                .andExpect(jsonPath("$.emailAddress").value(accountDto.getEmailAddress()));
    }

    @Test
    public void testUpdateUserById() throws Exception {
        HashMap data = new HashMap<>();
        String newUserName = "User3456";
        data.put("fullName", newUserName);
        mvc.perform(put("/api/v1/user/" + accountDto.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value(newUserName));
    }

    @Test
    public void testCreateNewUser() throws Exception {
        String userName = "UserAdded22";
        String fullName = "User Added22";
        String emailAddress = "user222@asd.com";
        String password = "asd";
        Account account = new Account(fullName, emailAddress, userName, password, true, true, true, true, new HashSet<>(Arrays.asList(role)));
        MvcResult response = mvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(account)))
                        .andExpect(status().isOk())
                        .andReturn();
        AccountDto createdAccount;
        String actualJson = response.getResponse().getContentAsString();
        createdAccount = new ObjectMapper().readValue(actualJson, AccountDto.class);
        assertEquals(createdAccount.getUserName(), userName);
    }

    @Test
    public void testOccupiedEmail() throws Exception {
        String userName = "UserAdded";
        String fullName = "User Added";
        String emailAddress = accountDto.getEmailAddress();
        String password = "asd";
        Account account = new Account(fullName, emailAddress, userName, password, true, true, true, true, new HashSet<>(Arrays.asList(role)));
        mvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(account)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testInvalidEmail() throws Exception {
        String userName = "User";
        String fullName = "User";
        String emailAddress = "@latte.com";
        String password = "asd";
        Account account = new Account(fullName, emailAddress, userName, password, true, true, true, true, new HashSet<>(Arrays.asList(role)));
        mvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(account)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testDuplicateUser() throws Exception {
        String userName = accountDto.getUserName();
        String fullName = "User FullName";
        String emailAddress = "coffee@latte.com";
        String password = "asd";
        Account account = new Account(fullName, emailAddress, userName, password, true, true, true, true, new HashSet<>(Arrays.asList(role)));
        mvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(account)))
                .andExpect(status().is4xxClientError());
    }
}