package com.cw.zkconf.web.Web;


import com.alibaba.fastjson.JSONObject;
import com.cw.zkconf.common.contents.ConstantClass;
import com.cw.zkconf.web.Service.ConfServer;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class ConfController {

    private static Logger LOGGER = LoggerFactory.getLogger(ConfServer.class);

    @Autowired
    private ConfServer confServer;

    @CrossOrigin
    @RequestMapping("/groups")
    public List<String> getAllGroups(@PathParam("env") String env,@PathParam("project") String project, @PathParam("version") String version){
        String versonNodePath = ZKPaths.makePath(ConstantClass.CONF_ROOT_NODE, env, project, version);
        return confServer.getChildren(versonNodePath);
    }
    @CrossOrigin
    @RequestMapping("/keys")
    public Map<String, Object> getAllKeys(@PathParam("env") String env,@PathParam("project") String project, @PathParam("version") String version,
                                          @PathParam("group") String group){

        LOGGER.info(group);
        Map<String, Object> map = new HashMap<>();
        map.put("code",200);
        String groupNodePath = ZKPaths.makePath(ConstantClass.CONF_ROOT_NODE, env, project, version,group);
        JSONObject json;
        Map<String, Object> m = new HashMap<>();
        m.putAll(confServer.getAllConfs(groupNodePath));
//        json = new JSONObject(m);
        map.put("data", m);
        return map;
    }
    @CrossOrigin
    @RequestMapping("/key")
    public Map<String, Object> getKeyValue(@PathParam("env") String env,@PathParam("project") String project, @PathParam("version") String version,
                                           @PathParam("group") String group,@PathParam("key") String key) {
        LOGGER.info("project:"+project + ",key:"+key);
        Map<String, Object> map = new HashMap<>();
        map.put("code",200);
        String keyPath = ZKPaths.makePath(ConstantClass.CONF_ROOT_NODE, env,project, version,group, key);
        try {
            map.put("data", confServer.getValue(keyPath));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return map;
    }
    @CrossOrigin
    @RequestMapping("/create_project_node")
    public String creatProjNode(@PathParam("env") String env,@PathParam("project") String project){
        String path = ZKPaths.makePath(ConstantClass.CONF_ROOT_NODE, env,project);
        try {
            path = confServer.createNode(path, null);
        } catch (KeeperException.NodeExistsException e) {
            LOGGER.error(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return path;

    }
    @CrossOrigin
    @RequestMapping("/create_version_node")
    public String creatVersionNode(@PathParam("env") String env,@PathParam("project") String project, @PathParam("version") String version){
        String path = ZKPaths.makePath(ConstantClass.CONF_ROOT_NODE, env,project, version);
        try {
            path = confServer.createNode(path, null);
        } catch (KeeperException.NodeExistsException e) {
            LOGGER.error(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return path;
    }
    @CrossOrigin
    @RequestMapping("/create_group_node")
    public String creatVersionNode(@PathParam("env") String env,@PathParam("project") String project,
                                   @PathParam("version") String version, @PathParam("group") String group){
        String path = ZKPaths.makePath(ConstantClass.CONF_ROOT_NODE, env,project, version, group);
        try {
            path = confServer.createNode(path, null);
        } catch (KeeperException.NodeExistsException e) {
            LOGGER.error(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return path;
    }
    @CrossOrigin
    @RequestMapping(value = "/create_key_node", method = RequestMethod.POST)
    public String createKeyNode(@RequestParam("env") String env,@RequestParam("project") String project,@RequestParam("version") String version,
                                @RequestParam("group") String group, @RequestParam("key") String key,@RequestParam("value") String value){
        String path = ZKPaths.makePath(ConstantClass.CONF_ROOT_NODE, env,project, version, group, key);
        try {
            return confServer.createNode(path, value);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return path;
    }
    public String deleteKeyNode(@PathParam("env") String env,@PathParam("project") String project,
                                @PathParam("version") String version, @PathParam("group") String group,
                                @PathParam("key") String key) {
        String path = ZKPaths.makePath(ConstantClass.CONF_ROOT_NODE, env,project, version, group, key);
        try {
            confServer.deleteNode(path);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return path;
    }

    public String updateKeyNode(@PathParam("env") String env,@PathParam("project") String project,
                                @PathParam("version") String version, @PathParam("group") String group,
                                @PathParam("oldkey") String oldKey, @PathParam("newkey") String newKey,
                                @PathParam("value") String value) {
        String oldKeyNodePath = ZKPaths.makePath(ConstantClass.CONF_ROOT_NODE, env,project, version, group, oldKey);
        try {
            confServer.updateKeyValue(oldKeyNodePath, newKey, value);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return oldKeyNodePath;
    }

}
