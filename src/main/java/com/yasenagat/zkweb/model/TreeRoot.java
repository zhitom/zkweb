package com.yasenagat.zkweb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeRoot extends ArrayList<Tree>{
	private static final long serialVersionUID = -8094096973144418349L;
	Map<String, Object> atr = new HashMap<String, Object>();
	private Tree root = new Tree(0,"/",Tree.STATE_CLOSED,null,atr);
	public TreeRoot() {
		atr.put("path", "/");
		this.add(root);
	}
	
	public void setChildern(List<Tree> childern){
		
		this.root.setChildern(childern);
	}
}
