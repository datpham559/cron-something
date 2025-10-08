package com.example.cron.dto;

import java.util.List;

public class BinanceAlphaResponse {
    private String code;
    private String message;
    private List<TokenInfoDTO> data;

    // getters & setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<TokenInfoDTO> getData() {
        return data;
    }

    public void setData(List<TokenInfoDTO> data) {
        this.data = data;
    }
}
