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
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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


    @Autowired
    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        Account account = new Account("Test User" , "user"+UUID.randomUUID()+"@asd.com", "UserAdded" + UUID.randomUUID(), "asd");
        MvcResult response = mvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(account)))
                        .andReturn();

        String actualJson = response.getResponse().getContentAsString();
        System.out.println(actualJson);
        accountDto = new ObjectMapper().readValue(actualJson, AccountDto.class);


    }

    @After
    public void cleanUp() throws Exception {
        HashMap data = new HashMap<>();
        data.put("id", accountDto.getId());
        mvc.perform(delete("/api/v1/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper(). writeValueAsString(data)))
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

//    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testDeleteUserById() throws Exception {
        HashMap data = new HashMap<>();
        data.put("id", accountDto.getId());
        mvc.perform(delete("/api/v1/user").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(data)))
                .andExpect(status().isOk());
    }

    @Test
    public void testSpecificUserById() throws Exception {
        mvc.perform(get("/api/v1/user/" + accountDto.getId().toString()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
////    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
//    @Test
//    public void testUpdateUserById() throws Exception {
//        HashMap data = new HashMap<>();
//        data.put("fullName", "User3456");
//        mvc.perform(put("/api/v1/user/b462290f-4006-4d71-8a39-e956e245ede8")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(data)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.fullName", is("User3456")));
//    }

//    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testCreateNewUser() throws Exception {
        Account account = new Account("User Added22", "user222@asd.com", "UserAdded22", "asd");
        MvcResult response =  mvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(account)))
                .andExpect(status().isOk())
                .andReturn();
        AccountDto createdAccount;
        String actualJson = response.getResponse().getContentAsString();
        createdAccount = new ObjectMapper().readValue(actualJson, AccountDto.class);
        assertEquals(createdAccount.getUserName(), "UserAdded22");

    }
}