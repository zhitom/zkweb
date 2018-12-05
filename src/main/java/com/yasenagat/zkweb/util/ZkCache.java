package com.yasenagat.zkweb.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZkCache {
	private static final Logger log = LoggerFactory.getLogger(ZkCache.class);
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
		log.info("zk info size={}",list.size());
		ZkManager zkManager;
		for(Map<String , Object> m : list){
			zkManager=ZkCache.get(m.get("ID").toString());
			if(zkManager==null) {
				log.info("zk info: id={},connectstr={},timeout={}",m.get("ID"),m.get("CONNECTSTR"),m.get("SESSIONTIMEOUT"));
				ZkCache.put(m.get("ID").toString(), ZkManagerImpl.createZk().connect(m.get("CONNECTSTR").toString(), Integer.parseInt(m.get("SESSIONTIMEOUT").toString())));
			}else {
				log.info("zk(exists) info: id={},connectstr={},timeout={}",m.get("ID"),m.get("CONNECTSTR"),m.get("SESSIONTIMEOUT"));
				zkManager.reconnect();
			}
			
		}
	}
	
}
