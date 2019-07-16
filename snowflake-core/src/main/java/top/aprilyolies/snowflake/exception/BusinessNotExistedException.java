package top.aprilyolies.snowflake.exception;

/**
 * @Author EvaJohnson
 * @Date 2019-07-16
 * @Email g863821569@gmail.com
 */
public class BusinessNotExistedException extends RuntimeException {
    private final String business;

    public BusinessNotExistedException(String business) {
        this.business = business;
    }

    public String getBusiness() {
        return business;
    }
}
