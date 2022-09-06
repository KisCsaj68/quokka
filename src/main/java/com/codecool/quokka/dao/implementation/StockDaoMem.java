package com.codecool.quokka.dao.implementation;

import com.codecool.quokka.dao.StockDao;
import com.codecool.quokka.service.Stock;

import java.util.HashSet;

public class StockDaoMem implements StockDao {
    private static StockDaoMem instance = null;
    private final HashSet<Stock> stockData;

    private StockDaoMem() {
        this.stockData = new HashSet<>();
    }

    private static StockDaoMem getInstance() {
        if (instance == null) {
            instance = new StockDaoMem();
        }
        return instance;
    }

    @Override
    public void add(Stock stock) {
        stock.setId(this.stockData.size() + 1);
        this.stockData.add(stock);

    }
}
