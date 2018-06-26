import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.Test;

import com.yasenagat.zkweb.util.H2Util;
import com.yasenagat.zkweb.util.ZkCfgFactory;
import com.yasenagat.zkweb.util.ZkCfgManager;
import com.yasenagat.zkweb.util.ZkManagerImpl;

public class T {

	@Test
	public void t() {

		try {
			Properties p = new Properties();
			p.setProperty("host", "192.168.20.111:2181");
			p.setProperty("sessionTimeOut", "3000");
			ZkManagerImpl zk = ZkManagerImpl.createZk();
			//zk.connect(p);
			System.out.println(zk.getChildren(null));
			;
			// System.out.println(zk.getNodeMeta("/root"));;
			System.out.println(zk.getACLs("/aaa"));
			// List<String> list = zk.getChildren(null);
			// System.out.println(list);
			// for(String p1 : list){
			// // System.out.println(zk.getChildren("/"+p1));
			// System.out.println("/"+p1+" data : "+zk.getData("/"+p1));;
			// }
			// zk.disconnect();
			// Map<String, String> map = zk.getNodeMeta("/");
			//
			// for (String key : map.keySet()) {
			// System.out.println(key + " : " + map.get(key));
			// }
			// List<Map<String, String>> l = zk.getACLs("/");
			// for (Map<String, String> m : l) {
			// System.out.println(m);
			// }

			try {
				// zk.setData("/root", new
				// String("哈哈123".getBytes("utf-8"),"utf-8"));
				// String d = zk.getData("/root");
				// System.out.println(d);

				// zk.createNode("/3f", "4f","4f");
				// zk.deleteNode("/ffff");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testH2() {

		try {
			// JdbcConnectionPool cp =
			// JdbcConnectionPool.create("jdbc:h2:~/test",
			// "sa", "sa");
			// Connection conn = cp.getConnection();
			// conn.createStatement().execute("CREATE TABLE IF NOT EXISTS TEST2(ID INT PRIMARY KEY, NAME VARCHAR);");
			// for (int i = 6; i < 10; i++) {
			// conn = cp.getConnection();
			// java.sql.Statement state = conn.createStatement();
			// ResultSet rs = state.executeQuery("select * from TEST2");
			// while(rs.next()){
			// int id = rs.getInt(1);
			// String name = rs.getString(2);
			// System.out.println(id);
			// System.out.println(name);
			// }
			// // .execute("INSERT INTO TEST VALUES("+i+", 'Hello World');");
			// conn.close();
			// }
			// cp.dispose();
			ResultSetHandler<Object[]> h = new ResultSetHandler<Object[]>() {
				public Object[] handle(ResultSet rs) throws SQLException {
					if (!rs.next()) {
						return null;
					}

					ResultSetMetaData meta = rs.getMetaData();
					int cols = meta.getColumnCount();
					Object[] result = new Object[cols];

					for (int i = 0; i < cols; i++) {
						result[i] = rs.getObject(i + 1);
					}

					return result;
				}
			};

			ResultSetHandler<List<Map<String, Object>>> h2 = new ResultSetHandler<List<Map<String, Object>>>() {

				public List<Map<String, Object>> handle(ResultSet rs)
						throws SQLException {

					
					List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
					
					ResultSetMetaData meta = rs.getMetaData();
					Map<String, Object> map = null;
					int cols = meta.getColumnCount();
					System.out.println("cols :"+cols);
					while(rs.next()){
						map = new HashMap<String, Object>();
						for(int i = 0 ; i < cols ;i++){
							System.out.println("----------------------");
							System.out.println(i);
							System.out.println(meta.getColumnName(i+1));
							System.out.println(rs.getObject(i+1));
							map.put(meta.getColumnName(i+1), rs.getObject(i+1));
						}
						list.add(map);
					}
					
					return list;
				}
			};

			QueryRunner run = new QueryRunner(H2Util.getDataSource());

			List<Map<String, Object>> result = run.query("SELECT * FROM TEST ", h2);
			System.out.println(result.size());
			for (Map<String, Object> o : result) {
				System.out.println(o.get("ID"));
				System.out.println(o.get("NAME"));
			}

			// run.update("CREATE TABLE IF NOT EXISTS TEST3(ID INT PRIMARY KEY, NAME VARCHAR);");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testZkCfg(){
		
		ZkCfgManager zkCfgManager = ZkCfgFactory.createZkCfgManager();
		
		try {
			zkCfgManager.init();
			
//			for(int i = 0 ; i < 9 ; i ++){
//				
//				zkCfgManager.add("test_" +i, "localhost:2181", "50000"+i);
//			}
			
//			boolean b = zkCfgManager.add("test", "localhost:2181", "50000");
//			System.out.println(b);
			System.out.println(zkCfgManager.query());;
			System.out.println(zkCfgManager.query(2, 4,""));
//			System.out.println(zkCfgManager.update("20cfbbe0eb4045afaecd9bf8dce58a44", "haha", "str", "123"));
//			System.out.println(zkCfgManager.findById("20cfbbe0eb4045afaecd9bf8dce58a44"));
//			System.out.println(zkCfgManager.delete("337cdbc2fbbf4890bb3184e6b618a589"));
			System.out.println(zkCfgManager.count());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOther(){
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("1", "2");
		map.put("1", "3");
		
		System.out.println(map);
	}
}
