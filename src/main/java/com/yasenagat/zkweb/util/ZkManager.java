package com.yasenagat.zkweb.util;

import java.util.List;
import java.util.Map;

public interface ZkManager {

	//public ZkManagerImpl connect(Properties p);

	public ZkManagerImpl connect(String host, int timeout);

	public boolean disconnect();
	
	public void reconnect();

	public List<String> getChildren(String path);

	public String getData(String path);
	public String getData(String path, boolean isPrintLog);

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
	public static class PropertyPanel{
		private String name;
		private String value;
		private String group;
		private String editor="text";
		public String getName() {
			return name;
		}
		public String getValue() {
			return value;
		}
		public String getGroup() {
			return group;
		}
		public String getEditor() {
			return editor;
		}
		public void setInfo(String name,String value,String group) {
			this.name = name;
			this.value = value;
			this.group = group;
		}
		public void setEditor(String editor) {
			this.editor = editor;
		}
	}
	public List<PropertyPanel> getJMXInfo(boolean simpleFlag);

}
