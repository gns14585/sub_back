package com.example.subback.mapper;

import com.example.subback.domain.BoardImg;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ImgMapper {

    @Insert("""
            INSERT INTO boardImg (boardId, name)
            VALUES (#{boardId}, #{name})
            """)
    int insert(Integer boardId, String name);

    @Select("""
            SELECT id, name
            FROM boardimg
            WHERE boardId = #{boardId}
            """)
    List<BoardImg> selectNamesByBoardId(Integer boardId);

    @Select("""
            SELECT id, name
            FROM boardimg
            WHERE id = #{id}
            """)
    BoardImg selectNameById(Integer id);

    @Delete("""
            DELETE FROM boardimg
            WHERE boardId = #{boardId}
            """)
    void deleteByBoardId(Integer boardId);
}
