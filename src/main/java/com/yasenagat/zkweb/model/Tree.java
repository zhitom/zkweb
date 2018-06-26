package com.yasenagat.zkweb.model;

import java.util.List;
import java.util.Map;

public class Tree {

	private int id;
	private String text;
	//state: node state, 'open' or 'closed', default is 'open'. When set to 'closed', the node have children nodes and will load them from remote site
	private String state; 
	
	public static final String STATE_OPENNED = "open";
	public static final String STATE_CLOSED = "closed";
	private List<Tree> childern;
	//checked: Indicate whether the node is checked selected.
	private Boolean checked;
	//attributes: custom attributes can be added to a node
	private Map<String, Object> attributes;
	
	public Tree() {
		// TODO Auto-generated constructor stub
	}
	
	
	public Tree(int id, String text, String state, List<Tree> childern,
			Map<String, Object> attributes) {
		super();
		this.id = id;
		this.text = text;
		this.state = state;
		this.childern = childern;
		this.attributes = attributes;
	}


	public Tree(int id, String text, String state, List<Tree> childern) {
		super();
		this.id = id;
		this.text = text;
		this.state = state;
		this.childern = childern;
	}


	public Tree(int id, String text, String state, List<Tree> childern,
			boolean checked, Map<String, Object> attributes) {
		super();
		this.id = id;
		this.text = text;
		this.state = state;
		this.childern = childern;
		this.checked = checked;
		this.attributes = attributes;
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public List<Tree> getChildern() {
		return childern;
	}
	public void setChildern(List<Tree> childern) {
		this.childern = childern;
	}
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
}
