package com.hanul.mypet.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private String writer;
    private Long postId;
    private LocalDateTime createdDate;
}
