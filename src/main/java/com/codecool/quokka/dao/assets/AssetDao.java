package com.codecool.quokka.dao.assets;

import com.codecool.quokka.model.assets.AssetType;
import com.codecool.quokka.model.assets.Asset;

import java.util.Set;

public interface AssetDao {
    Asset add(Asset asset);

    Set<Asset> getAll();
    Set<Asset> getAllByType(AssetType assetType);

    Asset get(String symbol, AssetType assetType);

    Asset delete(String symbol, AssetType assetType);

}