package com.codecool.quokka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/v1/asset")
@ConfigurationProperties
public class AssetController {

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    @Value("${quokka.service.assetcache.address}${quokka.service.assetcache.endpoint}")
    private String url;

    @GetMapping("{type}")
    @PreAuthorize("hasRole('TRADER')")
    public List<String> getAssets(@PathVariable("type") String type, HttpServletRequest req) throws JsonProcessingException {
        String newUrl = url + type;
        String response = restTemplate.getForObject(newUrl, String.class);
        JsonNode jsonNode = mapper.readTree(response);
        JsonNode jsonNode1 = jsonNode.get(type);
        List<String> list = new ArrayList<>();
        for (Iterator<JsonNode> it = jsonNode1.elements(); it.hasNext(); ) {
            JsonNode item = it.next();
            list.add(item.asText());
        }
        return list;
    }

    @GetMapping("{assetType}/{assetSymbol}")
    @PreAuthorize("hasRole('TRADER')")
    public Map<String, Object> getAssetData(@PathVariable("assetType") String pathAssetType,
                                            @PathVariable("assetSymbol") String pathAssetSymbol) {
        String newUrl = url + pathAssetType + "/" + pathAssetSymbol;
        Map<String, Object> response = restTemplate.getForObject(newUrl, Map.class);
        return new HashMap<>(response);
    }
}
