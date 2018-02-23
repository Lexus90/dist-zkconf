package com.cw.zkconf.web.Manager;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfClient {

    static final Logger LOGGER = LoggerFactory.getLogger(ConfClient.class);
    private static CuratorFramework client;

    public ConfClient(String connect) {
        client = CuratorFrameworkFactory.newClient(connect, new ExponentialBackoffRetry(1000, 3));
        if (CuratorFrameworkState.LATENT == client.getState()){
            client.start();
        }
    }

    /**
     * 返回节点path
     * @param nodePath
     * @return path
     * @throws Exception
     */
    public String createNode(String nodePath) throws Exception, KeeperException.NodeExistsException {
        return createNode(nodePath, "");
    }

    public String createNode(String nodePath, String value) throws Exception, KeeperException.NodeExistsException {

        String path = "";
        if (value == null) {
            value = "";
        }
        try {
            client.create().creatingParentContainersIfNeeded().forPath(nodePath, value.getBytes());
        } catch (KeeperException.NodeExistsException e){
            throw e;
        }
        return path;
    }

    /**
     * 更新配置键值对
     * @param oldKeyNodePath
     * @param newNode
     * @param newValue
     */
    public void updateKeyValue(String oldKeyNodePath, String newNode, String newValue) throws Exception,KeeperException.NodeExistsException {
        ZKPaths.PathAndNode pathAndNode = ZKPaths.getPathAndNode(oldKeyNodePath);
        String oldNode = pathAndNode.getNode();

        if (StringUtils.equals(oldNode, newNode)) {
            client.setData().forPath(oldKeyNodePath);
        } else {
            deleteNode(oldKeyNodePath);
            String parentPath = pathAndNode.getPath();
            String newKeyPath = ZKPaths.makePath(parentPath, newNode);
            createNode(newKeyPath, newValue);
        }
    }

    public void deleteNode(String nodePath) throws Exception {
        client.delete().guaranteed().forPath(nodePath);
    }

    public List<String> getChildren(String parentPath){
        List<String> groups = null;
        try {
            groups = client.getChildren().forPath(parentPath);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return groups;
    }

    public Map<String, String> getValue(String nodePath) throws Exception {
        Map<String, String> ret = new HashMap<String, String>();
        String node = ZKPaths.getNodeFromPath(nodePath);
        ret.put(node, new String(client.getData().forPath(nodePath), Charsets.UTF_8));
        return ret;
    }

    public Map<String, String> getAllConfs(String confsParentNodePath){
        Map<String, String> confsMap = Maps.newHashMap();
        try {
            List<String> keys = client.getChildren().forPath(confsParentNodePath);
            GetDataBuilder getDataBuilder = client.getData();
            String keyNodePath = null;
            for (String key : keys) {
                keyNodePath = ZKPaths.makePath(confsParentNodePath, key);
                confsMap.put(key, new String(getDataBuilder.forPath(keyNodePath), Charsets.UTF_8)) ;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return confsMap;
    }

}
