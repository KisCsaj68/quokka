package com.codecool.quokka.service.assets;

import com.codecool.quokka.dao.assets.AssetDao;
import com.codecool.quokka.model.AssetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AssetService {
    private final AssetDao assetDao;

    @Autowired
    public AssetService(AssetDao assetDao) {
        this.assetDao = assetDao;
    }

    private AssetDto createDto(Asset asset) {
        String symbol = asset.getSymbol();
        BigDecimal price = asset.getPrice();
        AssetType type = asset.getType();
        return new AssetDto(symbol, price, type);
    }

    public AssetDto addAsset(Asset asset) {
        Asset newAsset = this.assetDao.add(asset);
        return this.createDto(newAsset);
    }

    public Set<AssetDto> getAllAsset() {
        return this.assetDao.getAll().stream()
                .map(e -> new AssetDto(e.getSymbol(), e.getPrice(), e.getType()))
                .collect(Collectors.toSet());
    }

    public Set<AssetDto> getAllByType(AssetType assetType) {
        return this.assetDao
                .getAllByType(assetType)
                .stream()
                .map(e -> new AssetDto(e.getSymbol(), e.getPrice(), e.getType()))
                .collect(Collectors.toSet());
    }

    public AssetDto getAssetBySymbol(String symbol, AssetType assetType) {
        Asset asset = this.assetDao.get(symbol, assetType);
        return this.createDto(asset);
    }

    public AssetDto deleteAssetBySymbol(String symbol, AssetType assetType) {
        Asset asset = this.assetDao.delete(symbol, assetType);
        return this.createDto(asset);

    }
}
