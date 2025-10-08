package com.example.cron.service.impl;

import com.example.cron.dto.BinanceAlphaResponse;
import com.example.cron.dto.BinanceRecord;
import com.example.cron.dto.TokenInfoDTO;
import com.example.cron.service.BinanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

@Service
public class BinanceServiceImpl implements BinanceService {
    private final List<BinanceRecord> records = new ArrayList<>();
    private final RestTemplate restTemplate = new RestTemplate();

    private final List<BinanceRecord> recordAlpha = new ArrayList<>();
    private final ObjectMapper objectMapper;
    private final Map<String, String> imageCache = new ConcurrentHashMap<>();


    public BinanceServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<BinanceRecord> fetchPrices(List<String> symbols) {
        List<BinanceRecord> results = new ArrayList<>();

        for (String symbol : symbols) {
            try {
                String url = "https://api.binance.com/api/v3/ticker/price?symbol=" + symbol.toUpperCase();
                var response = restTemplate.getForObject(url, BinanceResponse.class);

                if (response != null) {
                    BinanceRecord record = new BinanceRecord(
                            response.getSymbol(),
                            getFormattedPrice(response.getPrice()),
                            LocalDateTime.now()
                    );
                    // nếu muốn update thay vì add thì check ở đây
                    records.removeIf(r -> r.getSymbol().equalsIgnoreCase(response.getSymbol()));
                    records.add(record);
                    records.stream().sorted();
                    results.add(record);
                }
            } catch (Exception e) {
                System.err.println("Lỗi fetch symbol: " + symbol + " -> " + e.getMessage());
            }
        }
        return results;
    }


    @Override
    public List<BinanceRecord> getAll() {
        return records;
    }

    // record type cho JSON parse
    private class BinanceResponse {
        private String symbol;
        private String price;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }

    public String getFormattedPrice(String price) {
        try {
            double value = Double.parseDouble(price);
            DecimalFormat df = new DecimalFormat("#,###.##");
            return df.format(value);
        } catch (NumberFormatException e) {
            return price;
        }
    }

    @Override
    public List<TokenInfoDTO> allAlpha() {
        try {
            String url = "https://www.binance.com/bapi/defi/v1/public/alpha-trade/aggTicker24?dataType=aggregate";
            String json = getJsonFromGzip(url, restTemplate); // đọc và giải nén

            if (json == null) return Collections.emptyList();

            BinanceAlphaResponse response = objectMapper.readValue(json, BinanceAlphaResponse.class);

            if (response.getData() != null) {
                List<TokenInfoDTO> tokenAlpha = response.getData();
                LocalDateTime now = LocalDateTime.now();
                tokenAlpha.parallelStream().forEach(token -> {
                    token.setFetchedAt(now);
                    if (!imageCache.containsKey(token.getIconUrl())) {
                        String iconImage = fetchIcon(token.getTokenId(), token.getIconUrl());
                        imageCache.put(token.getIconUrl(), iconImage);
                        token.setIconImage(iconImage);
                    } else {
                        token.setIconImage(imageCache.get(token.getIconUrl()));
                    }
                });
                tokenAlpha.sort(Comparator.comparing(TokenInfoDTO::getSymbol, String.CASE_INSENSITIVE_ORDER));
                return tokenAlpha;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public String getJsonFromGzip(String url, RestTemplate restTemplate) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept-Encoding", "gzip"); // yêu cầu gzip
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> resp = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                byte[].class
        );

        byte[] body = resp.getBody();
        if (body == null) return null;

        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(body));
             BufferedReader bf = new BufferedReader(new InputStreamReader(gis))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    public String fetchIcon(String tokenId, String iconUrl) {
        return imageCache.computeIfAbsent(tokenId, id -> {
            try {

                return base64Encode(restTemplate.getForObject(iconUrl, byte[].class));
            } catch (Exception e) {
                return null;
            }
        });
    }

    public String base64Encode(byte[] value){
        return Base64.getEncoder().encodeToString(value);
    }

}
