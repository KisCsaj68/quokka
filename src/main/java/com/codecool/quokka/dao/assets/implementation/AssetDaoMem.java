package com.codecool.quokka.dao.implementation;

import com.codecool.quokka.dao.AssetDao;
import com.codecool.quokka.model.AssetType;
import com.codecool.quokka.service.Asset;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("stockDaoMem")
public class AssetDaoMem implements AssetDao {
    private static AssetDaoMem instance = null;
    private final HashSet<Asset> assetData;

    private AssetDaoMem() {
        this.assetData = new HashSet<>();
    }

    private static AssetDaoMem getInstance() {
        if (instance == null) {
            instance = new AssetDaoMem();
        }
        return instance;
    }

    @Override
    public Asset add(Asset asset) {
        if (!this.assetData.contains(asset)) {
            asset.setId(this.assetData.size() + 1);
            this.assetData.add(asset);
        }
        return this.assetData.stream().filter(asset::equals).findFirst().orElse(null);
    }

    @Override
    public Set<String> getAll() {
        return this.assetData.stream().map(Asset::getSymbol).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAllStock() {
        return this.assetData.stream().filter(a -> a.getType() == AssetType.STOCK).map(Asset::getSymbol).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAllCrypto() {
        return this.assetData.stream().filter(a -> a.getType() == AssetType.CRYPTO).map(Asset::getSymbol).collect(Collectors.toSet());
    }

    @Override
    public Asset get(int id, String type) {
        AssetType assetType = AssetType.valueOf(type.toUpperCase());
        return this.assetData.stream()
                .filter(t -> assetType.equals(t.getType()))
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Asset get(String symbol, String type) {
        AssetType assetType = AssetType.valueOf(type.toUpperCase());
        return this.assetData.stream()
                .filter(t -> assetType.equals(t.getType()))
                .filter(t -> symbol.equals(t.getSymbol()))
                .findFirst()
                .orElse(null);
    }
}
