package com.hanul.mypet.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanul.mypet.entity.CommentEntity;
import com.hanul.mypet.repository.CommentRepository;
import com.hanul.mypet.security.dto.MemberAuthDTO;
import com.hanul.mypet.service.CommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;

	@Transactional
	public void createComment(CommentEntity comment) {
		log.info("댓글 생성 전 - 내용: {}, 작성자: {}", comment.getContent(), comment.getWriter());
		commentRepository.save(comment);
		log.info("댓글 생성 후 - {}", comment);
	}

	@Override
	public List<CommentEntity> getCommentsByPostId(Long postId) {
		log.info("댓글 조회 - 게시글 ID: {}", postId);
		return commentRepository.findByPostId(postId);
	}

	@Override
	@Transactional
	public void deleteComment(Long commentId) {
		log.info("댓글 삭제 - 댓글 ID: {}", commentId);
		commentRepository.deleteById(commentId);
	}

	@Override
	@Transactional
	public void updateComment(CommentEntity comment) {
	    commentRepository.save(comment);
	    log.info("댓글 수정: {}", comment);
	}


	@Override
	public boolean canAccessComment(Long id, MemberAuthDTO authDTO) {
		CommentEntity comment = getCommentById(id);
		return comment.getWriter().equals(authDTO.getUsername())
				|| authDTO.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
	}

	@Override
	public CommentEntity getCommentById(Long id) {
		return commentRepository.findById(id).orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
	}

}
