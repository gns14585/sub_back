package com.example.subback.mapper;

import com.example.subback.domain.Details;
import com.example.subback.dto.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Insert("""
            INSERT INTO board(title, content, price, manufacturer)
            VALUES (#{title}, #{content}, #{price}, #{manufacturer})
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
            SELECT id, title, content, price, inserted, manufacturer
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
                manufacturer = #{manufacturer},
                price = #{price}
            WHERE id = #{id}
            """)
    int updateById(Board board);

    @Insert("""
            INSERT INTO boardaddlist(color, axis, line, boardId, inch)
            VALUES (#{color}, #{axis}, #{line}, #{boardId}, #{inch})
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

    @Update("""
            UPDATE boardaddlist
            SET 
                color = #{color},
                axis = #{axis},
                line = #{line},
                inch = #{inch}
            WHERE boardId = #{boardId}
            """)
    void updateDetails(Details details);
}
