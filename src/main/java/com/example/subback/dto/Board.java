package com.example.subback.dto;

import com.example.subback.domain.BoardImg;
import lombok.Data;

import java.util.List;

@Data
public class Board {
    private Integer id;
    private String title;
    private String content;
    private String writer;
    private String inserted;
    private List<BoardImg> mainImgs;
}
