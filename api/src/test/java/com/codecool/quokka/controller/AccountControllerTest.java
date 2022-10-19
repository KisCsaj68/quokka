package com.codecool.quokka.controller;

import com.codecool.quokka.QuokkaApplication;
import com.codecool.quokka.model.account.Account;
import com.codecool.quokka.model.account.AccountDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = QuokkaApplication.class)
@AutoConfigureMockMvc
public class AccountControllerTest {



    @Autowired
    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        Account account = new Account("Test User", "user@asd.com", "UserAdded", "asd", UUID.fromString("b462290f-4006-4d71-8a39-e956e245ede8"));
        mvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(account)));

    }

    @After
    public void cleanUp() throws Exception {
        HashMap data = new HashMap<>();
        data.put("id", "b462290f-4006-4d71-8a39-e956e245ede8");
        mvc.perform(delete("/api/v1/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper(). writeValueAsString(data))).andExpect(status().isOk());
    }

    @Test
    public void testUsersEndPoint()
            throws Exception {
        mvc.perform(get("/api/v1/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

//    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testDeleteUserById() throws Exception {
        HashMap data = new HashMap<>();
        data.put("id", "b462290f-4006-4d71-8a39-e956e245ede8");
        mvc.perform(delete("/api/v1/user").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(data))).andExpect(status().isOk());
    }

//    @Test
//    public void testSpecificUserById() throws Exception {
//        mvc.perform(get("/api/v1/user/b462290f-4006-4d71-8a39-e956e245ede8"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.fullName", is("Test User")))
//                .andExpect(jsonPath("$.id", is("b462290f-4006-4d71-8a39-e956e245ede8")));
//    }
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
        mvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(account)))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.fullName", is("User Added")));
    }
}