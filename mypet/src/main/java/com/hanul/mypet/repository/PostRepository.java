package com.hanul.mypet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanul.mypet.entity.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByWriter(String writer);  // 작성자별 게시글 조회
}
