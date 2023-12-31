package com.example.subback.service;

import com.example.subback.domain.BoardImg;
import com.example.subback.domain.Details;
import com.example.subback.domain.DetailsReqeust;
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
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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

    // ------------------------------ 상품 저장 로직 ------------------------------
    public boolean save(Board board, MultipartFile[] mainImg) throws IOException {
        // 상품 정보를 먼저 저장
        if (mapper.insert(board) != 1) {
            return false;
        }
        Integer boardId = board.getId(); // 생성된 상품 ID
        // 이미지 정보 저장
        if (mainImg != null) {
            for (MultipartFile img : mainImg) {
                mainImgMapper.insert(boardId, img.getOriginalFilename());
                upload(boardId, img);
            }
        }
        return true;
    }

    // ------------------------------ 상품 상세선택 저장 로직 ------------------------------
    public void addList(DetailsReqeust detailsRequest) {
        if (detailsRequest != null && detailsRequest.getDetails() != null) {
            for (Details detail : detailsRequest.getDetails()) {
                // 각 필드가 null인지 확인하고, null이면 빈 문자열로 설정
                detail.setColor(detail.getColor() != null ? detail.getColor() : "");
                detail.setAxis(detail.getAxis() != null ? detail.getAxis() : "");
                detail.setLine(detail.getLine() != null ? detail.getLine() : "");
                detail.setInch(detail.getInch() != null ? detail.getInch() : "");
                // 기타 필드에 대해서도 동일한 작업을 수행

                // DB에 저장
                mapper.addList(detail);
            }
        }
    }


    // ------------------------------ 상품 이미지 업로드 로직 ------------------------------
    private void upload(Integer boardId, MultipartFile mainImg) throws IOException {
        String key = "prj1/" + boardId + "/" + mainImg.getOriginalFilename();
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
        s3.putObject(objectRequest, RequestBody.fromInputStream(mainImg.getInputStream(), mainImg.getSize()));
    }

    // ------------------------------ 상품 저장시 검증 로직 ------------------------------
    public boolean validate(Board board) {
        if (board == null) {
            return false;
        }
        if (board.getTitle() == null || board.getTitle().isBlank()) { // 상품명
            return false;
        }
        if (board.getContent() == null || board.getContent().isBlank()) { // 상품설명
            return false;
        }
        if (board.getManufacturer() == null || board.getManufacturer().isBlank()) { // 제조사
            return false;
        }
        if (board.getPrice() == null) { // 금액
            return false;
        }
        return true;
    }

    // ------------------------------ 상품 리스트 로직 ------------------------------
    public List<Board> list() {
        List<Board> boards = mapper.list();
        // 각 게시물의 이미지 URL 목록을 가져오는 로직 추가
        boards.forEach(board -> {
            List<BoardImg> boardImgs = mainImgMapper.selectNamesByBoardId(board.getId());
            boardImgs.forEach(img -> img.setUrl(urlPrefix + "prj1/" + board.getId() + "/" + img.getName()));
            board.setMainImgs(boardImgs); // 각 Board 객체에 이미지 목록을 설정
        });
        return boards;
    }

    // ------------------------------ 상품 보기 로직 ------------------------------
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

    // ------------------------------ 상품 상세선택 보기 로직 ------------------------------
    public List<Details> getDetailsByBoardId(Integer boardId) {
        return mapper.getDetailsByBoardId(boardId);
    }

    // ------------------------------ 상품 삭제 로직 ------------------------------
    public void remove(Integer id) {
        mapper.deleteDetailsByBoardId(id); // 상세선택 삭제
        deleteMainImg(id); // 이미지 삭제
        mapper.deleteById(id); // 상품삭제
    }

    // ------------------------------ 상품 삭제 시 s3,db 이미지 삭제 로직 ------------------------------
    private void deleteMainImg(Integer boardId) {
        List<BoardImg> boardImgs = mainImgMapper.selectNamesByBoardId(boardId);
        for (BoardImg img : boardImgs) {
            String key = "prj1/" + boardId + "/" + img.getName();
            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3.deleteObject(objectRequest);
        }
        mainImgMapper.deleteByBoardId(boardId);
    }

    // ------------------------------ 상품 수정 로직 ------------------------------
    public boolean update(Board board,
                          List<Integer> removeMainImgs,
                          MultipartFile[] uploadMainImg) throws IOException {
        // 이미지파일 지우기
        if (removeMainImgs != null) {
            for (Integer id : removeMainImgs) {
                // s3에서 지우기
                BoardImg mainImg = mainImgMapper.selectById(id);
                String key = "prj1/" + board.getId() + "/" + mainImg.getName();
                DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();
                s3.deleteObject(objectRequest);
                // db에서 지우기
                mainImgMapper.deleteById(id);
            }
        }
        // 이미지파일 추가하기
        if (uploadMainImg != null) {
            // s3에 추가하기
            for (MultipartFile file : uploadMainImg) {
                upload(board.getId(), file);
                // db에 추가하기
                mainImgMapper.insert(board.getId(), file.getOriginalFilename());
            }
        }
        // 상품 정보 수정 로직
        return mapper.updateById(board) == 1;
    }

    // ------------------------------ 상품 상세선택 수정 로직 ------------------------------
    public void updateDetails(List<Details> detailsList) {
        for (Details details : detailsList) {
            if (details.getBoardId() == null) {
                mapper.insertDetails(details);
            } else {
                mapper.updateDetails(details);
            }
        }
    }
}
