package com.stockvision.backend.service;
import com.stockvision.backend.entity.Transaction;
import com.stockvision.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;

    public List<Transaction> findAll() {
        return repository.findAll();
    }
}
