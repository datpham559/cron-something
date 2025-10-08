package com.example.cron.controller;

import com.example.cron.common.Constants;
import com.example.cron.service.BinanceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BinanceController {
    private final BinanceService binanceService;

    public BinanceController(BinanceService binanceService) {
        this.binanceService = binanceService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("prices", binanceService.getAll());
        return "index";
    }

    @PostMapping("/add")
    public String addSymbol(@RequestParam String symbol) {
        List<String> defaultSymbol = new ArrayList<>(Constants.defaultToken);
        if (!defaultSymbol.contains(symbol.toUpperCase()))
            defaultSymbol.add(symbol.toUpperCase());
        binanceService.fetchPrices(defaultSymbol);
        return "redirect:/";
    }

    @GetMapping("/reloadandupdate")
    public String reloadAndUpdate() {
        binanceService.fetchPrices(Constants.defaultToken);
        return "redirect:/";
    }

    @GetMapping("/alpha")
    public String allAlpha(Model model) {
        model.addAttribute("prices", binanceService.allAlpha());
        return "alpha";
    }
}
