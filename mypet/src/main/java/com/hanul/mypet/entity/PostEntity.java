package com.hanul.mypet.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table(name = "post")
@Entity
@Data
@NoArgsConstructor(force = true)  // 기본 생성자 강제 생성
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String writer;

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime lastUpdatedDate;

    // 댓글 목록과 연관 관계 설정 (일대다 관계)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CommentEntity> comments = new ArrayList<>();

    @Builder
    public PostEntity(String title, String content, String writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.createdDate = LocalDateTime.now();
        this.lastUpdatedDate = LocalDateTime.now();
    }
}
