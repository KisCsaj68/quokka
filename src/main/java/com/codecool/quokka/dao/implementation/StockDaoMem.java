package com.codecool.quokka.dao.implementation;

import com.codecool.quokka.dao.StockDao;
import com.codecool.quokka.service.Stock;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository("stockDaoMem")
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
    public Stock add(Stock stock) {
        stock.setId(this.stockData.size() + 1);
        this.stockData.add(stock);
        return stock;
    }
    public Set<String> getAll() {
        return this.stockData.stream()
                .map(Stock::getSymbol)
                .collect(Collectors.toSet());
    }
}
