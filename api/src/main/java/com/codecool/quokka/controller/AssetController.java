package com.codecool.quokka.controller;

import com.codecool.quokka.model.assets.AssetType;
import com.codecool.quokka.model.assets.Asset;
import com.codecool.quokka.model.assets.AssetDto;
import com.codecool.quokka.service.assets.AssetService;
import com.codecool.quokka.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RequestMapping("/api/v1/asset")
@RestController
@ConfigurationProperties
public class AssetController {
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    @Value("${quokka.service.assetcache.address}${quokka.service.assetcache.endpoint}")
    private String url;
    @GetMapping( "{type}")
    @PreAuthorize("hasRole('TRADER')")
    public List<String> getAssets(@PathVariable("type") String type) throws JsonProcessingException {
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
    public Map<String,Object> getAssetData(@PathVariable("assetType") String pathAssetType,
                                           @PathVariable("assetSymbol") String pathAssetSymbol){
        String newUrl = url + pathAssetType + "/" + pathAssetSymbol;
        Map<String, Object> response = restTemplate.getForObject(newUrl, Map.class);
        return new HashMap<>(response);
    }

//    @GetMapping
//    public Set<AssetDto> getAllAsset() {
//        return this.assetService.getAllAsset();
//    }
//
//    @GetMapping("{assetType}")
//    public Set<AssetDto> getAllAssetByType(@PathVariable("assetType") String pathAssetType) {
//        AssetType assetType = Utils.assetTypeParser(pathAssetType);
//        if (assetType == null) {
//            return null;
//        }
//        return this.assetService.getAllByType(assetType);
//    }
//
//    @PostMapping(path = "{assetType}")
//    public @ResponseBody AssetDto addAssetByType(@PathVariable("assetType") String pathAssetType,
//                                                 @RequestBody Asset asset) {
//        AssetType assetType = Utils.assetTypeParser(pathAssetType);
//        if (assetType == null) {
//            return null;
//        }
//        asset.setType(assetType);
//        return this.assetService.addAsset(asset);
//    }
//
//    @GetMapping(path = "{assetType}/{assetSymbol}")
//    public @ResponseBody AssetDto getAssetBySymbol(@PathVariable("assetType") String pathAssetType,
//                                                   @PathVariable("assetSymbol") String pathAssetSymbol) {
//        AssetType assetType = Utils.assetTypeParser(pathAssetType);
//        if (assetType == null) {
//            return null;
//        }
//        pathAssetSymbol = pathAssetSymbol.toUpperCase();
//        return this.assetService.getAssetBySymbol(pathAssetSymbol, assetType);
//    }
//
//    @DeleteMapping(path = "{assetType}/{assetSymbol}")
//    public @ResponseBody AssetDto deleteAssetBySymbol(@PathVariable("assetType") String pathAssetType,
//                                                      @PathVariable("assetSymbol") String pathAssetSymbol) {
//        AssetType assetType = Utils.assetTypeParser(pathAssetType);
//        if (assetType == null) {
//            return null;
//        }
//        pathAssetSymbol = pathAssetSymbol.toUpperCase();
//        return this.assetService.deleteAssetBySymbol(pathAssetSymbol, assetType);
//    }
}
