package com.hanul.mypet.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hanul.mypet.dto.PostDTO;
import com.hanul.mypet.entity.CommentEntity;
import com.hanul.mypet.entity.PostEntity;
import com.hanul.mypet.security.dto.MemberAuthDTO;
import com.hanul.mypet.service.CommentService;
import com.hanul.mypet.service.PostService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    // 게시글 목록 조회
    @GetMapping("/list")
    public String getAllPosts(Principal principal, Model model) {
        if (principal == null) {
            log.warn("비로그인 사용자가 게시글 목록에 접근 시도");
            return "redirect:/member/signin";
        }

        String writer = principal.getName();
        log.info("게시글 목록 조회 요청 - 사용자: {}", writer);

        List<PostEntity> posts = postService.getAllPosts(writer);
        model.addAttribute("posts", posts);
        return "post/list";
    }

    // 게시글 상세 조회 및 댓글 처리
    @GetMapping("/detail/{id}")
    public String getPostDetail(
        @PathVariable("id") Long postId, // 파라미터 명시
        Model model,
        @AuthenticationPrincipal MemberAuthDTO authDTO) {

        PostEntity post = postService.getPostById(postId);

        if (!post.getWriter().equals(authDTO.getUsername()) && !authDTO.getUsername().equals("admin")) {
            log.warn("접근 권한 없음 - 사용자: {}", authDTO.getUsername());
            return "redirect:/post/list";
        }

        model.addAttribute("post", post);
        model.addAttribute("newComment", new CommentEntity());
        return "post/detail";
    }



    // 게시글 작성 폼
    @GetMapping("/create")
    public String showCreateForm() {
        return "post/create";
    }

    // 게시글 작성 처리
    @PostMapping("/create")
    public String createPost(@ModelAttribute PostDTO postDTO,
                             @AuthenticationPrincipal MemberAuthDTO authDTO,
                             RedirectAttributes redirectAttributes) {
        try {
            postService.createPost(postDTO, authDTO);
            return "redirect:/post/create_success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "게시글 작성에 실패했습니다.");
            return "redirect:/post/create";
        }
    }

    // 게시글 수정 폼
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, 
                               @AuthenticationPrincipal MemberAuthDTO authDTO, 
                               Model model) {
        PostEntity post = postService.getPostById(id);
        if (!postService.canAccessPost(id, authDTO)) {
            log.warn("수정 권한 없음 - 사용자: {}, 게시글 ID: {}", authDTO.getUsername(), id);
            return "redirect:/post/list";
        }
        model.addAttribute("post", post);
        return "post/edit";
    }

    // 게시글 수정 처리
    @PostMapping("/detail/{id}")
    public String editPost(
        @PathVariable("id") Long postId,
        @ModelAttribute PostEntity postEntity,
        @AuthenticationPrincipal MemberAuthDTO authDTO) {

        if (!postService.canAccessPost(postId, authDTO)) {
            log.warn("수정 권한 없음 - 사용자: {}, 게시글 ID: {}", authDTO.getUsername(), postId);
            return "redirect:/post/list";
        }
        
        postService.updatePost(postId, postEntity);
        return "redirect:/post/detail/" + postId;
    }


    // 댓글 작성
    @PostMapping("/comments/{postId}/create")
    public String createComment(
            @PathVariable("postId") Long postId,  // 명시적으로 이름 지정
            @ModelAttribute("newComment") CommentEntity comment,
            @AuthenticationPrincipal MemberAuthDTO authDTO) {

        PostEntity post = postService.getPostById(postId);
        comment.setPost(post);
        comment.setWriter(authDTO.getUsername());
        commentService.createComment(comment);

        return "redirect:/post/detail/" + postId;
    }


    // 댓글 수정
    @PostMapping("/comments/{postId}/edit/{commentId}")
    public String editComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestParam("content") String content) {

        CommentEntity comment = commentService.getCommentById(commentId);
        comment.setContent(content);
        commentService.createComment(comment);

        return "redirect:/post/detail/" + postId;
    }

    // 댓글 삭제
    @GetMapping("/comments/{postId}/delete/{commentId}")
    public String deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId) {

        commentService.deleteComment(commentId);
        return "redirect:/post/detail/" + postId;
    }

    // 게시글 작성 성공 페이지
    @GetMapping("/create_success")
    public String showCreateSuccess() {
        return "post/create_success";
    }
}
