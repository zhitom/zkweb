package com.yasenagat.zkweb.web;

import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yasenagat.zkweb.util.ZkCache;
import com.yasenagat.zkweb.util.ZkCfgFactory;
import com.yasenagat.zkweb.util.ZkCfgManager;
import com.yasenagat.zkweb.util.ZkManagerImpl;

@Controller
@RequestMapping("/zkcfg")
public class ZkCfgController {
	
	private static final Logger log = LoggerFactory.getLogger(ZkCfgController.class);

	static ZkCfgManager zkCfgManager = ZkCfgFactory.createZkCfgManager();
	
	@RequestMapping(value="/queryZkCfg")
	public @ResponseBody Map<String, Object> queryZkCfg(
			@RequestParam(required=false) int page,
			@RequestParam(required=false) int rows,@RequestParam(required=false) String whereSql){
		
		try {
			log.info(new Date()+"");
			Map<String, Object> _map = new HashMap<String, Object>();
			_map.put("rows", zkCfgManager.query(page,rows,URLDecoder.decode(whereSql,"utf-8")));
			_map.put("total", zkCfgManager.count());
			return _map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
	@RequestMapping(value="/addZkCfg",produces="text/html;charset=UTF-8")
	public @ResponseBody String addZkCfg(
			@RequestParam(required=false) String desc,
			@RequestParam(required=false) String connectstr,
			@RequestParam(required=false) String sessiontimeout){
		
		try {
			//String id = UUID.randomUUID().toString().replaceAll("-", "");
			String id = UUID.randomUUID().toString();
			if(ZkCfgFactory.createZkCfgManager().add(id,desc, connectstr, sessiontimeout)){
				ZkCache.put(id, ZkManagerImpl.createZk().connect(connectstr,Integer.parseInt(sessiontimeout)));
			};
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
			return "添加失败";
		}
		return "添加成功";
	}
	
	@RequestMapping(value="/queryZkCfgById")
	public @ResponseBody Map<String, Object> queryZkCfg(
			@RequestParam(required=false) String id){
		
		try {
			return ZkCfgFactory.createZkCfgManager().findById(id);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
	@RequestMapping(value="/updateZkCfg",produces="text/html;charset=UTF-8")
	public @ResponseBody String updateZkCfg(
			@RequestParam(required=true) String id,
			@RequestParam(required=false) String desc,
			@RequestParam(required=false) String connectstr,
			@RequestParam(required=false) String sessiontimeout){
		
		try {
			if(ZkCfgFactory.createZkCfgManager().update(id,desc, connectstr, sessiontimeout)){
				ZkCache.put(id, ZkManagerImpl.createZk().connect(connectstr,Integer.parseInt(sessiontimeout)));
			};
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
			return "保存失败";
		}
		return "保存成功";
	}
	
	@RequestMapping(value="/delZkCfg",produces="text/html;charset=UTF-8")
	public @ResponseBody String delZkCfg(
			@RequestParam(required=true) String id){
		
		try {
			ZkCfgFactory.createZkCfgManager().delete(id);
			ZkCache.remove(id);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
			return "删除失败";
		}
		return "删除成功";
	}
}
