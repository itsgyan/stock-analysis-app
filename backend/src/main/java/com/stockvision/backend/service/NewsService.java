package com.stockvision.backend.service;
import com.stockvision.backend.entity.News;
import com.stockvision.backend.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository repository;

    public List<News> findAll() {
        return repository.findAll();
    }
}
