package com.example.zencash.dto;

import com.example.zencash.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TopTransactionResponse {
    private List<Transaction> newest;
    private List<Transaction> oldest;
}

