package com.cw.zkconf.service.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfNodeEventListener implements CuratorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfNodeEventListener.class);
    private final ConfGroup confGroupNode;


    public ConfNodeEventListener(ConfGroup confCliNode) {
        super();
        this.confGroupNode = Preconditions.checkNotNull(confCliNode);
    }

    @Override
    public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("event : {}",event.toString());
        }

        final WatchedEvent watchedEvent = event.getWatchedEvent();
        if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
            boolean someChange = false;
            switch (watchedEvent.getType()) {
                case NodeChildrenChanged:
                    confGroupNode.loadNode();
                    someChange = true;break;
                case NodeDataChanged:
                    confGroupNode.reloadKey(watchedEvent.getPath());
                    someChange = true;break;
                default:
                    break;
            }

            if (someChange) {
                LOGGER.info("watched event:{}",watchedEvent);
                LOGGER.info("persist confs to ehcache, confs=[{}]", confGroupNode.exportConfs());
            }
        }

    }
}
