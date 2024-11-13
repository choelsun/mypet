package com.hanul.mypet.service;

import com.hanul.mypet.entity.CommentEntity;
import com.hanul.mypet.security.dto.MemberAuthDTO;

import java.util.List;

public interface CommentService {

    // 댓글 생성
    void createComment(CommentEntity comment);

    // 특정 게시글에 속한 댓글 조회
    List<CommentEntity> getCommentsByPostId(Long postId);

    // 댓글 삭제
    void deleteComment(Long commentId);
    
    // 댓글 수정
    void updateComment(CommentEntity comment);

    // 권한 체크 
    boolean canAccessComment(Long id, MemberAuthDTO authDTO);

    CommentEntity getCommentById(Long id);

}
