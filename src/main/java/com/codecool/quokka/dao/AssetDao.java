package com.codecool.quokka.dao;

import com.codecool.quokka.service.Stock;

import java.util.Set;

public interface StockDao {
    Stock add(Stock stock);

    Set<String> getAll();

    Stock get(int id);

    Stock get(String symbol);

}