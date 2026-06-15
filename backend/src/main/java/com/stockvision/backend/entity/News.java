package com.stockvision.backend.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "news")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length = 1000)
    private String url;
    private String source;
    private LocalDateTime publishedAt;
    @ManyToOne
    @JoinColumn(name = "related_stock_id")
    private Stock relatedStock;
}
