package com.stockvision.backend.controller;

import com.stockvision.backend.service.IndicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/indices")
@RequiredArgsConstructor
public class IndicesController {

    private final IndicesService indicesService;

    @GetMapping
    public List<Map<String, Object>> getAllIndices() {
        return indicesService.getAllIndices();
    }
}
