package com.stockvision.backend.controller;
import com.stockvision.backend.entity.News;
import com.stockvision.backend.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService service;

    @GetMapping
    public List<News> getAll() {
        return service.findAll();
    }
}
