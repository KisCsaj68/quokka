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

@RequestMapping("/api/v1/asset")
@RestController
@ConfigurationProperties
public class AssetController {

    private static final Counter asset_request = Counter.build().namespace("quokka"). subsystem("api")
            .name("asset_request")
            .labelNames("operation")
            .help("total number of asset requests").register();

    public static final Histogram asset_request_time_duration = Histogram.build().namespace("quokka"). subsystem("api")
            .name("asset_request_time_duration")
            .labelNames("operation")
            .help("total elapsed time from request to response")

            .register();
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    @Value("${quokka.service.assetcache.address}${quokka.service.assetcache.endpoint}")
    private String url;
    @GetMapping( "{type}")
    @PreAuthorize("hasRole('TRADER')")
    public List<String> getAssets(@PathVariable("type") String type) throws JsonProcessingException {
        asset_request.labels("read_all").inc();
        Histogram.Timer timer = asset_request_time_duration.labels("read_all").startTimer();
        try {
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
        finally {
            timer.observeDuration();
        }
    }

    @GetMapping("{assetType}/{assetSymbol}")
    @PreAuthorize("hasRole('TRADER')")
    public Map<String,Object> getAssetData(@PathVariable("assetType") String pathAssetType,
                                           @PathVariable("assetSymbol") String pathAssetSymbol){
        asset_request.labels("read").inc();
        Histogram.Timer timer = asset_request_time_duration.labels("read").startTimer();
        try {
            String newUrl = url + pathAssetType + "/" + pathAssetSymbol;
            Map<String, Object> response = restTemplate.getForObject(newUrl, Map.class);
            return new HashMap<>(response);
        }
        finally {
            timer.observeDuration();
        }
    }

}
