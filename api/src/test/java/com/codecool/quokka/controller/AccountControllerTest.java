package com.codecool.quokka.controller;

import com.codecool.quokka.QuokkaApplication;
import com.codecool.quokka.model.account.Account;
import com.codecool.quokka.model.account.AccountDto;
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

import java.util.*;

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
    private Account account;
    @Autowired
    private MockMvc mvc;

    private String jwtHeader;


    @Before
    public void setUp() throws Exception {
        account = new Account("Test User", "user" + UUID.randomUUID() + "@asd.com", "UserAdded" + UUID.randomUUID(), "asd");
        MvcResult response = mvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(account)))
                .andExpect(status().isOk())
                .andReturn();
        String actualJson = response.getResponse().getContentAsString();
        accountDto = new ObjectMapper().readValue(actualJson, AccountDto.class);
        Map<String, String> credentials = Map.of("user_name", account.getUserName(), "password", account.getPassword());
        response = mvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andReturn();
        jwtHeader = response.getResponse().getHeader("Authorization");
    }

    @After
    public void cleanUp() throws Exception {
        Map<String, UUID> data = Map.of("id", accountDto.getId());
        mvc.perform(delete("/api/v1/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(data))
                .header("Authorization", jwtHeader));
//                .andExpect(status().isOk());
    }

    @Test
    public void testGetUsersEndPoint()
            throws Exception {
        MvcResult response = mvc.perform(get("/api/v1/user")
                        .header("Authorization", jwtHeader))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String actualJson = response.getResponse().getContentAsString();
        assertTrue(actualJson.contains(accountDto.getId().toString()));
    }

    @Test
    public void testGetUsersEndPointWithoutToken()
            throws Exception {
        mvc.perform(get("/api/v1/user"))
                .andExpect(status().is(403));
    }

    @Test
    public void testDeleteUserById() throws Exception {
        Map<String, UUID> data = Map.of("id", accountDto.getId());
        mvc.perform(delete("/api/v1/user").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(data))
                        .header("Authorization", jwtHeader))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/user/" + accountDto.getId().toString())
                        .header("Authorization", jwtHeader))
                .andExpect(status().is(404));
    }

    @Test
    public void testDeleteUserByIdWithWrongId() throws Exception {
        String randomUUID = "c328a57e-9f3e-45bd-ab8d-11782a9885e0";
        Map<String, String> data = Map.of("id", randomUUID);
        mvc.perform(delete("/api/v1/user").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(data))
                        .header("Authorization", jwtHeader))
                .andExpect(status().is(404));
        mvc.perform(get("/api/v1/user/" + accountDto.getId().toString())
                        .header("Authorization", jwtHeader))
                .andExpect(status().is(200));
    }

    @Test
    public void testDeleteUserByIdWithoutToken() throws Exception {
        Map<String, UUID> data = Map.of("id", accountDto.getId());
        mvc.perform(delete("/api/v1/user").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(data)))
                .andExpect(status().is(403));
    }

    @Test
    public void testSpecificUserById() throws Exception {
        mvc.perform(get("/api/v1/user/" + accountDto.getId().toString())
                        .header("Authorization", jwtHeader))
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
        data.put("full_name", newUserName);
        mvc.perform(put("/api/v1/user/" + accountDto.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwtHeader)
                        .content(new ObjectMapper().writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value(newUserName));
    }

    @Test
    public void testUpdateUserByIdWithoutToken() throws Exception {
        String newUserName = "User3456";
        Map<String, String> data = Map.of("full_name", newUserName);
        mvc.perform(put("/api/v1/user/" + accountDto.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(data)))
                .andExpect(status().is(403));
    }

    @Test
    public void testUpdateUserByIdWithWrongId() throws Exception {
        String newUserName = "User3456";
        Map<String, String> data = Map.of("full_name", newUserName);
        String randomUUID = "c328a57e-9f3e-45bd-ab8d-11782a9885e0";

        mvc.perform(put("/api/v1/user/" + randomUUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwtHeader)
                        .content(new ObjectMapper().writeValueAsString(data)))
                .andExpect(status().is(404));
    }

    @Test
    public void testCreateNewUser() throws Exception {
        String userName = "UserAdded22";
        String fullName = "User Added22";
        String emailAddress = "user222@asd.com";
        String password = "asd";
        Account account = new Account(fullName, emailAddress, userName, password);
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
        Account account = new Account(fullName, emailAddress, userName, password);
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
        Account account = new Account(fullName, emailAddress, userName, password);
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
        Account account = new Account(fullName, emailAddress, userName, password);
        mvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(account)))
                .andExpect(status().is4xxClientError());
    }
}