package top.aprilyolies.snowflake.machineid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public abstract class AbstractMachineIdProvider implements MachineIdProvider {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 检查 id 是否合法
     *
     * @param id 被检查的 machine id
     * @return 合法的 id
     */
    protected int checkMachineId(int id) {
        if (id >= 0 && id <= 1023) {
            return id;
        } else {
            logger.info("Machine id should between 0 and 1023");
            throw new IllegalArgumentException("Machine id should between 0 and 1023");
        }
    }

    // 获取本地的 ip 地址，优先获取第一个可用的 ip 地址
    protected String getIpAddress() {
        String ip = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()    // 查看当前 ip 是否是本地回环地址
                            && !inetAddress.isLinkLocalAddress()    // 链路本地地址
                            && inetAddress.isSiteLocalAddress()) {  // 这个算是公网地址？？
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
            return ip;
        } catch (SocketException e) {
            logger.error("None of ip address could be use, return null");
            return null;
        }
    }
}
