package com.codecool.quokka.dao.assets.implementation;

import com.codecool.quokka.dao.assets.AssetDao;
import com.codecool.quokka.model.assets.AssetType;
import com.codecool.quokka.model.assets.Asset;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("assetDaoMem")
public class AssetDaoMem implements AssetDao {
//    private static AssetDaoMem instance = null;
    private final HashSet<Asset> assetData;

    public AssetDaoMem() {
        this.assetData = new HashSet<>();
    }

//    public static AssetDaoMem getInstance() {
//        if (instance == null) {
//            instance = new AssetDaoMem();
//        }
//        return instance;
//    }

    @Override
    public Asset add(Asset asset) {
        if (!this.assetData.contains(asset)) {
            asset.setId(this.assetData.size() + 1);
            this.assetData.add(asset);
        }
        return this.assetData.stream().filter(asset::equals).findFirst().orElse(null);
    }

    @Override
    public Set<Asset> getAll() {
        return Set.copyOf(this.assetData);
    }

    @Override
    public Set<Asset> getAllByType(AssetType assetType) {
        return this.assetData.stream()
                .filter(a -> a.getType().equals(assetType))
                .collect(Collectors.toSet());
    }
    @Override
    public Asset get(String symbol, AssetType assetType) {
        return this.assetData.stream()
                .filter(t -> assetType.equals(t.getType()))
                .filter(t -> symbol.equals(t.getSymbol()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Asset delete(String symbol, AssetType assetType) {
        Asset asset = this.get(symbol, assetType);
        this.assetData.remove(asset);
        return asset;
    }
}
