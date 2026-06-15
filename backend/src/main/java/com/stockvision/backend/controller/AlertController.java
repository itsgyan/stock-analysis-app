package com.stockvision.backend.controller;
import com.stockvision.backend.entity.Alert;
import com.stockvision.backend.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {
    private final AlertService service;

    @GetMapping
    public List<Alert> getAll() {
        return service.findAll();
    }
}
