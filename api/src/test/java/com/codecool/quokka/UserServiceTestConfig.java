package com.codecool.quokka;

import com.codecool.quokka.dao.user.implementation.UserDaoMem;
import com.codecool.quokka.service.user.UserService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;

@Profile("test")
@Configuration
public class UserServiceTestConfig {
    @Bean
    @Primary
    public UserDaoMem getUserDaoMemTest() {
        return Mockito.mock(UserDaoMem.class);
    }

    @Bean
    @Primary
    public UserService getUserServiceTest() {
        return new UserService(getUserDaoMemTest());
    }

}
