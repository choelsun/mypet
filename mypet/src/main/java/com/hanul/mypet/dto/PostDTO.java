package com.hanul.mypet.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PostDTO {
    private Long id;              
    private String title;         
    private String content;       
    private String writer;        
    private LocalDateTime createdDate; 
    private LocalDateTime lastUpdatedDate;
}
