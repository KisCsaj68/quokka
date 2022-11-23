package com.codecool.quokka.controller;

import com.codecool.quokka.dao.account.AccountDao;
import com.codecool.quokka.jwt.JwtConfig;
import com.codecool.quokka.model.OrderDto;
import com.codecool.quokka.model.account.Account;
import com.codecool.quokka.model.assets.AssetType;
import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.utils.TokenEncoder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@ConfigurationProperties
public class FinController {
    public static final UUID ACCOUNT_ID = UUID.fromString("a1521309-f533-460a-a9fc-3028b0efc79b");
    private RestTemplate restTemplate = new RestTemplate();

//    private final JwtConfig jwtConfig;
//    private final AccountDao accountDao;
//
//    private final SecretKey secretKey;
    private final TokenEncoder tokenEncoder;

    @Autowired
    public FinController(TokenEncoder tokenEncoder) {
        this.tokenEncoder = tokenEncoder;
    }

    @Value("${quokka.service.oms.address}${quokka.service.oms.endpoint}")
    private String url;

    @PostMapping(path = "stock")
    @PreAuthorize("hasRole('TRADER')")
    public ResponseEntity createNewStockOrder(@RequestBody OrderDto data, @RequestHeader("Authorization") String token) {
//        String userToke = token.replace(jwtConfig.getTokenPrefix(), "");
//        Jws<Claims> claimJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(userToke);
//        Claims body = claimJws.getBody();
//        String username = body.getSubject();
//        Optional<Account> userAccount = accountDao.findAccountByUserName(username);
        Orders actualOrders = data.toEntity(tokenEncoder.getUserId(token));
        System.out.println(actualOrders);
        actualOrders.setAssetType(AssetType.STOCK);

        //post request to OMS / marketOrder
        HttpEntity<Orders> request = new HttpEntity<>(actualOrders);
        ResponseEntity orderCreateResponse = restTemplate.postForObject(url, request, ResponseEntity.class);
        return orderCreateResponse;
    }

    @PostMapping(path = "crypto")
    @PreAuthorize("hasRole('TRADER')")
    public ResponseEntity createNewCryptoOrder(@RequestBody OrderDto data) {
        Orders actualOrders = data.toEntity(ACCOUNT_ID);
        actualOrders.setAssetType(AssetType.CRYPTO);

        //post request to OMS / marketOrder
        HttpEntity<Orders> request = new HttpEntity<>(actualOrders);
        ResponseEntity orderCreateResponse = restTemplate.postForObject(url, request, ResponseEntity.class);
        return orderCreateResponse;
    }
}
