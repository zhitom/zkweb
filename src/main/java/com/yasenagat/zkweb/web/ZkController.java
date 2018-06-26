package com.yasenagat.zkweb.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.jr.ob.JSON;
import com.yasenagat.zkweb.model.Tree;
import com.yasenagat.zkweb.model.TreeRoot;
import com.yasenagat.zkweb.util.ZkCache;
import com.yasenagat.zkweb.util.ZkCfgFactory;
import com.yasenagat.zkweb.util.ZkCfgManagerImpl;
import com.yasenagat.zkweb.util.ZkManagerImpl;

@Controller
@RequestMapping("/zk")
public class ZkController implements DisposableBean{
	
	private static final Logger log = LoggerFactory.getLogger(ZkController.class);
	
	@RequestMapping(value="/queryZnodeInfo",produces="text/html;charset=UTF-8")
	public String queryzNodeInfo(
			@RequestParam(required=false) String path,
			Model model,
			@RequestParam(required=true) String cacheId
			){
		
		try {
			path = URLDecoder.decode(path,"utf-8");
			log.info("queryzNodeInfo1111 : " + path);
			if(path != null){
				model.addAttribute("zkpath", path);
				model.addAttribute("path",path);
				model.addAttribute("cacheId", cacheId);
				String data=ZkCache.get(cacheId).getData(path);
				if(data==null) {
					model.addAttribute("data", "");
					model.addAttribute("acls", "");
					return "info";
				}
				model.addAttribute("data", ZkCache.get(cacheId).getData(path).trim());
				model.mergeAttributes(ZkCache.get(cacheId).getNodeMeta(path));
				model.addAttribute("acls", ZkCache.get(cacheId).getACLs(path));			
			}
			log.info("model : " + model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "info";
	}
	@RequestMapping(value="/queryZKOk")
	public @ResponseBody String queryZKOk(Model model,@RequestParam(required=true) String cacheId){
		String exmsg="<font color='red'>Disconnected Or Exception</font>";
		try {
			if(ZkCache.get(cacheId).getData("/")!=null)
				return "<font color='blue'>Connected</font>";
			else
				return exmsg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return exmsg;
	}
	@RequestMapping(value="/queryZKJMXInfo")
	public @ResponseBody List<Object> queryZKJMXInfo(
			@RequestParam(required=true) String simpleFlag,
			@RequestParam(required=true) String cacheId
			){
		
		try {
//			model.mergeAttributes(ZkCache.get(cacheId).getJMXInfo());
//			//model.addAttribute("acls", ZkCache.get(cacheId).getACLs(path));
//			//model.addAttribute("path",path);
//			model.addAttribute("cacheId", cacheId);
			List<Object> result=ZkCache.get(cacheId).getJMXInfo(Integer.parseInt(simpleFlag)==0?false:true);
			log.info("simpleFlag={},cacheId={},result : {}",simpleFlag,cacheId,JSON.std.asString(result));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Collections.emptyList();
	}
	
	@RequestMapping(value="/queryZnode")
	public @ResponseBody List<Tree> query(
			@RequestParam(required=false) String id,
			@RequestParam(required=false) String path,
			@RequestParam(required=true) String cacheId
			){
		
		log.info("id : {}",id);
		log.info("path : {}",path);
		log.info("cacheId : {}",cacheId);
		
		TreeRoot root = new TreeRoot();
		
		if(path == null){
			
		}else if("/".equals(path)){
			root.remove(0);
			List<String> pathList = ZkCache.get(cacheId).getChildren(null);
			log.info("list {}",pathList);
			for(String p : pathList){
				Map<String, Object> atr = new HashMap<String, Object>();
				atr.put("path", "/"+p);
				Tree tree = new Tree(0,p,Tree.STATE_CLOSED,null,atr);
				root.add(tree);
			}
		}else {
			root.remove(0);
			try {
				path = URLDecoder.decode(path,"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<String> pathList = ZkCache.get(cacheId).getChildren(path);
			for(String p : pathList){
				Map<String, Object> atr = new HashMap<String, Object>();
				atr.put("path", path+"/"+p);
				Tree tree = new Tree(0,p,Tree.STATE_CLOSED,null,atr);
				root.add(tree);
			}
		}

		return root;
	}
	
	@RequestMapping(value="/saveData",produces="text/html;charset=UTF-8")
	public @ResponseBody String saveData(
			@RequestParam() String path,
			@RequestParam() String data,
			@RequestParam(required=true) String cacheId
			){
		
		try {
			log.info("data:{}",data);
			return ZkCache.get(cacheId).setData(path, data)==true ? "保存成功" : "保存失败";
		} catch (Exception e) {
			log.info("Error : {}",e.getMessage());
			e.printStackTrace();
			return "保存失败! Error : " +e.getMessage();
		}
		
	}
	
	@RequestMapping(value="/createNode",produces="text/html;charset=UTF-8")
	public @ResponseBody String createNode(
			@RequestParam() String path,
			@RequestParam() String nodeName,
			@RequestParam(required=true) String cacheId
			){
		
		try {
			log.info("path:{}",path);
			log.info("nodeName:{}",nodeName);
			return ZkCache.get(cacheId).createNode(path, nodeName, "")==true ? "保存成功" : "保存失败";
		} catch (Exception e) {
			log.info("Error : {}",e.getMessage());
			e.printStackTrace();
			return "保存失败! Error : " +e.getMessage();
		}
		
	}
	
	@RequestMapping(value="/deleteNode",produces="text/html;charset=UTF-8")
	public @ResponseBody String deleteNode(
			@RequestParam() String path,
			@RequestParam(required=true) String cacheId
			){
		
		try {
			log.info("path:{}",path);
			return ZkCache.get(cacheId).deleteNode(path)==true ? "删除成功" : "删除失败";
		} catch (Exception e) {
			log.info("Error : {}",e.getMessage());
			e.printStackTrace();
			return "删除失败! Error : " +e.getMessage();
		}
		
	}


	@Override
	public void destroy() throws Exception {
		log.info("destroyZkCfgManager()...");
		ZkCfgFactory.createZkCfgManager().destroyPool();
	} 
	
}
