package top.aprilyolies.snowflake.machineid.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.StringUtils;
import top.aprilyolies.snowflake.machineid.AbstractMachineIdProvider;

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

    //
    private static final String SNOWFLAKE_ZOOKEEPER_PARENT_PATH = "/snowflake/machine-ids";
    // machine id
    private int machineId = -1;

    public ZookeeperMachineIdProvider(String host) {
        this.host = host;
    }

    public void init() {
        try {
            this.zkClient = createZkClient();
            zkClient.start();
            Stat stat = zkClient.checkExists().forPath(SNOWFLAKE_ZOOKEEPER_PARENT_PATH);
            if (stat == null) {
                getMachineId();
            } else {
                List<String> machines = zkClient.getChildren().forPath(SNOWFLAKE_ZOOKEEPER_PARENT_PATH);
                Map<String, String> ipIds = new HashMap<>();
                for (String machine : machines) {   // 获取已经在 zookeeper 上注册的 ip 和 machine id 信息
                    if (machine.contains("-")) {
                        String[] strs = machine.split("-");
                        if (strs.length > 1) {
                            ipIds.putIfAbsent(strs[0], strs[1]);
                        }
                    }
                }
                if (ipIds.containsKey(host)) {
                    machineId = Integer.parseInt(ipIds.get(host));
                } else {
                    getMachineId();
                }
            }
        } catch (Exception e) {
            logger.info("Can't get machine id from zookeeper, and snowflake will try to fetch machine id from local file, if still can't," +
                    "snowflake will exit with an exception thrown");
        }
    }

    // 在 zookeeper 上创建序列化的
    private void getMachineId() throws Exception {
        String path = zkClient.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                .forPath(SNOWFLAKE_ZOOKEEPER_PARENT_PATH + "/" + host + "-");
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
        return 0;
    }

}
