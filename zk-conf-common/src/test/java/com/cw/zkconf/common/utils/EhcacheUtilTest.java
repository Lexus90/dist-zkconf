package com.cw.zkconf.common.utils;


import org.ehcache.Cache;
import org.junit.Test;

public class EhcacheUtilTest {

    @Test
    public void createCache() throws Exception {

    }

    @Test
    public void getCache() throws Exception {
        Cache<String, String> cache = EhcacheUtil.createCache("mycache", String.class, String.class);
//        cache.putIfAbsent("name", "caowei");
        cache.put("name", "caowei");
        String name = cache.get("name");
        System.out.println("name:"+name);

        Cache<String, String> cache1 = EhcacheUtil.getCache("mycache", String.class, String.class);
        cache1.get("name");
        System.out.println("name2="+name);
    }

    @Test
    public void getValue() throws Exception {

    }
}
