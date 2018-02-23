package com.cw.zkconf.web.Service;

import com.cw.zkconf.web.Manager.ConfClient;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ConfServer {

    private static ConfClient confCli;
    private static Logger LOGGER = LoggerFactory.getLogger(ConfServer.class);

    public ConfServer() {
    }

    public ConfServer(String connect) {
        confCli = new ConfClient(connect);
    }

    public String createNode(String nodePath, String value) throws Exception,KeeperException.NodeExistsException  {
        return confCli.createNode(nodePath, value);
    }

    public void deleteNode(String nodePath) throws Exception {
        confCli.deleteNode(nodePath);
    }

    public void updateKeyValue(String oldKeyNodePath, String newNode, String newValue) throws Exception,KeeperException.NodeExistsException {
        confCli.updateKeyValue(oldKeyNodePath, newNode, newValue);
    }

    public List<String> getChildren(String parentPath) {
        return confCli.getChildren(parentPath);
    }

    public Map<String, String> getAllConfs(String confsParentNodePath) {
        return confCli.getAllConfs(confsParentNodePath);
    }

    public Map<String, String> getValue(String nodePath) throws Exception {
        return confCli.getValue(nodePath);
    }

}
