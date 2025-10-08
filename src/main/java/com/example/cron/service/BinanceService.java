package com.example.cron.service;

import com.example.cron.dto.BinanceRecord;
import com.example.cron.dto.TokenInfoDTO;

import java.util.List;

public interface BinanceService {
    List<BinanceRecord> fetchPrices(List<String> symbols);

    List<BinanceRecord> getAll();

    List<TokenInfoDTO> allAlpha();

}
