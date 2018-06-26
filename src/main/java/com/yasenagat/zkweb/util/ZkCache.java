package com.yasenagat.zkweb.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZkCache {
	
	private static Map<String, ZkManager> _cache = new ConcurrentHashMap<String, ZkManager>();
	
	public static ZkManager put(String key,ZkManager zk){
		return _cache.put(key, zk);
	}
	
	public static ZkManager get(String key){
		return _cache.get(key);
	}
	
	public static ZkManager remove(String key){
		return _cache.remove(key);
	}
	
	public static int size(){
		return _cache.size();
	}

	public static Map<String, ZkManager> get_cache() {
		return _cache;
	}

	public static void set_cache(Map<String, ZkManager> _cache) {
		ZkCache._cache = _cache;
	}

	public static void init(ZkCfgManager cfgManager){
		
		List<Map<String, Object>> list = cfgManager.query();
		
		for(Map<String , Object> m : list){
			ZkCache.put(m.get("ID").toString(), ZkManagerImpl.createZk().connect(m.get("CONNECTSTR").toString(), Integer.parseInt(m.get("SESSIONTIMEOUT").toString())));
		}
	}
	
}
