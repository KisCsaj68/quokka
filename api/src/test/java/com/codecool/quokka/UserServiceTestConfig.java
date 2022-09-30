package com.codecool.quokka;

import com.codecool.quokka.dao.account.AccountDao;
import com.codecool.quokka.service.account.AccountService;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;

@Profile("test")
@Configuration
public class UserServiceTestConfig {
    @Bean
    @Primary
    public AccountDao getUserDaoTest() {
        return Mockito.mock(AccountDao.class);
    }

    @Bean
    @Primary
    public AccountService getUserServiceTest() {
        return new AccountService(getUserDaoTest());
    }

}
