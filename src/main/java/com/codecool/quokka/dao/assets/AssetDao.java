package com.codecool.quokka.dao;

import com.codecool.quokka.service.Asset;

import java.util.Set;

public interface AssetDao {
    Asset add(Asset asset);

    Set<String> getAll();
    Set<String> getAllStock();
    Set<String> getAllCrypto();

    Asset get(int id, String type);
    Asset get(String symbol, String type);

}