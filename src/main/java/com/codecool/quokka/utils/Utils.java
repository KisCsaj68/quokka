package com.codecool.quokka.utils;

import com.codecool.quokka.model.assets.AssetType;

public class Utils {

    public static int numParser(String string) {
        if (string == null || string.equals("")) {
            return -1;
        }

        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    public static AssetType assetTypeParser(String assetType) {
        try {
            return AssetType.valueOf(assetType.toUpperCase());
        } catch (IllegalArgumentException i) {
            return null;
        }
    }
}
