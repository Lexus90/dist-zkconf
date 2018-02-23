package com.cw.zkconf.common.utils;

import com.google.common.collect.Sets;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EhcacheUtil {

    private static class SingletonHolder {
        private final static CacheManager cacheManager =  CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("zk-conf",
                        CacheConfigurationBuilder
                                .newCacheConfigurationBuilder(String.class, String.class,
                                        ResourcePoolsBuilder.heap(10)))
                .build();
        static { cacheManager.init();}

    }

    public static CacheManager getCacheManagerInstance() {
        return SingletonHolder.cacheManager;
    }

    public static <K, V>  Cache<K, V> createCache(String cacheName, Class<K> keyType, Class<V> valueType){
        CacheManager cacheManager = getCacheManagerInstance();
        try {
            return cacheManager.createCache(cacheName, cacheConfigurationBuilder(keyType, valueType));
        } catch (IllegalArgumentException e){
            return cacheManager.getCache(cacheName, keyType, valueType);
        }
    }

    public static <K, V>  Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        CacheManager cacheManager = getCacheManagerInstance();
        try {
            return cacheManager.getCache(cacheName, keyType, valueType);
        } catch (IllegalArgumentException e){
            return cacheManager.createCache(cacheName, cacheConfigurationBuilder(keyType, valueType));
        }
    }

    public static <K, V> Map<K, V> toMap(Cache<K, V> cache){
        Iterator<Cache.Entry<K, V>> iterator = cache.iterator();
        Set<K> ketSet = Sets.newHashSet();
        while (iterator.hasNext()){
            Cache.Entry e = iterator.next();
            ketSet.add((K) e.getKey());
        }
        return cache.getAll(ketSet);
    }

    public static <K, V> V getValue (Cache<K, V> cache, K key){
        if (cache != null){
            return cache.get(key);
        }
        return null;
    }

    private static <K, V> CacheConfiguration<K, V> cacheConfigurationBuilder(Class<K> keyType, Class<V> valueType) {
        return CacheConfigurationBuilder.newCacheConfigurationBuilder(keyType, valueType, ResourcePoolsBuilder.heap(10)).build();
    }



}
