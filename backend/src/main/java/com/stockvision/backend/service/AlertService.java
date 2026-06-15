package com.stockvision.backend.service;
import com.stockvision.backend.entity.Alert;
import com.stockvision.backend.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository repository;

    public List<Alert> findAll() {
        return repository.findAll();
    }
}
