package com.codecool.quokka.service;

import com.codecool.quokka.dao.AssetDao;
import com.codecool.quokka.model.AssetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

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

    public AssetDto addAssetStock(Asset asset) {
        asset.setType(AssetType.STOCK);
        Asset newAsset = this.assetDao.add(asset);
        System.out.println(newAsset);
        return this.createDto(newAsset);
    }

    public AssetDto addAssetCrypto(Asset asset) {
        asset.setType(AssetType.STOCK);
        Asset newAsset = this.assetDao.add(asset);
        return this.createDto(newAsset);
    }

    public Set<String> getAllAsset() {
        return this.assetDao.getAll();
    }

    public Set<String> getAllStock() {
        return this.assetDao.getAllStock();
    }

    public Set<String> getAllCrypto() {
        return this.assetDao.getAllCrypto();
    }
}
