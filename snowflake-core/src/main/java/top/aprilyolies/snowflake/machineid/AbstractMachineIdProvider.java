package top.aprilyolies.snowflake.machineid;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public abstract class AbstractMachineIdProvider implements MachineIdProvider {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // 本地用于存放 machine id 的文件
    protected static final String LOCAL_MACHINE_ID_FILE = System.getProperty("java.io.tmpdir") + "machine_id";

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

    // 将 machine id 缓存到 machine id file 中，或者是更新其中的内容
    protected boolean storeOrUpdateMachineId(int machineId) {
        try {
            File file = new File(LOCAL_MACHINE_ID_FILE);
            if (file.exists()) {    // 如果缓存文件存在，就直接更新其中的内容
                logger.info("The machine id cache file in {} is existed, try to update the machine id cache file content", LOCAL_MACHINE_ID_FILE);
                FileUtils.write(file, String.valueOf(machineId), false);
            } else {
                logger.info("The machine id cache file in {} is not existed, try to create a new file and store the machine id", LOCAL_MACHINE_ID_FILE);
                boolean mkdir = file.getParentFile().mkdirs();   // 否则先创建父文件夹
                if (mkdir) {
                    logger.info("Create parent path {} successfully", LOCAL_MACHINE_ID_FILE.substring(0, LOCAL_MACHINE_ID_FILE.lastIndexOf(File.separator)));
                    FileUtils.writeStringToFile(file, String.valueOf(machineId), false);    // 创建文件成功，将 machine id 信息写入其中
                } else {
                    logger.info("Create parent path {} failed", LOCAL_MACHINE_ID_FILE.substring(0, LOCAL_MACHINE_ID_FILE.lastIndexOf(File.separator)));
                    if (file.getParentFile().exists()) {
                        FileUtils.writeStringToFile(file, String.valueOf(machineId), false);    // 创建文件成功，将 machine id 信息写入其中
                    } else {
                        return false;
                    }
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // 重本地 machine id 缓存中获取 machine id
    protected int fetchMachineIdFromLocalCache() {
        File file = new File(LOCAL_MACHINE_ID_FILE);
        if (file.exists()) {
            try {
                List<String> lines = FileUtils.readLines(file);
                if (lines.size() > 0) {
                    String line = lines.get(0);
                    return Integer.parseInt(line);
                }
                logger.info("Machine id local cache file is empty");
                return -1;
            } catch (Exception e) {
                logger.error("Can't open file {}", LOCAL_MACHINE_ID_FILE);
                return -1;
            }
        } else {
            return -1;
        }
    }
}
