package com.hanul.mypet.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hanul.mypet.entity.CommentEntity;
import com.hanul.mypet.entity.PostEntity;
import com.hanul.mypet.security.dto.MemberAuthDTO;
import com.hanul.mypet.service.CommentService;
import com.hanul.mypet.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequiredArgsConstructor  // 생성자 자동 생성
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    // 댓글 생성
    @PostMapping("/create")
    public String createComment(
            @ModelAttribute CommentEntity comment, 
            @RequestParam("postId") Long postId, 
            Principal principal) {
        try {
            PostEntity post = postService.getPostById(postId);  // 게시글 조회
            comment.setPost(post);
            comment.setWriter(principal.getName());
            comment.setCreatedDate(LocalDateTime.now());

            commentService.createComment(comment);  // 댓글 저장
            log.info("댓글 작성 - 작성자: {}, 게시글 ID: {}", principal.getName(), postId);

            return "redirect:/post/detail/" + postId;
        } catch (Exception e) {
            log.error("댓글 작성 중 오류: ", e);
            return "redirect:/post/detail/" + postId + "?error";
        }
    }

    // 댓글 삭제
    @GetMapping("/delete/{id}")
    public String deleteComment(
            @PathVariable("id") Long commentId, 
            @RequestParam("postId") Long postId, 
            Principal principal) {
        log.info("댓글 삭제 요청 - 댓글 ID: {}, 사용자: {}", commentId, principal.getName());
        commentService.deleteComment(commentId);
        return "redirect:/post/detail/" + postId;
    }

    // 댓글 생성 API (JSON 응답)
    @PostMapping("/{postId}")
    public ResponseEntity<String> createCommentApi(
            @PathVariable Long postId, 
            @ModelAttribute CommentEntity comment, 
            @AuthenticationPrincipal MemberAuthDTO authDTO) {
        try {
            PostEntity post = postService.getPostById(postId);  // 게시글 조회
            comment.setPost(post);
            comment.setWriter(authDTO.getUsername());
            comment.setCreatedDate(LocalDateTime.now());

            commentService.createComment(comment);  // 댓글 저장
            return ResponseEntity.ok("댓글이 작성되었습니다.");
        } catch (Exception e) {
            log.error("댓글 작성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 작성 실패");
        }
    }
    
 // 댓글 수정 폼 이동
    @GetMapping("/edit/{id}")
    public String showCommentEditForm(@PathVariable Long id, 
                                      @AuthenticationPrincipal MemberAuthDTO authDTO, 
                                      Model model) {
        CommentEntity comment = commentService.getCommentById(id);
        if (!commentService.canAccessComment(id, authDTO)) {
            log.warn("댓글 수정 권한 없음 - 사용자: {}, 댓글 ID: {}", authDTO.getUsername(), id);
            return "redirect:/post/list";  // 권한 없을 시 목록으로 이동
        }
        model.addAttribute("comment", comment);
        return "comment/edit";  // 수정 폼 페이지로 이동
    }

    // 댓글 수정 처리
    @PostMapping("/edit/{id}")
    public String editComment(@PathVariable Long id,
                              @RequestParam("content") String content,
                              @AuthenticationPrincipal MemberAuthDTO authDTO) {
        CommentEntity comment = commentService.getCommentById(id);

        if (!comment.getWriter().equals(authDTO.getUsername()) &&
            authDTO.getAuthorities().stream().noneMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            log.warn("수정 권한 없음 - 사용자: {}, 댓글 ID: {}", authDTO.getUsername(), id);
            return "redirect:/post/detail/" + comment.getPost().getId();
        }

        comment.setContent(content);
        comment.setCreatedDate(LocalDateTime.now());
        commentService.updateComment(comment);

        log.info("댓글 수정 완료 - 댓글 ID: {}", id);
        return "redirect:/post/detail/" + comment.getPost().getId();
    }


}
