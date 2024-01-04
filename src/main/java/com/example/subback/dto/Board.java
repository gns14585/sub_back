package com.example.subback.dto;

import com.example.subback.domain.BoardImg;
import com.example.subback.domain.Details;
import lombok.Data;

import java.util.List;

@Data
public class Board {
    private Integer id;
    private String title;
    private String content;
    private Integer price;
    private String inserted;
    private List<BoardImg> mainImgs;
}
