package com.codecool.quokka.controller;

import com.codecool.quokka.service.Asset;
import com.codecool.quokka.service.AssetDto;
import com.codecool.quokka.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1/asset")
@RestController
public class AssetController {
    private final AssetService assetService;

    @Autowired
    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping(path = "stock")
    public @ResponseBody AssetDto addAssetStock(@RequestBody Asset asset) {
        return this.assetService.addAssetStock(asset);
    }

    @PostMapping(path = "crypto")
    public @ResponseBody AssetDto addAssetCrypto(@RequestBody Asset asset) {
        return this.assetService.addAssetCrypto(asset);
    }

    @GetMapping
    public Set<String> getAllAsset() {
        return this.assetService.getAllAsset();
    }

    @GetMapping(path = "stock")
    public Set<String> getAllStock() {
        return this.assetService.getAllStock();
    }

    @GetMapping(path = "crypto")
    public Set<String> getAllCrypto() {
        return this.assetService.getAllCrypto();
    }
}
