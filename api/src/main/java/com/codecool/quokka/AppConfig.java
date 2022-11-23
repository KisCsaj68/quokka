package com.codecool.quokka;

import com.codecool.quokka.dao.account.AccountDao;
import com.codecool.quokka.dao.assets.AssetDao;
import com.codecool.quokka.dao.assets.implementation.AssetDaoMem;
import com.codecool.quokka.jwt.JwtConfig;
import com.codecool.quokka.jwt.JwtSecretKey;
import com.codecool.quokka.service.assets.AssetService;
import com.codecool.quokka.utils.TokenEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class AppConfig {

    @Bean
    public AssetService getAssetService() {
        return new AssetService(getAssetDaoMem());
    }

    @Bean
    public AssetDao getAssetDaoMem() {
        return new AssetDaoMem();
    }

    @Bean
    public JwtConfig getJwtConfig() {
        return new JwtConfig();
    }





    @Bean
    public TokenEncoder getTokenEncoder() {
        return new TokenEncoder(getJwtConfig());
    }
}
