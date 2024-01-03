package com.example.subback.domain;

import lombok.Data;

import java.util.List;

@Data
public class DetailsReqeust{
    private Integer boardId;
    private String content;
    private String title;
    private String writer;
    private List<Details> details;
}
