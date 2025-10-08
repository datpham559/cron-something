package com.example.cron.scheduler;

import com.example.cron.common.Constants;
import com.example.cron.config.ObjectMapperInstance;
import com.example.cron.service.BinanceService;
import com.example.cron.socket.BinanceSocketService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BinanceScheduler {
    private final BinanceService binanceService;
    private final BinanceSocketService binanceSocketService;
    private final SimpMessagingTemplate messagingTemplate;

    public BinanceScheduler(BinanceService binanceService, BinanceSocketService binanceSocketService, SimpMessagingTemplate messagingTemplate) {
        this.binanceService = binanceService;
        this.binanceSocketService = binanceSocketService;
        this.messagingTemplate = messagingTemplate;
    }

    // Cron chạy mỗi 1 phút lấy giá BTCUSDT
//    @Scheduled(cron = "0 */1 * * * *")
//    public void fetchBTCPrice() {
//        binanceService.fetchPrices(Constants.defaultToken);
//        System.out.println("Cron fetched BTC price");
//    }
//
//    @Scheduled(fixedRate = 10000)
//    public void broadcast() {
//        System.out.println("broadcast is running");
//        String json = ObjectMapperInstance.ObjectToJson(binanceService.fetchPrices(Constants.defaultToken)) ;
//        if (!json.isEmpty()) {
//            System.out.println("fetch success");
//            messagingTemplate.convertAndSend("/topic/prices", json);
//        }
//    }
}
