package com.example.subback.mapper;

import com.example.subback.domain.Details;
import com.example.subback.dto.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Insert("""
            INSERT INTO board(title, content, price)
            VALUES (#{title}, #{content}, #{price})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Board board);

    @Select("""
        SELECT id, title, content, price, inserted
        FROM board
        ORDER BY inserted DESC
        """)
    List<Board> list();

    @Select("""
            SELECT id, title, content, price, inserted
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
                price = #{price}
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

    @Delete("""
        DELETE FROM boardaddlist
        WHERE boardId = #{boardId}
        """)
    void deleteDetailsByBoardId(Integer boardId);
}
