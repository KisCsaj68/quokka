package com.codecool.quokka;

import com.codecool.quokka.dao.implementation.UserDaoMem;
import com.codecool.quokka.service.UserService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class UserServiceTestConfig {
    @Bean
    @Primary
    public UserDaoMem getUserServiceTest() {
        return Mockito.mock(UserDaoMem.class);
    }
}
