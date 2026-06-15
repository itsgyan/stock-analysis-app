package com.stockvision.backend.controller;
import com.stockvision.backend.entity.Transaction;
import com.stockvision.backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService service;

    @GetMapping
    public List<Transaction> getAll() {
        return service.findAll();
    }
}
