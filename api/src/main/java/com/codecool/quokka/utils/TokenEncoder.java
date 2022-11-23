package com.codecool.quokka.utils;

import com.codecool.quokka.dao.account.AccountDao;
import com.codecool.quokka.jwt.JwtConfig;
import com.codecool.quokka.model.account.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.SecretKey;
import java.util.Optional;
import java.util.UUID;

public class TokenEncoder {
    private final JwtConfig jwtConfig;
    private  final AccountDao accountDao;
    private  final SecretKey secretKey;

    @Autowired
    public TokenEncoder(JwtConfig jwtConfig, AccountDao accountDao, SecretKey secretKey) {
        this.jwtConfig = jwtConfig;
        this.accountDao = accountDao;
        this.secretKey = secretKey;
    }

//    @PostConstruct
//    public void initDao() {
//        this.accountDao = dao;
//    }

    public  UUID getUserId(String token) {
        String userToke = token.replace(jwtConfig.getTokenPrefix(), "");
        Jws<Claims> claimJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(userToke);
        Claims body = claimJws.getBody();
        String username = body.getSubject();
        Optional<Account> userAccount = accountDao.findAccountByUserName(username);
        return userAccount.get().getId();
    }
}
