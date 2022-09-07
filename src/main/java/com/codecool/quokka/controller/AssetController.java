package com.codecool.quokka.controller;

import com.codecool.quokka.service.Stock;
import com.codecool.quokka.service.StockDto;
import com.codecool.quokka.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/stock")
@RestController
public class StockController {
    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public @ResponseBody StockDto addStock(@RequestBody Stock stock) {
        return this.stockService.addStock(stock);
    }
}
