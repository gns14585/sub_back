package com.example.subback.service;

import com.example.subback.dto.Board;
import com.example.subback.mapper.BoardMapper;
import com.example.subback.mapper.ImgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper mapper;
    private final ImgMapper mainImgMapper;

    public boolean save(Board board, MultipartFile[] mainImg) {
        int cnt = mapper.insert(board);

        // boardFile 테이블에 mainImg 정보 저장
        if (mainImg != null) {
            for (int i = 0; i < mainImg.length; i++) {
                mainImgMapper.insert(board.getId(), mainImg[i].getOriginalFilename());

                upload(board.getId(), mainImg[i]);

            }
        }


        // 실제 이미지파일 S3 Bucket에 upload


        return cnt == 1;
    }

    private void upload(Integer boardId, MultipartFile mainImg) {
        // 파일 저장 경로
        // C:\Temp\prj1\게시물번호\파일명
        try {
            File folder = new File("C:\\Temp\\prj1\\" + boardId);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String path = folder.getAbsolutePath() + "\\" + mainImg.getOriginalFilename();
            File des = new File(path);
            mainImg.transferTo(des);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validate(Board board) {
        if (board == null) {
            return false;
        }
        if (board.getContent() == null || board.getContent().isBlank()) {
            return false;
        }
        if (board.getTitle() == null || board.getTitle().isBlank()) {
            return false;
        }
        if (board.getWriter() == null || board.getWriter().isBlank()) {
            return false;
        }
        return true;
    }

    public List<Board> list() {
        return mapper.list();
    }

    public Board get(Integer id) {
        return mapper.selectById(id);
    }

    public boolean remove(Integer id) {
        return mapper.deleteById(id) == 1;
    }

    public boolean update(Board board) {
        return mapper.updateById(board) == 1;
    }
}
