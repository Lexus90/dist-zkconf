package com.cw.zkconf.web.Manager;

import org.apache.curator.utils.ZKPaths;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class ZkClientTest {

    static Logger LOGGER = LoggerFactory.getLogger(ZkClientTest.class);
    static String connect = "127.0.0.1:2181";
    //    static String rootNode = "/zkconf/project1/env1/module1";
    static String env = "stg3";
    static String version = "0.0.1";
    static String project = "P1";
    static String group = "G1";

    private static ConfClient client = new ConfClient(connect);


    @Test
    public void getAllConfs() throws Exception {
        String groupPath = ZKPaths.makePath(env,project, version, group);
        Map<String, String> map = client.getAllConfs(groupPath);
        System.out.println("confs = "+map);
    }
}
