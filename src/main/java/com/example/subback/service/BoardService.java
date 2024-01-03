package com.example.subback.service;

import com.example.subback.controller.BoardController;
import com.example.subback.domain.BoardImg;
import com.example.subback.domain.Details;
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        List<Board> boards = mapper.list();
        // 각 게시물의 이미지 URL 목록을 가져오는 로직 추가
        boards.forEach(board -> {
            List<BoardImg> boardImgs = mainImgMapper.selectNamesByBoardId(board.getId());
            boardImgs.forEach(img -> img.setUrl(urlPrefix + "prj1/" + board.getId() + "/" + img.getName()));
            board.setMainImgs(boardImgs); // 각 Board 객체에 이미지 목록을 설정
        });
        return boards;
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

    public void remove(Integer id) {

        deleteMainImg(id);

        mapper.deleteById(id); // 상품삭제
    }

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


    public boolean update(Board board) {
        return mapper.updateById(board) == 1;
    }

    public void addList(BoardController.DetailsReqeust detailsRequest) {

        if (detailsRequest != null && !detailsRequest.getDetails().isEmpty()) {
            for (int i = 0; i < detailsRequest.getDetails().size(); i++) {

            Details firstDetail = detailsRequest.getDetails().get(i); // 0번째 Detail 객체를 가져옵니다.
                // 이제 firstDetail을 사용하여 필요한 작업을 수행하세요.

                // 예시: 첫 번째 Detail의 정보를 출력합니다.
                System.out.println("상품명: " + firstDetail.getProductName());
                System.out.println("색상: " + firstDetail.getColor());
                System.out.println("축: " + firstDetail.getAxis());
                System.out.println("선: " + firstDetail.getLine());

                // mapper의 addList 메소드를 호출하여 모든 Details 리스트를 추가합니다.
            mapper.addList(firstDetail);
            }
        }
    }

    public List<Details> getDetailsByBoardId(Integer boardId) {
        System.out.println("boardId = " + boardId);
        return mapper.getDetailsByBoardId(boardId);
    }
}
