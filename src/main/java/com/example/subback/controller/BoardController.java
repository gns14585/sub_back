package com.example.subback.controller;

import com.example.subback.dto.Board;
import com.example.subback.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService service;

    @PostMapping("add")
    public ResponseEntity add(Board board,
                              @RequestParam(value = "mainImg[]", required = false) MultipartFile[] mainImg) {


        // 저장버튼 클릭 시 0.3초 버튼 잠금
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!service.validate(board)) {
            return ResponseEntity.badRequest().build();
        }
        if (service.save(board, mainImg)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("list")
    public List<Board> list() {
        return service.list();
    }

    @GetMapping("id/{id}")
    public Board get(@PathVariable Integer id) {
        return service.get(id);
    }

    @DeleteMapping("remove/{id}")
    public ResponseEntity remove(@PathVariable Integer id) {
        if (service.remove(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("edit")
    public ResponseEntity update(@RequestBody Board board) {
        if (service.update(board)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
