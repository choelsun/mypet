package com.hanul.mypet.repository;

import com.hanul.mypet.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    // 특정 게시글의 댓글 조회
    List<CommentEntity> findByPostId(Long postId);
}
