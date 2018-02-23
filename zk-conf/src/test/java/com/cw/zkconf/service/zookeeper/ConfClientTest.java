package com.cw.zkconf.service.zookeeper;

import com.cw.zkconf.common.contents.ConstantClass;
import org.apache.curator.utils.ZKPaths;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ConfClientTest {


    static String connect = "127.0.0.1:2181";
    static String project = "P1";
    static String env = "stg3";
    static String version = "0.0.1";
    static String rootNode = ZKPaths.makePath(ConstantClass.CONF_ROOT_NODE, env, project);
    static String groupNode = "G1";

    @Test
    public void get() throws Exception {
        ConfClient confClient = new ConfClient(connect, rootNode, version, groupNode);

        while (true) {

            System.out.println("name = "+confClient.get("name"));
            System.out.println("age = "+confClient.get("age"));
            TimeUnit.SECONDS.sleep(3);
        }

    }

}
