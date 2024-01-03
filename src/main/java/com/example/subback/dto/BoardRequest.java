package com.example.subback.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BoardRequest {
    private String productName;
    private List<BoardAddDetail> options;
}
