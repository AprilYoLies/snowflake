package top.aprilyolies.snowflake.idservice.support.idbuilder;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */

/**
 * id 构建器，用于根据 id 的结构信息来构建 id
 */
public interface IdBuilder {
    // 根据 id 的结构信息来构建 id
    String buildId();
}
