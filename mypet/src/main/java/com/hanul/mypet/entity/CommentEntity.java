package com.hanul.mypet.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "comments")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String writer;
    private boolean isAdminReply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Column(updatable = false)  // 수정되지 않도록 설정
    private LocalDateTime createdDate;

    // 기본 생성자
    public CommentEntity() {
    }

    // 매개변수 생성자
    public CommentEntity(String content, String writer, boolean isAdminReply, PostEntity post) {
        this.content = content;
        this.writer = writer;
        this.isAdminReply = isAdminReply;
        this.post = post;
        this.createdDate = LocalDateTime.now();  // 생성 시점 설정
    }
}
