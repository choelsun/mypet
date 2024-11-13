package com.hanul.mypet.service;

import java.util.List;

import com.hanul.mypet.dto.PostDTO;
import com.hanul.mypet.entity.PostEntity;
import com.hanul.mypet.security.dto.MemberAuthDTO;

public interface PostService {

    PostEntity getPostById(Long postId); // 게시글 상세 조회

    boolean canAccessPost(Long postId, MemberAuthDTO authDTO); // 접근 권한 체크

    void createPost(PostDTO postDTO, MemberAuthDTO authDTO); // 게시글 작성
    
    List<PostEntity> getAllPosts(String email);
    
    void updatePost(Long id, PostEntity updatedPost);
    
}
