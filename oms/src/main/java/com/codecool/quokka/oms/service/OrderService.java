package com.codecool.quokka.oms.service;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

    // Send open order to persister via RMQ
    // Ask the actual price from assetcache
    // Fill the price to the order and update the order in DB.
    // Create position, send to persister RMQ
    // Store both Entity in-memory
}
