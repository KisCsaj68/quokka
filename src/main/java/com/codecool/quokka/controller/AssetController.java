package com.codecool.quokka.controller;

import com.codecool.quokka.model.assets.AssetType;
import com.codecool.quokka.model.assets.Asset;
import com.codecool.quokka.model.assets.AssetDto;
import com.codecool.quokka.service.assets.AssetService;
import com.codecool.quokka.utils.Utils;
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

    @GetMapping
    public Set<AssetDto> getAllAsset() {
        return this.assetService.getAllAsset();
    }

    @GetMapping("{assetType}")
    public Set<AssetDto> getAllAssetByType(@PathVariable("assetType") String pathAssetType) {
        AssetType assetType = Utils.assetTypeParser(pathAssetType);
        if (assetType == null) {
            return null;
        }
        return this.assetService.getAllByType(assetType);
    }

    @PostMapping(path = "{assetType}")
    public @ResponseBody AssetDto addAssetByType(@PathVariable("assetType") String pathAssetType,
                                                 @RequestBody Asset asset) {
        AssetType assetType = Utils.assetTypeParser(pathAssetType);
        if (assetType == null) {
            return null;
        }
        asset.setType(assetType);
        return this.assetService.addAsset(asset);
    }

    @GetMapping(path = "{assetType}/{assetSymbol}")
    public @ResponseBody AssetDto getAssetBySymbol(@PathVariable("assetType") String pathAssetType,
                                                   @PathVariable("assetSymbol") String pathAssetSymbol) {
        AssetType assetType = Utils.assetTypeParser(pathAssetType);
        if (assetType == null) {
            return null;
        }
        pathAssetSymbol = pathAssetSymbol.toUpperCase();
        return this.assetService.getAssetBySymbol(pathAssetSymbol, assetType);
    }

    @DeleteMapping(path = "{assetType}/{assetSymbol}")
    public @ResponseBody AssetDto deleteAssetBySymbol(@PathVariable("assetType") String pathAssetType,
                                                      @PathVariable("assetSymbol") String pathAssetSymbol) {
        AssetType assetType = Utils.assetTypeParser(pathAssetType);
        if (assetType == null) {
            return null;
        }
        pathAssetSymbol = pathAssetSymbol.toUpperCase();
        return this.assetService.deleteAssetBySymbol(pathAssetSymbol, assetType);
    }
}
