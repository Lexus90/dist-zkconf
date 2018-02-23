package com.cw.zkconf.service.zookeeper;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;

@ToString(exclude = {"ExponentialBackoffRetry", "RetryPolicy"})
public class ZkConfProfile {
    private static final ExponentialBackoffRetry DEFAULT_RETRY_POLICY =
            new ExponentialBackoffRetry(1000, 3);
    @Getter @Setter
    private final String connectStr;
    @Getter @Setter
    private final String rootNode;
    @Getter @Setter
    private String version;
    @Getter @Setter
    private final RetryPolicy retryPolicy;

    public ZkConfProfile(String connectStr, String rootNode) {
        this(connectStr, rootNode, null, DEFAULT_RETRY_POLICY);
    }
    public ZkConfProfile(String connectStr, String rootNode, String version) {
        this(connectStr, rootNode, version, DEFAULT_RETRY_POLICY);
    }

    public ZkConfProfile(String connectStr, String rootNode, RetryPolicy retryPolicy) {
        this(connectStr, rootNode, null, retryPolicy);
    }

    public ZkConfProfile(String connectStr, String rootNode, String version, RetryPolicy retryPolicy) {
        this.connectStr = connectStr;
        this.rootNode = rootNode;
        this.version = version;
        this.retryPolicy = retryPolicy;
    }

    public String getRootVersionNode(){
        if (Strings.isNullOrEmpty(version)) {
            return rootNode;
        }
        return ZKPaths.makePath(rootNode, version);
    }
}
