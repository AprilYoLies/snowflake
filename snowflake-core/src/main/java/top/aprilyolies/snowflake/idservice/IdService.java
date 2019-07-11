package top.aprilyolies.snowflake.idservice;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */

/**
 * id service 接口，该接口的实现类提供具体的 id 生成逻辑
 */
public interface IdService {
    /**
     * 生成 id 的方法
     *
     * @return 生成的唯一 id
     */
    String generateId();
}
