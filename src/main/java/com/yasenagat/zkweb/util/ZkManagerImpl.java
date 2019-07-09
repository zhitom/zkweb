package com.yasenagat.zkweb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.yasenagat.zkweb.util.ZkManagerImpl.ZkConnectInfo.ZkHostPort;

public class ZkManagerImpl implements Watcher,ZkManager {

	private ZooKeeper zk=null;
	private ServerStatusByCMD serverStatusByCMD;
	private ZkConnectInfo zkConnectInfo=new ZkConnectInfo();
	private final String ROOT = "/";
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(ZkManagerImpl.class);
//	private static final ZkManagerImpl _instance = new ZkManagerImpl();
	public ZkManagerImpl(){
		new ZkJMXInfo(zkConnectInfo);
		serverStatusByCMD=new ServerStatusByCMD(zkConnectInfo);
	}
	
	public static ZkManagerImpl createZk(){
		
		return new ZkManagerImpl();
	}
	public static class ZkConnectInfo{
		private String connectStr;
		private int timeout;
		public static class ZkHostPort{
			private String host;
			private int port;
			public String getHost() {
				return host;
			}
			public void setHost(String host) {
				this.host = host;
			}
			public int getPort() {
				return port;
			}
			public void setPort(int port) {
				this.port = port;
			}
		}
		public String getConnectStr() {
			return connectStr;
		}
		public void setConnectStr(String connectStr) {
			this.connectStr = connectStr;
		}
		public List<ZkHostPort> getConnectInfo(){
			List<ZkHostPort> retList=new ArrayList<>();
			for(String hostIp:connectStr.split(",")) {
				ZkHostPort zkHostPort=new ZkHostPort();
				String[] hostIpArray=hostIp.split(":");
				zkHostPort.setHost(hostIpArray[0]);
				if(hostIpArray.length==1) {
					zkHostPort.setPort(2181);
				}else {
					zkHostPort.setPort(Integer.parseInt(hostIpArray[1]));
				}
				retList.add(zkHostPort);
			}
			return retList;
		}
		public int getTimeout() {
			return timeout;
		}
		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}
	}
	private interface ZkState{
		List<PropertyPanel> state() throws IOException, MalformedObjectNameException,  
        InstanceNotFoundException, IntrospectionException, ReflectionException;
		List<PropertyPanel> simpleState() throws IOException, MalformedObjectNameException,  
        InstanceNotFoundException, IntrospectionException, ReflectionException;
	};
	
	public static class ServerStatusByCMD implements ZkState{  
		private ZkConnectInfo zkConnectInfo;
		
		private static final ImmutableMap<String, ImmutableList<String>> cmdKeys=new ImmutableMap.Builder<String, ImmutableList<String>>()
				.put(
				"srvr",ImmutableList.of(
						"Zookeeper version","Latency min/avg/max","Received","Sent",
						"Connections","Outstanding","Zxid","Mode","Node"))
				.put("conf",ImmutableList.of()).put("cons",ImmutableList.of())
				.put("envi",ImmutableList.of()).put("ruok",ImmutableList.of())
				.put("wchs",ImmutableList.of()).put("wchc",ImmutableList.of())
				.put("wchp",ImmutableList.of()).put("mntr",ImmutableList.of()).build();
		private static final ImmutableMap<String, String> cmdFindStr=new ImmutableMap.Builder<String, String>()
				.put("srvr",": ")
				.put("conf","=").put("cons","(")
				.put("envi","=").put("ruok","")
				.put("wchs","").put("wchc","")
				.put("wchp","").put("mntr"," ").build();
	    public ServerStatusByCMD(ZkConnectInfo zkConnectInfo) {
	    	this.zkConnectInfo=zkConnectInfo;
		}
	    private List<PropertyPanel> executeOneCmdByWch(Socket sock,String cmd,String group) throws IOException{
	    	BufferedReader reader = null;  
	    	List<PropertyPanel> retList=new ArrayList<>();
	    	try {
            reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));  
            
            String line;  
            String lines="";
            PropertyPanel propertyPanel=new PropertyPanel();
            while ((line = reader.readLine()) != null) {  
            	List<String> keys=cmdKeys.get(cmd);
            	if(keys==null) {
            		continue;
            	}
            	lines=lines+line;
            }  
            propertyPanel=new PropertyPanel();
			propertyPanel.setInfo(cmd, lines.trim(),group);
        	retList.add(propertyPanel);
            return retList;
	    	}finally {
	    		if (reader != null) {  
	                reader.close();  
	            } 
			}
            
	    }
	    private List<PropertyPanel> executeOneCmd(Socket sock,String cmd,String group) throws IOException{
	    	BufferedReader reader = null;  
	    	List<PropertyPanel> retList=new ArrayList<>();
	    	try {
            reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));  
            
            String line;  
            PropertyPanel propertyPanel=new PropertyPanel();
            while ((line = reader.readLine()) != null) {  
            	List<String> keys=cmdKeys.get(cmd);
            	if(keys==null) {
            		continue;
            	}
            	for(int i=0;i<keys.size();i++) {
            		if(cmd.equals("ruok")) {
            			propertyPanel=new PropertyPanel();
        				propertyPanel.setInfo(keys.get(i), line.trim(),group);
                    	retList.add(propertyPanel);
                    	continue;
            		}
            		if(cmd.equals("conf")||cmd.equals("cons")||cmd.equals("envi")||cmd.equals("mntr")) {
        				propertyPanel=new PropertyPanel();
        				String[] strArray=line.split(cmdFindStr.get(cmd));
        				if(cmd.equals("cons")) {
        					String vString=line.replaceFirst(strArray[0]+cmdFindStr.get(cmd), "").trim();
        					vString=vString.substring(0,vString.length()-1);
        					if(vString.isEmpty()) {
        						continue;
        					}
        					propertyPanel.setInfo(strArray[0], vString ,group);
        				}else {
        					String vString=line.replaceFirst(strArray[0]+cmdFindStr.get(cmd), "").trim();
        					if(vString.isEmpty()) {
        						continue;
        					}
        					propertyPanel.setInfo(strArray[0],vString,group);
        				}
                    	retList.add(propertyPanel);
            			continue;
            		}
            		if (line.indexOf(keys.get(i)+cmdFindStr.get(cmd)) != -1) { 
        				propertyPanel=new PropertyPanel();
        				String vString=line.replaceFirst(keys.get(i)+cmdFindStr.get(cmd), "").trim();
    					if(vString.isEmpty()) {
    						continue;
    					}
                    	propertyPanel.setInfo(keys.get(i), vString,group);
                    	retList.add(propertyPanel);
            		}
            	}
            }  
            return retList;
	    	}finally {
	    		if (reader != null) {  
	                reader.close();  
	            } 
			}
            
	    }
	    private List<PropertyPanel> executeOneCmdSimple(Socket sock,String cmd,String group) throws IOException{
	    	BufferedReader reader = null;  
	    	List<PropertyPanel> retList=new ArrayList<>();
	    	try {
	    	reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));  
            
            String line;  
            PropertyPanel propertyPanel=new PropertyPanel();
            while ((line = reader.readLine()) != null) {  
            	List<String> keys=cmdKeys.get(cmd);
            	if(keys==null) {
            		continue;
            	}
            	for(int i=0;i<keys.size();i++) {
            		if (line.indexOf(keys.get(i)+cmdFindStr.get(cmd)) != -1) { 
            			if(keys.get(i).equals("Mode")) {
        					propertyPanel=new PropertyPanel();
        					String vString=line.replaceFirst(keys.get(i)+cmdFindStr.get(cmd), "").trim();
        					if(vString.isEmpty()) {
        						continue;
        					}
                        	propertyPanel.setInfo(keys.get(i), vString,group);
                        	retList.add(propertyPanel);
                        	return retList;
        				}                        	
            			
                    }
            	}
            }  
            return retList;
	    	}finally {
	    		if (reader != null) {  
	                reader.close();  
	            } 
			}
	    }
	    public List<PropertyPanel> state()  throws IOException, MalformedObjectNameException,  
    	InstanceNotFoundException, IntrospectionException, ReflectionException{  
	    	return innerState(false);
	    }
	    public List<PropertyPanel> simpleState() throws MalformedObjectNameException, InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
			return innerState(true);
		}  
		public List<PropertyPanel> innerState(boolean simpleFlag)  throws IOException, MalformedObjectNameException,  
        	InstanceNotFoundException, IntrospectionException, ReflectionException{  
	        String host;  
	        int port;
	        List<PropertyPanel> retList=new ArrayList<>();
	        String group;
			for (ZkHostPort zkHostPort : zkConnectInfo.getConnectInfo()) {
				host = zkHostPort.getHost();
				port = zkHostPort.getPort();
				Socket sock = null;

				// cmd="srvr";
				for (String cmd : cmdKeys.keySet()) {
					try {
						sock = new Socket(host, port);
						OutputStream outstream = sock.getOutputStream();
						// 通过Zookeeper的四字命令获取服务器的状态
						outstream.write(cmd.getBytes());
						outstream.flush();
						group = host + "." + port + "." + cmd;
						log.info("group=" + group);
						if (simpleFlag) {
							retList.addAll(executeOneCmdSimple(sock, cmd, group));
							break;
						} else {
							if (cmd.equals("wchs") || cmd.equals("wchc") || cmd.equals("wchp")) {
								retList.addAll(executeOneCmdByWch(sock, cmd, group));
							} else {
								retList.addAll(executeOneCmd(sock, cmd, group));
							}
						}
					} catch (Exception e) {
						sock = null;
						e.printStackTrace();
						log.error("zk open error for state(four cmd): echo {} |nc {} {}",cmd,host, port,e);
						break;
					} finally {
						if (sock != null) {
							// sock.shutdownOutput();
							sock.close();
						}
					}
				}
			}
			return retList;  
	    }
		
	}  
	public static class ZkJMXInfo {  
	    private JMXConnector connectorJMX;  
	    public ZkJMXInfo(ZkConnectInfo zkConnectInfo) {
		}

		/** 
	     * @param args 
	     * @throws IOException 
	     * @throws MalformedObjectNameException 
	     * @throws InstanceNotFoundException 
	     * @throws ReflectionException 
	     * @throws IntrospectionException 
	     */  
	    public List<Object> state()  throws IOException, MalformedObjectNameException,  
    	InstanceNotFoundException, IntrospectionException, ReflectionException{  
	    	return innerState(false);
	    }
	    public List<Object> simpleState() throws MalformedObjectNameException, InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
			return innerState(true);
		}  
	    public List<Object> innerState(boolean simpleFlag) throws IOException, MalformedObjectNameException,  
	        InstanceNotFoundException, IntrospectionException, ReflectionException {  
	    	List<Object> retList=new ArrayList<>();
	        PropertyPanel propertyPanel=new PropertyPanel();
	        propertyPanel.setInfo("jmx","unsupported" ,"jmx");
	        retList.add(propertyPanel);
	        return retList;
	        /*
	        OperatingSystemMXBean osbean = ManagementFactory.getOperatingSystemMXBean(); 
	        ///TODO
	        System.out.println("体系结构:" + osbean.getArch());//操作系统体系结构  
	        System.out.println("处理器核数:" + osbean.getAvailableProcessors());///核数  
	        System.out.println("名字:" + osbean.getName());//名字  
	  
	        System.out.println(osbean.getVersion());//操作系统版本  
	        ThreadMXBean threadBean=ManagementFactory.getThreadMXBean();  
	        System.out.println("活动线程:" + threadBean.getThreadCount());//总线程数  
	  
	        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();  
	        CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();  
	        System.out.println("===========");  
	  
	        // 通过 MBeanServer间接地访问 MXBean 接口  
	        MBeanServerConnection mbsc = createMBeanServer("192.168.1.100", "9991", "controlRole", "123456");  
	  
	        // 操作系统  
	        ObjectName os = new ObjectName("java.lang:type=OperatingSystem");  
	        System.out.println("体系结构:" + getAttribute(mbsc, os, "Arch"));//体系结构  
	        System.out.println("处理器核数:" + getAttribute(mbsc, os, "AvailableProcessors"));//核数  
	        System.out.println("总物理内存:" + getAttribute(mbsc, os, "TotalPhysicalMemorySize"));//总物理内存  
	        System.out.println("空闲物理内存:" + getAttribute(mbsc, os, "FreePhysicalMemorySize"));//空闲物理内存  
	        System.out.println("总交换空间:" + getAttribute(mbsc, os, "TotalSwapSpaceSize"));//总交换空间  
	        System.out.println("空闲交换空间:" + getAttribute(mbsc, os, "FreeSwapSpaceSize"));//空闲交换空间  
	  
	        System.out.println("操作系统:" + getAttribute(mbsc, os, "Name")+ getAttribute(mbsc, os, "Version"));//操作系统  
	        System.out.println("提交的虚拟内存:" + getAttribute(mbsc, os, "CommittedVirtualMemorySize"));//提交的虚拟内存  
	        System.out.println("系统cpu使用率:" + getAttribute(mbsc, os, "SystemCpuLoad"));//系统cpu使用率  
	        System.out.println("进程cpu使用率:" + getAttribute(mbsc, os, "ProcessCpuLoad"));//进程cpu使用率  
	  
	        System.out.println("============");//  
	        // 线程  
	        ObjectName Threading = new ObjectName("java.lang:type=Threading");  
	        System.out.println("活动线程:" + getAttribute(mbsc, Threading, "ThreadCount"));// 活动线程  
	        System.out.println("守护程序线程:" + getAttribute(mbsc, Threading, "DaemonThreadCount"));// 守护程序线程  
	        System.out.println("峰值:" + getAttribute(mbsc, Threading, "PeakThreadCount"));// 峰值  
	        System.out.println("启动的线程总数:" + getAttribute(mbsc, Threading, "TotalStartedThreadCount"));// 启动的线程总数  
	        ThreadMXBean threadBean2 = ManagementFactory.newPlatformMXBeanProxy  
	                (mbsc, ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);  
	        System.out.println("活动线程:" + threadBean2.getThreadCount());// 活动线程  
	        ThreadMXBean threadBean3 = ManagementFactory.getThreadMXBean();  
	        System.out.println("本地活动线程:" + threadBean3.getThreadCount());// 本地活动线程  
	  
	        System.out.println("============");//  
	        ObjectName Compilation = new ObjectName("java.lang:type=Compilation");  
	        System.out.println("总编译时间 毫秒:" + getAttribute(mbsc, Compilation, "TotalCompilationTime"));// 总编译时间 毫秒  
	  
	        System.out.println("============");//  
	        ObjectName ClassLoading = new ObjectName("java.lang:type=ClassLoading");  
	        System.out.println("已加载类总数:" + getAttribute(mbsc, ClassLoading, "TotalLoadedClassCount"));// 已加载类总数  
	        System.out.println("已加装当前类:" + getAttribute(mbsc, ClassLoading, "LoadedClassCount"));// 已加装当前类  
	        System.out.println("已卸载类总数:" + getAttribute(mbsc, ClassLoading, "UnloadedClassCount"));// 已卸载类总数  
	  
	  
	        System.out.println("==========================================================");//  
	        // http://zookeeper.apache.org/doc/r3.4.6/zookeeperJMX.html  
	        // org.apache.ZooKeeperService:name0=ReplicatedServer_id1,name1=replica.1,name2=Follower  
	        ObjectName replica = new ObjectName("org.apache.ZooKeeperService:name0=ReplicatedServer_id1,name1=replica.1");  
	        System.out.println("replica.1运行状态:" + getAttribute(mbsc, replica, "State"));// 运行状态  
	  
	        mbsc = createMBeanServer("192.168.1.100", "9992", "controlRole", "123456");  
	        System.out.println("==============节点树对象===========");  
	        ObjectName dataTreePattern = new ObjectName("org.apache.ZooKeeperService:name0=ReplicatedServer_id?,name1=replica.?,name2=*,name3=InMemoryDataTree");  
	        Set<ObjectName> dataTreeSets = mbsc.queryNames(dataTreePattern, null);  
	        Iterator<ObjectName> dataTreeIterator = dataTreeSets.iterator();  
	        // 只有一个  
	        while (dataTreeIterator.hasNext()) {  
	            ObjectName dataTreeObjectName = dataTreeIterator.next();  
	            DataTreeMXBean dataTree = JMX.newMBeanProxy(mbsc, dataTreeObjectName, DataTreeMXBean.class);  
	            System.out.println("节点总数:" + dataTree.getNodeCount());// 节点总数  
	            System.out.println("Watch总数:" + dataTree.getWatchCount());// Watch总数  
	            System.out.println("临时节点总数:" + dataTree.countEphemerals());// Watch总数  
	            System.out.println("节点名及字符总数:" + dataTree.approximateDataSize());// 节点全路径和值的总字符数  
	  
	            Map<String, String> dataTreeMap = dataTreeObjectName.getKeyPropertyList();  
	            String replicaId = dataTreeMap.get("name1").replace("replica.", "");  
	            String role = dataTreeMap.get("name2");// Follower,Leader,Observer,Standalone  
	            String canonicalName = dataTreeObjectName.getCanonicalName();  
	            int roleEndIndex = canonicalName.indexOf(",name3");  
	  
	            ObjectName roleObjectName = new ObjectName(canonicalName.substring(0, roleEndIndex));  
	            System.out.println("==============zk服务状态===========");  
	            ZooKeeperServerMXBean ZooKeeperServer = JMX.newMBeanProxy(mbsc, roleObjectName, ZooKeeperServerMXBean.class);  
	            System.out.println(role + " 的IP和端口:" + ZooKeeperServer.getClientPort());// IP和端口  
	            System.out.println(role + " 活着的连接数:" + ZooKeeperServer.getNumAliveConnections());// 连接数  
	            System.out.println(role + " 未完成请求数:" + ZooKeeperServer.getOutstandingRequests());// 未完成的请求数  
	            System.out.println(role + " 接收的包:" + ZooKeeperServer.getPacketsReceived());// 收到的包  
	            System.out.println(role + " 发送的包:" + ZooKeeperServer.getPacketsSent());// 发送的包  
	            System.out.println(role + " 平均延迟（毫秒）:" + ZooKeeperServer.getAvgRequestLatency());  
	            System.out.println(role + " 最大延迟（毫秒）:" + ZooKeeperServer.getMaxRequestLatency());  
	  
	            System.out.println(role + " 每个客户端IP允许的最大连接数:" + ZooKeeperServer.getMaxClientCnxnsPerHost());  
	            System.out.println(role + " 最大Session超时（毫秒）:" + ZooKeeperServer.getMaxSessionTimeout());  
	            System.out.println(role + " 心跳时间（毫秒）:" + ZooKeeperServer.getTickTime());  
	            System.out.println(role + " 版本:" + ZooKeeperServer.getVersion());// 版本  
	            // 三个重置操作  
//	            ZooKeeperServer.resetLatency(); //重置min/avg/max latency statistics  
//	            ZooKeeperServer.resetMaxLatency(); //重置最大延迟统计  
//	            ZooKeeperServer.resetStatistics(); // 重置包和延迟所有统计  
	  
	  
	            System.out.println("==============所有客户端的连接信息===========");  
	            ObjectName connectionPattern = new ObjectName("org.apache.ZooKeeperService:name0=ReplicatedServer_id?,name1=replica.?,name2=*,name3=Connections,*");  
	            Set<ObjectName> connectionSets = mbsc.queryNames(connectionPattern, null);  
	            List<ObjectName> connectionList = new ArrayList<ObjectName>(connectionSets.size());  
	            connectionList.addAll(connectionSets);  
	            Collections.sort(connectionList);  
	            for (ObjectName connectionON : connectionList) {  
	                System.out.println("=========================");  
	                ConnectionMXBean connectionBean = JMX.newMBeanProxy(mbsc, connectionON, ConnectionMXBean.class);  
	                System.out.println(" IP+Port:" + connectionBean.getSourceIP());//  
	                System.out.println(" SessionId:" + connectionBean.getSessionId());//  
	                System.out.println(" PacketsReceived:" + connectionBean.getPacketsReceived());// 收到的包  
	                System.out.println(" PacketsSent:" + connectionBean.getPacketsSent());// 发送的包  
	                System.out.println(" MinLatency:" + connectionBean.getMinLatency());//  
	                System.out.println(" AvgLatency:" + connectionBean.getAvgLatency());//  
	                System.out.println(" MaxLatency:" + connectionBean.getMaxLatency());//  
	                System.out.println(" StartedTime:" + connectionBean.getStartedTime());//  
	                System.out.println(" EphemeralNodes:" + connectionBean.getEphemeralNodes().length);//  
	                System.out.println(" EphemeralNodes:" + Arrays.asList(connectionBean.getEphemeralNodes()));//  
	                System.out.println(" OutstandingRequests:" + connectionBean.getOutstandingRequests());//  
	                  
	                //connectionBean.resetCounters();  
	                //connectionBean.terminateConnection();  
	                //connectionBean.terminateSession();  
	            }  
	        }  
	        // close connection  
	        if (connectorJMX != null) {  
	            connectorJMX.close();  
	        }
			return retList;  */
	    }  
	  
	    /** 
	     * 建立连接 
	     * 
	     * @param ip 
	     * @param jmxport 
	     * @return 
	     */  
	    public MBeanServerConnection createMBeanServer(String ip, String jmxport, String userName, String password) {  
	        try {  
	            String jmxURL = "service:jmx:rmi:///jndi/rmi://" + ip + ":"  
	                    + jmxport + "/jmxrmi";  
	            // jmxurl  
	            JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);  
	  
	            Map<String, String[]> map = new HashMap<String, String[]>();  
	            String[] credentials = new String[] { userName, password };  
	            map.put("jmx.remote.credentials", credentials);  
	            connectorJMX = JMXConnectorFactory.connect(serviceURL, map);  
	            MBeanServerConnection mbsc = connectorJMX.getMBeanServerConnection();  
	            return mbsc;  
	  
	        } catch (IOException ioe) {  
	            ioe.printStackTrace();  
	            System.err.println(ip + ":" + jmxport + " 连接建立失败");  
	        }  
	        return null;  
	    }  
	  
	    /** 
	     * 使用MBeanServer获取对象名为[objName]的MBean的[objAttr]属性值 
	     * <p> 
	     * 静态代码: return MBeanServer.getAttribute(ObjectName name, String attribute) 
	     * 
	     * @param mbeanServer 
	     *            - MBeanServer实例 
	     * @param objName 
	     *            - MBean的对象名 
	     * @param objAttr 
	     *            - MBean的某个属性名 
	     * @return 属性值 
	     */  
	    @SuppressWarnings("unused")
		private String getAttribute(MBeanServerConnection mbeanServer,  
	            ObjectName objName, String objAttr) {  
	        if (mbeanServer == null || objName == null || objAttr == null)  
	            throw new IllegalArgumentException();  
	        try {  
	            return String.valueOf(mbeanServer.getAttribute(objName, objAttr));  
	        } catch (Exception e) {  
	            return null;  
	        }  
	    }  
	}  
//	public boolean connect(Properties p) {
//
//		try {
//			return this.connect(p.getProperty(P.host.toString()), (Integer
//					.valueOf(p.getProperty(P.sessionTimeOut.toString()))));
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	};
//
//	private boolean connect(String host, int timeout) {
//		try {
//			if (null == zk) {
//				zk = new ZooKeeper(host, timeout, this);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
	
//	public ZkManagerImpl connect() {
//
//		try {
//			Properties p = ConfigUtil.getP();
//			return this.connect(p.getProperty(P.host.toString()), (Integer
//					.valueOf(p.getProperty(P.sessionTimeOut.toString()))));
//		} catch (Exception e) {
//			e.printStackTrace();
//			return this;
//		}
//	};
//	
//	public ZkManagerImpl connect(Properties p) {
//
//		try {
//			return this.connect(p.getProperty(P.host.toString()), (Integer
//					.valueOf(p.getProperty(P.sessionTimeOut.toString()))));
//		} catch (Exception e) {
//			e.printStackTrace();
//			return this;
//		}
//	};
	@Override
	public List<PropertyPanel> getJMXInfo(boolean simpleFlag) {
		try {
			if(simpleFlag)
				return serverStatusByCMD.simpleState();
			//return jmxInfo.state();
			return serverStatusByCMD.state();
		} catch (MalformedObjectNameException | InstanceNotFoundException | IntrospectionException | ReflectionException
				| IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	public ZkManagerImpl connect(String host, int timeout) {
		try {
			zkConnectInfo.setConnectStr(host);
			zkConnectInfo.setTimeout(timeout);
			if (null == zk) {
				zk = new ZooKeeper(host, timeout, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	public boolean disconnect() {
		if (zk != null) {
			try {
				zk.close();
				zk = null;
				return true;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			log.error("zk is not init");
		}
		return false;
	};

	public List<String> getChildren(String path){

		try {
			return zk.getChildren(path == null ? ROOT : path, false);
		} catch (Exception e) {
			e.printStackTrace();
			reconnect();
		}
		return new ArrayList<String>();
	}

	public String getData(String path) {
		return getData(path,true);
	}
	public String getData(String path,boolean isPrintLog) {
		try {
			Stat s = zk.exists(path, false);
			if (s != null) {
				byte b[] = zk.getData(path, false, s);
				if(null == b){
					return "";
				}
				String pathContent=new String(zk.getData(path, false, s));
				if(isPrintLog)log.info("data[{}] : {}",path,pathContent);
				return pathContent;
			}
		} catch (Exception e) {
			e.printStackTrace();
			reconnect();
		}
		return null;
	}

	public Map<String, String> getNodeMeta(String nodePath) {
		Map<String, String> nodeMeta = new LinkedHashMap<String, String>();
		try {
			if (nodePath.length() == 0) {
				nodePath = ROOT;
			}
			Stat s = zk.exists(nodePath, false);
			if (s != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String timeStr;
				nodeMeta.put(Meta.aversion.toString(),
						String.valueOf(s.getAversion()));
				timeStr = sdf.format(new Date(s.getCtime()));
				nodeMeta.put(Meta.ctime.toString(),
						timeStr+" ["+String.valueOf(s.getCtime())+"]");
				nodeMeta.put(Meta.cversion.toString(),
						String.valueOf(s.getCversion()));
				nodeMeta.put(Meta.czxid.toString(),
						String.valueOf(s.getCzxid()));
				nodeMeta.put(Meta.dataLength.toString(),
						String.valueOf(s.getDataLength()));
				nodeMeta.put(Meta.ephemeralOwner.toString(),
						String.valueOf(s.getEphemeralOwner()));
				timeStr = sdf.format(new Date(s.getMtime()));
				nodeMeta.put(Meta.mtime.toString(),
						timeStr+" ["+String.valueOf(s.getMtime())+"]");
				nodeMeta.put(Meta.mzxid.toString(),
						String.valueOf(s.getMzxid()));
				nodeMeta.put(Meta.numChildren.toString(),
						String.valueOf(s.getNumChildren()));
				nodeMeta.put(Meta.pzxid.toString(),
						String.valueOf(s.getPzxid()));
				nodeMeta.put(Meta.version.toString(),
						String.valueOf(s.getVersion()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("",e);
			reconnect();
		}
		return nodeMeta;
	}

	public List<Map<String, String>> getACLs(String nodePath) {
		List<Map<String, String>> returnACLs = new ArrayList<Map<String, String>>();
		try {
			if (nodePath.length() == 0) {
				nodePath = ROOT;
			}
			Stat s = zk.exists(nodePath, false);
			if (s != null) {
				List<ACL> acls = zk.getACL(nodePath, s);
				for (ACL acl : acls) {
					Map<String, String> aclMap = new LinkedHashMap<String, String>();
					aclMap.put(Acl.scheme.toString(), acl.getId().getScheme());
					aclMap.put(Acl.id.toString(), acl.getId().getId());
					StringBuilder sb = new StringBuilder();
					int perms = acl.getPerms();
					boolean addedPerm = false;
					if ((perms & Perms.READ) == Perms.READ) {
						sb.append("Read");
						addedPerm = true;
					}
					if (addedPerm) {
						sb.append(", ");
					}
					if ((perms & Perms.WRITE) == Perms.WRITE) {
						sb.append("Write");
						addedPerm = true;
					}
					if (addedPerm) {
						sb.append(", ");
					}
					if ((perms & Perms.CREATE) == Perms.CREATE) {
						sb.append("Create");
						addedPerm = true;
					}
					if (addedPerm) {
						sb.append(", ");
					}
					if ((perms & Perms.DELETE) == Perms.DELETE) {
						sb.append("Delete");
						addedPerm = true;
					}
					if (addedPerm) {
						sb.append(", ");
					}
					if ((perms & Perms.ADMIN) == Perms.ADMIN) {
						sb.append("Admin");
						addedPerm = true;
					}
					aclMap.put(Acl.perms.toString(), sb.toString());
					returnACLs.add(aclMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("",e);
			reconnect();
		}
		return returnACLs;
	}

	public boolean createNode(String path, String nodeName,String data) {
		try {
			String p;
			if(ROOT.equals(path)){
				p = path + nodeName;
			}else {
				p = path + "/" + nodeName;
			}
			Stat s = zk.exists(p, false);
			if (s == null)
			{
				zk.create(p, data.getBytes(),
						Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("",e);
			reconnect();
		}
		return false;
	}

	public boolean deleteNode(String nodePath) {
		try {
			Stat s = zk.exists(nodePath, false);
			if (s != null) {
				List<String> children = zk.getChildren(nodePath, false);
				for (String child : children) {
					String node = nodePath + "/" + child;
					deleteNode(node);
				}
				zk.delete(nodePath, -1);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("",e);
			reconnect();
		}
		return false;
	}

	public boolean setData(String nodePath, String data) {
		try {
			zk.setData(nodePath, data.getBytes("utf-8"), -1);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("",e);
			reconnect();
		}
		return false;
	}

	public void process(WatchedEvent arg0) {
		// do nothing
	}

	public long getNodeId(String nodePath) {
		
		try {
			Stat s = zk.exists(nodePath, false);
			if(s != null){
				return s.getPzxid();
			}
		} catch (Exception e) {
			e.printStackTrace();
			reconnect();
		} 

		return 0l;
	}

	@Override
	public void reconnect(){
		if(zk != null) {
			try {
			zk.close();
			}catch (Exception e) {
			}
			try {
			zk=new ZooKeeper(this.zkConnectInfo.getConnectStr(), this.zkConnectInfo.getTimeout(),this);
			}catch (Exception e) {
				e.printStackTrace();
				zk=null;
			}
		}
		
	}


}
