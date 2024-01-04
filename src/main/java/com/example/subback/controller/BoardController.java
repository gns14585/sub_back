package com.example.subback.controller;

import com.example.subback.domain.Details;
import com.example.subback.domain.DetailsReqeust;
import com.example.subback.dto.Board;
import com.example.subback.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService service;

    // ------------------------------ 상품 저장 ------------------------------
    @PostMapping("add")
    public ResponseEntity add(Board board,
                              @RequestParam(value = "mainImg[]", required = false) MultipartFile[] mainImg) throws IOException {
        // 저장버튼 클릭 시 0.3초 버튼 잠금
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 상품 저장 시 검증
        if (!service.validate(board)) {
            return ResponseEntity.badRequest().build();
        }
        // 상품 저장로직
        if (service.save(board, mainImg)) {
            // board 테이블에 있는 id를 넣어줌
            return ResponseEntity.ok(board.getId());
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ------------------------------ 상품 상세선택 저장 ------------------------------
    @PostMapping("addList")
    public void addList(@RequestBody DetailsReqeust details) {
        service.addList(details);
    }

    // ------------------------------ 상품 리스트 ------------------------------
    @GetMapping("list")
    public List<Board> list() {
        return service.list();
    }

    // ------------------------------ 상품 보기 ------------------------------
    @GetMapping("id/{id}")
    public Board get(@PathVariable Integer id) {
        return service.get(id);
    }

    // ------------------------------ 상품 상세선택 보기 ------------------------------
    @GetMapping("details/{id}")
    public ResponseEntity<List<Details>> getDetailsByBoardId(@PathVariable Integer id) {
        List<Details> details = service.getDetailsByBoardId(id);
        return ResponseEntity.ok(details);
    }

    // ------------------------------ 상품 삭제 ------------------------------
    @DeleteMapping("remove/{id}")
    public void remove(@PathVariable Integer id) {
        service.remove(id);
    }

    // ------------------------------ 상품 수정 ------------------------------
    @PutMapping("edit")
    public ResponseEntity update(Board board,
                                 @RequestParam(value = "removeMainImgs[]", required = false) List<Integer> removeMainImgs,
                                 @RequestParam(value = "mainImg[]", required = false) MultipartFile[] uploadMainImg ) throws IOException {
        if (service.update(board, removeMainImgs, uploadMainImg)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
