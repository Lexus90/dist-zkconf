package com.cw.zkconf.service.zookeeper;

import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfClient {

    private Logger LOGGER = LoggerFactory.getLogger(ConfClient.class);

    private String connect;
    private String rootNode;
    private String version;
    private String groupNode;
    private ConfGroup confGroup;
    private Cache<String, String> cache;

    public ConfClient(String connect, String rootNode, String version, String groupNode) {
        this.connect = connect;
        this.rootNode = rootNode;
        this.version = version;
        this.groupNode = groupNode;
        ZkConfProfile zkConfProfile =  new ZkConfProfile(connect, rootNode, version);
        confGroup = new ConfGroup(zkConfProfile, groupNode);
        confGroup.initConfCli();
        cache = confGroup.getConfsCache();
    }

    /**
     * 缓存未命中则，从zk获取
     * @param key
     * @return
     */
    public String get(String key){
        if (cache == null){
            cache = confGroup.getConfsCache();
        }
        String val = cache.get(key);
        if (null == val){
            try {
                val = confGroup.getValueFromZk(key);
            } catch (Exception e) {
                val = "";
                LOGGER.error(e.getMessage(), e);
            }
        }
        return val;
    }
}
