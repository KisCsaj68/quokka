package com.codecool.quokka.config;

import com.codecool.quokka.dao.user.implementation.UserDaoMem;
import com.codecool.quokka.service.user.UserService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
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
