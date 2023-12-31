package com.example.subback.service;

import com.example.subback.domain.BoardImg;
import com.example.subback.dto.Board;
import com.example.subback.mapper.BoardMapper;
import com.example.subback.mapper.ImgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class BoardService {
    private final BoardMapper mapper;
    private final ImgMapper mainImgMapper;

    private final S3Client s3;

    @Value("${image.file.prefix}")
    private String urlPrefix;

    @Value("${aws3.s3.bucket.name}")
    private String bucket;

    public boolean save(Board board, MultipartFile[] mainImg) throws IOException {
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

    private void upload(Integer boardId, MultipartFile mainImg) throws IOException {

        String key = "prj1/" + boardId + "/" + mainImg.getOriginalFilename();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3.putObject(objectRequest, RequestBody.fromInputStream(mainImg.getInputStream(), mainImg.getSize()));

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
        Board board = mapper.selectById(id);

        List<BoardImg> boardImgs = mainImgMapper.selectNamesByBoardId(id);

        for (BoardImg boardImg : boardImgs) {
            String url = urlPrefix + "prj1/" + id + "/" + boardImg.getName();
            boardImg.setUrl(url);
        }

        board.setMainImgs(boardImgs);

        return board;
    }

    public boolean remove(Integer id) {
        return mapper.deleteById(id) == 1;
    }

    public boolean update(Board board) {
        return mapper.updateById(board) == 1;
    }
}
