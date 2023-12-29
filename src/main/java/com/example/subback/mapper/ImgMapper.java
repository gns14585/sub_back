package com.example.subback.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImgMapper {

    @Insert("""
            INSERT INTO boardImg (boardId, name)
            VALUES (#{boardId}, #{name})
            """)
    int insert(Integer boardId, String name);
}
