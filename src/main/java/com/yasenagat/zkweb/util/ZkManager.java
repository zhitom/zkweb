package com.yasenagat.zkweb.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface ZkManager {

	//public ZkManagerImpl connect(Properties p);

	public ZkManagerImpl connect(String host, int timeout);

	public boolean disconnect();
	
	public void reconnect();

	public List<String> getChildren(String path);

	public String getData(String path);

	public Map<String, String> getNodeMeta(String nodePath);

	public List<Map<String, String>> getACLs(String nodePath);
	
	public boolean createNode(String path, String nodeName, String data);

	public boolean deleteNode(String nodePath);

	public boolean setData(String nodePath, String data);

	public long getNodeId(String nodePath);

	public enum P {

		host, sessionTimeOut
	}

	public enum Meta {

		// ACL_Version,
		// Creation_Time,
		// Children_Version,
		// Creation_ID,
		// Data_Length,
		// Ephemeral_Owner,
		// Last_Modified_Time,
		// Modified_ID,
		// Number_of_Children,
		// Node_ID,
		// Data_Version
		czxid, mzxid, ctime, mtime, version, cversion, aversion, ephemeralOwner, dataLength, numChildren, pzxid
	}

	public enum Acl {

		scheme, id, perms
	}

	public List<Object> getJMXInfo(boolean simpleFlag);

}
