package top.aprilyolies.snowflake.machineid.impl;

import org.apache.commons.io.FileUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.StringUtils;
import top.aprilyolies.snowflake.machineid.AbstractMachineIdProvider;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author EvaJohnson
 * @Date 2019-07-12
 * @Email g863821569@gmail.com
 */
public class ZookeeperMachineIdProvider extends AbstractMachineIdProvider {
    // zookeeper 的地址
    private final String host;
    // zk 客户端
    private CuratorFramework zkClient;
    // 本地用于存放 machine id 的文件
    private static final String LOCAL_MACHINE_ID_FILE = System.getProperty("java.io.tmpdir") + "machine_id";
    // 用于注册到 zookeeper 的标识信息
    private String ipAddress;
    // 注册中心注册 ip 和 machine 信息的路径
    private static final String SNOWFLAKE_ZOOKEEPER_PARENT_PATH = "/snowflake/machine-ids";
    // machine id
    private int machineId = -1;

    public ZookeeperMachineIdProvider(String host) {
        this.host = host;
        this.ipAddress = getIpAddress();
    }

    public void init() {
        try {
            this.zkClient = createZkClient();
            zkClient.start();
            Stat stat = zkClient.checkExists().forPath(SNOWFLAKE_ZOOKEEPER_PARENT_PATH);
            if (stat == null) {
                logger.info("Initialize snowflake persistent path {} in zookeeper", SNOWFLAKE_ZOOKEEPER_PARENT_PATH);
                getMachineId();
            } else {
                List<String> machines = zkClient.getChildren().forPath(SNOWFLAKE_ZOOKEEPER_PARENT_PATH);
                Map<String, String> ipIds = new HashMap<>();
                for (String machine : machines) {   // 获取已经在 zookeeper 上注册的 ip 和 machine id 信息
                    if (machine.contains("-")) {
                        String[] ipIdsArr = machine.split("-");
                        if (ipIdsArr.length > 1) {
                            ipIds.putIfAbsent(ipIdsArr[0], ipIdsArr[1]);
                        }
                    }
                }
                if (ipIds.containsKey(ipAddress)) {
                    machineId = Integer.parseInt(ipIds.get(ipAddress));
                } else {
                    getMachineId();
                }
            }   // 将 machine id 缓存到 machine id file 中，或者是更新其中的内容
            if (storeOrUpdateMachineId(machineId)) {
                logger.info("Store machine id in {} successfully", LOCAL_MACHINE_ID_FILE);
            } else {
                logger.warn("Can't store machine id info in {}", LOCAL_MACHINE_ID_FILE);
            }
        } catch (Exception e) {
            logger.info("Can't get machine id from zookeeper, and snowflake will try to fetch machine id from local file, if still can't," +
                    "snowflake will exit with an exception thrown");
        }
    }

    // 将 machine id 缓存到 machine id file 中，或者是更新其中的内容
    private boolean storeOrUpdateMachineId(int machineId) {
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

    // 在 zookeeper 上创建序列化的
    private void getMachineId() throws Exception {
        String path = zkClient.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                .forPath(SNOWFLAKE_ZOOKEEPER_PARENT_PATH + "/" + ipAddress + "-");
        int idx = path.lastIndexOf("-");
        String sid = path.substring(idx + 1);
        int machineId = Integer.parseInt(sid);
        checkMachineId(machineId);
        this.machineId = machineId;
    }

    // 创建 zookeeper 客户端，即 CuratorFramework
    private CuratorFramework createZkClient() {
        if (StringUtils.isEmpty(host)) {
            logger.info("Can't create curator framework for host {}", host);
        }
        return CuratorFrameworkFactory.builder()
                .connectString(host)
                .retryPolicy(new RetryUntilElapsed(2000, 3))
                .connectionTimeoutMs(10000)
                .sessionTimeoutMs(6000)
                .build();
    }

    @Override
    public int buildMachineId() {
        return machineId;
    }

    // 获取本地的 ip 地址，优先获取第一个可用的 ip 地址
    private String getIpAddress() {
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
