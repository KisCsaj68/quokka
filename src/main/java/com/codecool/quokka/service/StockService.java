package com.codecool.quokka.service;

import com.codecool.quokka.dao.StockDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StockService {
    private final StockDao stockDao;

    @Autowired
    public StockService(StockDao stockDao) {
        this.stockDao = stockDao;
    }

    public StockDto addStock(Stock stock) {
        Stock newStock = this.stockDao.add(stock);
        String symbol = newStock.getSymbol();
        BigDecimal price = newStock.getPrice();
        return new StockDto(symbol, price);
    }
}
