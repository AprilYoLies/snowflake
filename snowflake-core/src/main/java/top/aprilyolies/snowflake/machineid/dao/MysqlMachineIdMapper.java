package top.aprilyolies.snowflake.machineid.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * @Author EvaJohnson
 * @Date 2019-07-13
 * @Email g863821569@gmail.com
 */
public interface MysqlMachineIdMapper {
    @Select("select ID from MYSQL_MACHINE_ID_PROVIDER where ip = #{ipAddress}")
    @Results(value = {
            @Result(column = "ID", property = "machineId"),
            @Result(column = "IP", property = "ipAddress")
    })
    MachineId getMachineIdByIpAddress(@Param("ipAddress") String ipAddress);
}
