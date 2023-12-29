package com.example.subback.mapper;

import com.example.subback.dto.Board;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Insert("""
            INSERT INTO board(title, content, writer)
            VALUES (#{title}, #{content}, #{writer})
            """)
    int insert(Board board);

    @Select("""
            SELECT id, title, writer, inserted
            FROM board
            ORDER BY inserted DESC
            """)
    List<Board> list();

    @Select("""
            SELECT id, title, content, writer, inserted
            FROM board
            WHERE id = #{id}
            """)
    Board selectById(Integer id);

    @Delete("""
            DELETE FROM board
            WHERE id = #{id}
            """)
    int deleteById(Integer id);
}
