package com.cw.zkconf.service.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ConfGroupTest {


    static String connect = "127.0.0.1:2181";
//    static String rootNode = "/zkconf/project1/env1/module1";
    static String rootNode = "/zkconf/project1/env1/module1";
    static String version = "0.0.1";
//    static String groupNode = "group1";
    static String groupNode = "group1";

    Logger LOGGER = LoggerFactory.getLogger(ConfGroupTest.class);

//    @Before
    public void beforeMethod(){
        LOGGER.info("====== beforeMethod ======");
        CuratorFramework client = CuratorFrameworkFactory.newClient(connect, new RetryUntilElapsed(1000, 3));
        client.start();
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = client.getZookeeperClient().getZooKeeper();
            String versionPath = ZKPaths.makePath(rootNode, version);
            String groupPath = ZKPaths.makePath(versionPath, groupNode);
            ZKPaths.mkdirs(zooKeeper, groupPath, true);

            String prop1Path = ZKPaths.makePath(groupPath, "name");
            client.create().forPath(prop1Path, "caowei".getBytes());

            String prop2Path = ZKPaths.makePath(groupPath, "age");
            client.create().forPath(prop2Path, "18".getBytes());

            String prop3Path = ZKPaths.makePath(groupPath, "sex");
            client.create().forPath(prop3Path, "man".getBytes());

        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        } finally {
            if (zooKeeper != null) {
                try {
                    zooKeeper.close();
                } catch (InterruptedException e) {
                    LOGGER.info(e.getMessage(), e);
                }
            }
            if (client != null) {
                client.close();
            }
        }
    }

    @Test
    public void testConfGroup() {
        ConfGroup confGroup = null;
        try {
            ZkConfProfile zkConfProfile =  new ZkConfProfile(connect, rootNode, version);
            confGroup = new ConfGroup(zkConfProfile, groupNode);
            confGroup.initConfCli();
            TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            confGroup.close();
        }
    }
    @Test
    public void testGetValueFromZk(){
        ConfGroup confGroup = null;
        ZkConfProfile zkConfProfile =  new ZkConfProfile(connect, rootNode, version);
        confGroup = new ConfGroup(zkConfProfile, groupNode);
        confGroup.initConfCli();
        try {
            LOGGER.info("name="+confGroup.getValueFromZk("name"));;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}