package com.yasenagat.zkweb.util;

import java.util.List;
import java.util.Map;

public interface ZkCfgManager {

	public boolean init();
	public boolean add(String desc,String connectStr,String sessionTimeOut);
	public boolean add(String id,String desc,String connectStr,String sessionTimeOut);
	public List<Map<String, Object>> query(int page, int rows, String whereSql);
	public boolean update(String id,String desc,String connectStr,String sessionTimeOut);
	public boolean delete(String id);
	public Map<String, Object> findById(String id);
	public int count();
	static String initSql = "CREATE TABLE IF NOT EXISTS ZK(ID VARCHAR PRIMARY KEY, DESC VARCHAR, CONNECTSTR VARCHAR, SESSIONTIMEOUT VARCHAR)";
	//static String initSql = "drop table ZK";
	public void destroyPool();
	public List<Map<String, Object>> query();
	
}
