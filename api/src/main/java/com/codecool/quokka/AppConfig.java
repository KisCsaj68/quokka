package com.codecool.quokka;

import com.codecool.quokka.dao.assets.AssetDao;
import com.codecool.quokka.dao.assets.implementation.AssetDaoMem;
import com.codecool.quokka.dao.user.UserDao;
import com.codecool.quokka.service.user.UserService;
import com.codecool.quokka.service.assets.AssetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

//    @Bean
//    public UserDaoMem getUserDaoMem() {
//        return new UserDaoMem();
//    }

//    @Bean
//    public UserService getUserService() {
//        return new UserService(new UserDao());
//    }

    @Bean
    public AssetService getAssetService() {
        return new AssetService(getAssetDaoMem());
    }

    @Bean
    public AssetDao getAssetDaoMem() {
        return new AssetDaoMem();
    }
}
