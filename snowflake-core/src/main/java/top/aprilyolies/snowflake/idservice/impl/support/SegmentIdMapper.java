package top.aprilyolies.snowflake.idservice.impl.support;

import org.apache.ibatis.annotations.*;

/**
 * @Author EvaJohnson
 * @Date 2019-07-14
 * @Email g863821569@gmail.com
 */
public interface SegmentIdMapper {
    // 根据 business 从数据库中获取对应的数据记录
    @Select("select business,begin,end from SEGMENT_ID_TABLE where business = #{business}")
    @Results(value = {
            @Result(column = "business", property = "business"),
            @Result(column = "begin", property = "begin"),
            @Result(column = "end", property = "end")
    })
    SegmentInfo getSegmentInfoFromDB(@Param("business") String business);

    // 根据 step 更新 business 对应的 segment 的信息
    @Update("UPDATE  SEGMENT_ID_TABLE SET `begin` = `end` + 1,`end` = `begin` + #{step} where business = 'order'")
    int updateSegmentTable(String business, int step);
}
