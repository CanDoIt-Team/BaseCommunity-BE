package com.base.community.controller;


import com.base.community.dto.BoardCommentDto;
import com.base.community.dto.BoardDetailDto;
import com.base.community.dto.BoardDto;
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
    public ResponseEntity<?> boardList(@RequestParam(required = false) String category,
                                          @RequestParam(defaultValue = "1") int page){

         return ResponseEntity.ok(boardService.boardList(category, page-1));
   }

   // 게시글 상세보기
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailDto> boardDetail(@PathVariable Long boardId){

        return ResponseEntity.ok(boardService.boardDetail(boardId));
    }

   // 내가 작성한 게시글 목록 보기
    @GetMapping("/myBoardList")
    public  ResponseEntity<?> myBoardList(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                    @RequestParam(defaultValue = "1") int page){

        return ResponseEntity.ok(boardService.myBoardList(tokenProvider.getUser(token).getId(), page-1));
    }

    // 내가 좋아요한 게시글 목록 보기
    @GetMapping("/myHeartList")
    public ResponseEntity<?> myHeartList(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                      @RequestParam(defaultValue = "1") int page){

        return ResponseEntity.ok(boardService.myHeartList(tokenProvider.getUser(token).getId(), page-1));
    }


    // 게시글 작성
    @PostMapping()
    public ResponseEntity<Long> write(@RequestHeader(name = "X-AUTH-TOKEN") String token
    , @RequestBody BoardDto form){

        return ResponseEntity.ok(boardService.writeBoard(form, tokenProvider.getUser(token).getId()));
    }

    //게시글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<Long> modify(@RequestHeader(name = "X-AUTH-TOKEN") String token
            , @RequestBody BoardDto form, @PathVariable Long boardId){

        return ResponseEntity.ok(boardService.modifyBoard(form, tokenProvider.getUser(token).getId(),boardId));
    }

    //게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> delete(@RequestHeader(name = "X-AUTH-TOKEN") String token
            , @PathVariable Long boardId){

        return ResponseEntity.ok(boardService.deleteBoard(tokenProvider.getUser(token).getId(), boardId));
    }

    // 댓글 작성
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<Long> writeComment(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                             @RequestBody BoardCommentDto form, @PathVariable Long boardId){

        return ResponseEntity.ok(boardService.writeBoardComment(form, tokenProvider.getUser(token).getId(), boardId));
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Long> modifyComment(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                              @RequestBody BoardCommentDto form, @PathVariable Long commentId){
        return ResponseEntity.ok(boardService.modifyComment(form, tokenProvider.getUser(token).getId() ,commentId));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@RequestHeader(name = "X-AUTH-TOKEN") String token
            , @PathVariable Long commentId){

        return ResponseEntity.ok(boardService.deleteComment(tokenProvider.getUser(token).getId() , commentId));

    }


    // 좋아요 | 좋아요 취소
    @GetMapping("/{boardId}/hearts")
    public ResponseEntity<Boolean> heart(@PathVariable Long boardId , @RequestHeader(name = "X-AUTH-TOKEN") String token){

        return  ResponseEntity.ok(boardService.heart(tokenProvider.getUser(token).getId(), boardId));
    }


}
