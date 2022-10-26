package com.base.community.controller;


import com.base.community.dto.BoardCommentDto;
import com.base.community.dto.BoardDto;
import com.base.community.model.entity.Member;
import com.base.community.security.TokenProvider;
import com.base.community.service.BoardService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/boards")
public class BoardController {
    private BoardService boardService;
    private final TokenProvider tokenProvider;

    // 게시글 전체보기
    @GetMapping()
    public ResponseEntity boardList(@RequestParam(required = false) String category,
                                    @RequestParam(defaultValue = "1") int page){

         return ResponseEntity.ok(boardService.boardList(category, page-1));
   }

    // 게시글 작성
    @PostMapping()
    public ResponseEntity<Long> write(@RequestHeader(name = "auth-token") String token
    , @RequestBody BoardDto form){

        return ResponseEntity.ok(boardService.writeBoard(form, tokenProvider.getUser(token).getId()));
    }

    //게시글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<?> modify(@RequestHeader(name = "auth-token") String token
            , @RequestBody BoardDto form, @PathVariable Long boardId){

        return ResponseEntity.ok(boardService.modifyBoard(form, tokenProvider.getUser(token).getId(),boardId));
    }

    //게시글 삭제
    @DeleteMapping("/{boardId}")
    public void delete(@RequestHeader(name = "auth-token") String token
            , @PathVariable Long boardId){
     boardService.deleteBoard(tokenProvider.getUser(token).getId(), boardId);

    }

    // 댓글 작성
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<Long> writeComment(@RequestHeader(name = "auth-token") String token,
                                             @RequestBody BoardCommentDto form, @PathVariable Long boardId){
        return ResponseEntity.ok(boardService.writeBoardComment(form, tokenProvider.getUser(token).getId(), boardId));
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Long> modifyComment(@RequestHeader(name = "auth-token") String token,
                                              @RequestBody BoardCommentDto form, @PathVariable Long commentId){
        return ResponseEntity.ok(boardService.modifyComment(form, tokenProvider.getUser(token).getId() ,commentId));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@RequestHeader(name = "auth-token") String token
            , @PathVariable Long commentId){
        boardService.deleteComment(tokenProvider.getUser(token).getId() , commentId);

    }


    // 좋아요 | 좋아요 취소
    @GetMapping("/{boardId}/hearts")
    public ResponseEntity heart(@PathVariable Long boardId , @RequestHeader(name = "auth-token") String token){

        return  ResponseEntity.ok(boardService.heart(tokenProvider.getUser(token).getId(), boardId));
    }


}
