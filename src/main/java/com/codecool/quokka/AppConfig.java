package com.codecool.quokka;

import com.codecool.quokka.dao.implementation.UserDaoMem;
import com.codecool.quokka.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UserDaoMem getUserDaoMem(){
        return new UserDaoMem();
    }

    @Bean
    public UserService getUserService() {
        return new UserService(getUserDaoMem());
    }
}
