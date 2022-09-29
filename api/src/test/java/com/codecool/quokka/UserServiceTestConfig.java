package com.codecool.quokka;

import com.codecool.quokka.dao.user.UserDao;
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
    public UserDao getUserDaoTest() {
        return Mockito.mock(UserDao.class);
    }

    @Bean
    @Primary
    public UserService getUserServiceTest() {
        return new UserService(getUserDaoTest());
    }

}
