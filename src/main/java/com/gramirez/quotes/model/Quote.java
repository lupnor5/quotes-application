package com.gramirez.quotes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "quotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quote {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 1000)
    private String text;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @Column (name = "created_at")
    private LocalDateTime createdAt;

    @Column (name = "updated_ad")
    private LocalDateTime updatedAt;

}
