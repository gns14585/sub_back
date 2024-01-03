package com.example.subback.mapper;

import com.example.subback.domain.Details;
import com.example.subback.dto.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Insert("""
            INSERT INTO board(title, content, writer)
            VALUES (#{title}, #{content}, #{writer})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Board board);

    @Select("""
            SELECT b.id, b.title, b.content, b.writer, b.inserted,
                   CONCAT(#{urlPrefix}, 'prj1/', bi.boardId, '/', bi.name) as imageUrl
            FROM board b
            LEFT JOIN boardimg bi ON b.id = bi.boardId
            ORDER BY b.inserted DESC
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

    @Update("""
            UPDATE board
            SET 
                title = #{title},
                content = #{content},
                writer = #{writer}
            WHERE id = #{id}
            """)
    int updateById(Board board);

    @Insert("""
            INSERT INTO boardaddlist(color, axis, line, boardId)
            VALUES (#{color}, #{axis}, #{line}, #{boardId})
            """)
    void addList(Details details);

    @Select("""
            SELECT *
            FROM boardaddlist
            WHERE boardId = #{id}
            """)
    List<Details> getDetailsByBoardId(Integer id);
}
