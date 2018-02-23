package com.cw.zkconf.service.zookeeper;

import com.cw.zkconf.common.utils.EhcacheUtil;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.utils.ZKPaths;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.apache.curator.shaded.com.google.common.base.Charsets.UTF_8;

public class ConfGroup {

    static final Logger LOGGER = LoggerFactory.getLogger(ConfGroup.class);
    private CuratorFramework client;
    private ZkConfProfile zkConfProfile;
    private String groupNode;
    @Getter @Setter
    static Cache<String, String> confsCache ;

    private CuratorListener listener = new ConfNodeEventListener(this);

    public ConfGroup(ZkConfProfile zkConfProfile, String groupNode) {
        this.zkConfProfile = zkConfProfile;
        this.groupNode = groupNode;
        confsCache = EhcacheUtil.createCache(groupNode, String.class, String.class);
    }

    /**
     * 初始化 client 节点
     */
    public void initConfCli(){
        client = CuratorFrameworkFactory.newClient(zkConfProfile.getConnectStr(),zkConfProfile.getRetryPolicy());
        client.start();
        LOGGER.info("Loading properties for groupNode: {}" , groupNode);
        client.getCuratorListenable().addListener(listener);
        loadNode();
    }

    private String getGroupNodePath() {
        return ZKPaths.makePath(zkConfProfile.getRootVersionNode(), groupNode);
    }

    public String getValueFromZk(String key) throws Exception {
        String groupNodePath = getGroupNodePath();
        String keyPath = ZKPaths.makePath(groupNodePath, key);
        return new String(client.getData().forPath(keyPath), UTF_8);
    }

    /**
     * 加载节点并监听节点变化
     */
    void loadNode() {
        // root_node/version_node
        final String groupNodePath = getGroupNodePath();

        // keys groupNode
        final GetChildrenBuilder childrenBuilder = client.getChildren();
        try {
            //get group groupNode and listen them
            ///root_node/vsersion_node/grou_node
            final List<String> keys = childrenBuilder.watched().forPath(groupNodePath);
            if (keys != null) {
                final Map<String, String> configs = Maps.newHashMap();
                for (String key : keys) {
                    // 加载属性的 key-value
                    final Pair<String, String> keyValue = loadKey(ZKPaths.makePath(groupNodePath, key));
                    if (keyValue != null) {
                        configs.put(keyValue.getKey(), keyValue.getValue());
                    }
                }
                cleanAndPutAll(configs);
                LOGGER.info("configs : {}",configs);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        }
    }

    /**
     * 更新缓存 confsCache,
     * @param confMap
     */
    private void cleanAndPutAll(Map<String, String> confMap) {

        Iterator<Cache.Entry<String, String>> iterator = confsCache.iterator();
        while (iterator.hasNext()) {
            Cache.Entry<String, String> entry = iterator.next();
            if ( !confMap.containsKey(entry.getKey())) {
                iterator.remove();
            }
        }
        confsCache.putAll(confMap);
    }


    /**
     *
     * @param keyNodePath property key groupNode path
     * @return
     * @throws Exception
     */
    private Pair<String, String> loadKey(final String keyNodePath) throws Exception {

        final String keyNodeName = ZKPaths.getNodeFromPath(keyNodePath);
        final GetDataBuilder dataBuilder = client.getData();
        final String value = new String(dataBuilder.watched().forPath(keyNodePath), UTF_8);
        return new ImmutablePair<String, String>(keyNodeName, value);
    }

    /**
     * reload  key-value of property
     * @param keyNodePath property key groupNode path
     */
    void reloadKey(final String keyNodePath) {
        try {

            final Pair<String, String> keyValue = loadKey(keyNodePath);
            if (keyValue != null) {
                confsCache.put(keyValue.getKey(), keyValue.getValue());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    public void close() {
        if (client != null) {
            client.getCuratorListenable().removeListener(listener);
            client.close();
        }
    }

    public Map<String, String>  exportConfs(){
        return EhcacheUtil.toMap(confsCache);
    }

}