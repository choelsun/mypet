package com.hanul.mypet.service.Impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanul.mypet.dto.PostDTO;
import com.hanul.mypet.entity.PostEntity;
import com.hanul.mypet.repository.PostRepository;
import com.hanul.mypet.security.dto.MemberAuthDTO;
import com.hanul.mypet.service.PostService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PostEntity getPostById(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    @Override
    public boolean canAccessPost(Long postId, MemberAuthDTO authDTO) {
        PostEntity post = getPostById(postId);
        boolean isAdmin = authDTO.getAuthorities().stream()
                                 .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        return post.getWriter().equals(authDTO.getUsername()) || isAdmin;
    }

    @Override
    @Transactional
    public void createPost(PostDTO postDTO, MemberAuthDTO authDTO) {
        PostEntity postEntity = PostEntity.builder()
            .title(postDTO.getTitle())
            .content(postDTO.getContent())
            .writer(authDTO.getUsername())
            .build();
        postRepository.save(postEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostEntity> getAllPosts(String email) {
        return postRepository.findByWriter(email);
    }

    @Override
    @Transactional
    public void updatePost(Long id, PostEntity updatedPost) {
        PostEntity post = getPostById(id);
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setLastUpdatedDate(LocalDateTime.now());
        log.info("게시글 수정 완료 - 게시글 ID: {}", id);
    }

}
