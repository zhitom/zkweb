$(function(){
		//var lastpathid=null;
		initDataGrid();
		$('#millisecs').numberspinner({
			onSpinUp:function(){
				ZkStateRefresh(null)
			},
			onSpinDown:function(){
				ZkStateRefresh(null)
			}
		});
		$('#locale').combobox({
			width:"70px",
			height:"18px",
			panelHeight:"70px",
			editable:false,
			onSelect:function(record){
				var isdelwelcome=$('#isfirstopen').attr('value')
				if(isdelwelcome == "0"){
					$('#isfirstopen').attr('value',"1");
				}else{
					setLanguage(record)
				}			
			}
		});
		console.log('getLanguageFromCookie(null)='+getLanguageFromCookie(null))
		$('#locale').combobox('select',getLanguageFromCookie(null));
	});

	function setFilter(node){
		var _cfg = $('#zkweb_zkcfg').datagrid('getSelected');
		var _index = $('#zkweb_zkcfg').datagrid('getRowIndex',_cfg);
		$('#selectIndex').val(_index)
		//alert($('#filterValue').val()+"\n"+encodeURI($('#filterValue').val())+"\n"+encodeURI(encodeURI($('#filterValue').val())))
		$('#zkweb_zkcfg').datagrid('options').url='zkcfg/queryZkCfg?whereSql='+encodeURI(encodeURI($('#filterValue').val())).trim()
		$('#zkweb_zkcfg').datagrid("reload");
		//alert(_index)
		
	}
	function initDataGrid(){
		$('#zkweb_zkcfg').datagrid({
			sortName:"DESC",
			striped:true,
//			onSortColumn:function(sort, order){
//				$('#zkweb_zkcfg').datagrid('selectRow',$('#selectIndex').val());
//			},
			columns: [
                [
                    {field: "ID",title: "ID",sortable:true }
                    , {field: "DESC",title: "DESC",sortable:true }//, title: "姓名", width: 100,sortable:true,order:"desc"}
                    , {field: "CONNECTSTR",title: "CONNECTSTR",sortable:true}
                    , {field: "SESSIONTIMEOUT",title: "SESSIONTIMEOUT",sortable:true}
                ]
            ],
			remoteSort:false,
			onLoadSuccess:function(data){
				$('#zkweb_zkcfg').datagrid('selectRow',$('#selectIndex').val());
				//$('#zkweb_zkcfg').datagrid('highlightRow',$('#selectIndex').val());
			},
			onClickRow:function(rowIndex, rowData){
				//alert(rowData.DESC);
				initTree(rowIndex,rowData);
				//设置选中
				var _index = $('#zkweb_zkcfg').datagrid('getRowIndex',rowData);
				$('#selectIndex').val(_index)
				$('#zkweb_zkcfg').datagrid('selectRow',$('#selectIndex').val());
				//重置tab页面
				$('#zkTab').tabs('select',rowData.DESC);
				var isdelwelcome=$('#isDelWelcomeTab').attr('value')
				if(isdelwelcome == "0"){
					$('#zkTab').tabs('close',0);
					$('#isDelWelcomeTab').attr('value',"1");
				}				
				//重置状态页面
				var url="zk/queryZKJMXInfo?cacheId="+rowData.ID+"&simpleFlag="+$('#zkstate_showtype_form input[name="showtype"]:checked ').val();
				//scrollbarSize: 0
				$('#jmxpropertygrid').propertygrid({
					url: url
					});
				$('#zkstate_showtype_form input[name="id"]').val(rowData.ID);
				$('#jmxpanel').remove()
				refreshConnectState(rowData);
			},
			url:'zkcfg/queryZkCfg?whereSql='+encodeURI(encodeURI($('#filterValue').val())).trim()
		});
	}
	function refreshConnectState(row){
		//return;
		$.post("zk/queryZKOk", {cacheId:row.ID},function(data){$('#connstaterefresh').html(data);});
		if($('#lastRefreshConn').val()){
			clearInterval($('#lastRefreshConn').val());
			$('#lastRefreshConn').val(null);
		}
		ref = setInterval(function(){
			$.post("zk/queryZKOk", {cacheId:row.ID},function(data){$('#connstaterefresh').html(data);});
			},5000);
		$('#lastRefreshConn').val(ref);
	}
	function ZkStateShowTypeChange(node){
    	//alert('hhh:'+node.name+','+node.value);
    	var url="zk/queryZKJMXInfo?cacheId="+$('#zkstate_showtype_form input[name="id"]').val()+"&simpleFlag="+$('#zkstate_showtype_form input[name="showtype"]:checked ').val();
		//alert(url)
		$('#jmxpropertygrid').propertygrid({
					url: url,
					});
    	//$('#jmxpropertygrid').propertygrid('options').url=url;
		//$('#jmxpropertygrid').propertygrid('reload')
		//$('zkstate_showtype_form').submit(function(e){
		//	  alert("Submitted");
		//});
    }
	function ZkStateRefresh(node){
		var url="zk/queryZKJMXInfo?cacheId="+$('#zkstate_showtype_form input[name="id"]').val()+"&simpleFlag="+$('#zkstate_showtype_form input[name="showtype"]:checked ').val();
		//alert(url)
		var secs=$('#zkstate_showtype_form input[name="millisecs"]').val()*1000;
		var ref = null;
		if(secs>0){
			var refreshObject=$('#zkstate_showtype_form input[name="refreshObject"]').val();
			if(refreshObject){
				clearInterval(refreshObject);
				$('#zkstate_showtype_form input[name="refreshObject"]').val(null);
			}
			ref = setInterval(function(){
				$('#jmxpropertygrid').propertygrid({
					url: url,
					});
				},secs);
			$('#zkstate_showtype_form input[name="refreshObject"]').val(ref);
		}else{
			$('#jmxpropertygrid').propertygrid({
				url: url,
				});
			var refreshObject=$('#zkstate_showtype_form input[name="refreshObject"]').val();
			if(refreshObject){
				clearInterval(refreshObject);
				$('#zkstate_showtype_form input[name="refreshObject"]').val(null);
			}
		}
		
	}

    /****************************************************************************************************************************/
    
    function initTree(rowIndex,row){
    	$('#zkTab').tabs({
    		onSelect:function(title,index){
    			//$.messager.alert('提示','title='+title+',index='+index);
				var pp = $(this).tabs('getTab',title);
				var pa = pp.panel('options');
				//$.messager.alert('提示','id='+pa.id);
				if(pp!=null&&pa.id!=null){
					$('#zkweb_zkcfg').datagrid('unselectAll');
					$('#zkweb_zkcfg').datagrid('selectRow',pa.id);
					var _cfg = $('#zkweb_zkcfg').datagrid('getSelected');
					initOneTree(pa.id,_cfg);
					$('#zkstate_showtype_form input[name="id"]').val(_cfg.ID);
					ZkStateRefresh(null);
					refreshConnectState(_cfg);
					var rootNode=$('#zkTree').tree('getRoot');
	    			if(rootNode==null){
	    				localeMessager('alert','title','提示','connstatedisconn','连接未建立！');
	    			}
				}
    	    }
    	});
    	initOneTree(rowIndex,row);
    }
    function initOneTree(rowIndex,row){
    	cacheId=row.ID
    	
    	$('#zkTree').tree({
    		checkbox: false,
    		url: "zk/queryZnode?id="+encodeURI(encodeURI('0'))+"&cacheId="+cacheId,
    		animate:true,
    		lines:true,
    		onLoadSuccess: function(node, data){//node为加载完毕的父节点,data是加载好的子节点列表
    			//下面的代码是递归全部展开整颗树，暂不使用
//     			var t = $(this);  
// 		        if(data){  
// 			        $(data).each(function(index,d){  
// 					        if(this.state == 'closed'){  
// 					            t.tree('expandAll');  
// 				        }
// 					  });
// 		        }

    			var rootNode=$(this).tree('getRoot');
    			if(rootNode==null){
    				localeMessager('alert','title','提示','connstatedisconn','连接未建立！');
    				return;
    			}
    			var curNode=$(this).tree('getSelected');
    			if(!node){
    				node=rootNode;
    			}
    			var trueselectnode=node
    			//$(this).tree('expandTo',trueselectnode.target);
     			$(this).tree('expand',trueselectnode.target);
				$(this).tree('select',trueselectnode.target);
				if(node != rootNode&&trueselectnode==curNode){
					return;
    			}
				var _path = "/";
		    	if (trueselectnode){  
		            if (trueselectnode.attributes&&trueselectnode.attributes.path){  
		            	 _path = trueselectnode.attributes.path ;
		            }  
		        }
    			var tab = $('#zkTab').tabs('getTab',row.DESC);
    			//$.messager.alert('提示','enter onLoadSuccess()！');
    			if(tab != null){
					
//					$('#zkTab').tabs('update', {
//						tab: tab,
//						options: {
//							title: row.DESC, //node.text,
//							href: "zk/queryZnodeInfo?path="+encodeURI(encodeURI(_path))+"&cacheId="+cacheId  
//						}
//					});
					tab.panel('refresh',"zk/queryZnodeInfo?path="+encodeURI(encodeURI(_path))+"&cacheId="+cacheId);
				}else {
    				$('#zkTab').tabs('add',{
    					id:rowIndex,
    					title:row.DESC,  
    					closable:true,
    					href: "zk/queryZnodeInfo?path="+encodeURI(encodeURI(_path))+"&cacheId="+cacheId
    	        	}); 
    				
    			}
    		},
    		onContextMenu: function(e,node){  
                e.preventDefault();  
                $(this).tree('select',node.target);  
                $('#mm').menu('show',{  
                    left: e.pageX,  
                    top: e.pageY  
                });  
            },
    		onClick:function(node){
    			$(this).tree('reload',node.target);
    			//var tab = $('#zkTab').tabs('getSelected');
    			var tab = $('#zkTab').tabs('getTab',row.DESC);
    			//var index = $('#zkTab').tabs('getTabIndex',tab);
    			//alert(index);
    			//$.messager.alert('提示',tab+'enter onClickSuccess()！'+node.attributes.path);
    			var _path="/"
				if (node&&node.attributes)
                	 _path = node.attributes.path ;
    			if(tab != null){
    				//tab.title=node.text;
    				//tab.panel('refresh', "zk/queryZnodeInfo?path="+node.attributes.path);
//    				$.messager.alert('提示',tab+'enter onClickSuccess()！'+"zk/queryZnodeInfo?path="+encodeURI(encodeURI(node.attributes.path))+"&cacheId="+cacheId );
//    				$('#zkTab').tabs('update', {
//    					tab: tab,
//    					options: {
//    						title: row.DESC, //node.text,
//    						href: "zk/queryZnodeInfo?path="+encodeURI(encodeURI(node.attributes.path))+"&cacheId="+cacheId  
//    					}
//    				});
    				tab.panel('refresh',"zk/queryZnodeInfo?path="+encodeURI(encodeURI(_path))+"&cacheId="+cacheId);
    			}
    			else {
    				$('#zkTab').tabs('add',{
    					id:rowIndex,
    					title:row.DESC,  
    					closable:true,
    					href: "zk/queryZnodeInfo?path="+encodeURI(encodeURI(_path))+"&cacheId="+cacheId
    	        	});     				
    			}
    			
    		},
    		onBeforeExpand:function(node,param){
    			
    			if(node&&node.attributes != null){
    				$(this).tree('options').url = "zk/queryZnode?id="+encodeURI(encodeURI(node.id))+"&path="+encodeURI(encodeURI(node.attributes.path))+"&cacheId="+cacheId;
    				//console.log('onBeforeExpand：node.id='+node.id)
    			}
    		}
    	});
    	
    }


    function remove(){
    	 var node = $('#zkTree').tree('getSelected');  
    	 if(!node){
    		localeMessager('alert','title',"提示",'nochoosenode','没选择节点！');
      		return;
    	 }
    	 var parentNode = $('#zkTree').tree('getParent',node.target);  
    	 if(!parentNode){parentNode=$('#zkTree').tree('getRoot');  }
         if (node){  
         	if('/'==node.attributes.path || '/zookeeper'==node.attributes.path || '/zookeeper/quota'==node.attributes.path){
         		localeMessager('alert','title','提示','canntdelnode','不能删除此节点！');
         		return;
         	}
         	
         	 var _cfg = $('#zkweb_zkcfg').datagrid('getSelected');
             
             if(_cfg){
            	 
            	 	localeMessager('confirm','title','提示', 'none','delete this node and all children-nodes: '+node.attributes.path+' ?', function(r){  
             	    if (r){  
                         //var s = node.text;  
                         if (node.attributes){  
                         	 _path = node.attributes.path ;
                         	 $.post("zk/deleteNode", {path: _path,cacheId:_cfg.ID},
                     				function(data){
                     					//alert("Data Loaded: " + data);
                         		 		localeMessager('alert','title','提示', 'none',data+',Delete Done!');
                     					//
                     					//var tab = $('#zkTab').tabs('getTab',0);
                     					//alert(tab.title);
                     					//
                     					node=parentNode;
                     					$('#zkTree').tree('reload',node.target);
                     					$('#zkTree').tree('collapse',node.target);
                        				$('#zkTree').tree('expand',node.target);
                        				$('#zkTree').tree('select',node.target);
                        				//var tab = $('#zkTab').tabs('getSelected');
                        				var tab = $('#zkTab').tabs('getTab',_cfg.DESC);
                        				cacheId=_cfg.ID;
                        				//localeMessager('alert','title','提示','none','enter refreshtab()！'+node.attributes.path);
//                        				$('#zkTab').tabs('update', {
//                        					tab: tab,
//                        					options: {
//                        						title: _cfg.DESC, //node.text,
//                        						href: "zk/queryZnodeInfo?path="+encodeURI(encodeURI(node.attributes.path))+"&cacheId="+cacheId  
//                        					}
//                        				});
                        				tab.panel('refresh',"zk/queryZnodeInfo?path="+encodeURI(encodeURI(node.attributes.path))+"&cacheId="+cacheId);
                     				}
                         	);
                         	
                         }  
             	    }  
                 }); 
             }

         }else {
        	 localeMessager('alert','title','提示','choosenode','请选择一个节点');
         };
    }  

    function collapseAll(){  
        var node = $('#zkTree').tree('getSelected');  
        $('#zkTree').tree('collapse',node.target);  
        collapseAllRecur(node);
    }  
    function collapseAllRecur(node){  
    	var childNodeList=$('#zkTree').tree('getChildren',node.target);
    	if(!childNodeList){
    		return;
    	}
    	for(var i=0;i<childNodeList.length;i++){
    		$('#zkTree').tree('collapse',childNodeList[i].target);  
    		collapseAllRecur(childNodeList[i]);
    	}        
    }  
    function collapse(){  
        var node = $('#zkTree').tree('getSelected');  
        $('#zkTree').tree('collapse',node.target);  
    }  
    
    function refreshtree(){  
        var node = $('#zkTree').tree('getSelected');  
        $('#zkTree').tree('reload',node.target);//onLoadSuccess 里边 实现
        $('#zkTree').tree('collapse',node.target);
        $('#zkTree').tree('expand',node.target);  
    }  


    function expandAll(){  
        var node = $('#zkTree').tree('getSelected'); 
        $('#zkTree').tree('expand',node.target);  
        //$('#zkTree').tree('reload',node.target);
        //var data=$('#zkTree').tree('getData',node.target);
        expandAllRecur(node);
    }
    
    function expandAllRecur(node){ 
    	//下面支持部分全部展开的功能尚未实现完全
//     	var t = $("#zkTree");  
//         if(data){  
// 	        $(data).each(function(index,d){  
// 			        if(d.state == 'closed'){  
// 			            t.tree('expand',d);  
// 		        }
// 			  });
//         }
//         return
    	//$('#zkTree').tree('reload',node.target);
    	var data=$('#zkTree').tree('getData',node.target);
    	//var rootdata=$('#zkTree').tree('options').data;
    	var childNodeList=data.children;//$('#zkTree').tree('getChildren',trueNode.target);
    	if(!childNodeList){
    		return;
    	}
    	//if(Array.isArray(childNodeList)){
    	//	$.messager.alert('提示', 'expand all isArray true,len='+childNodeList.length+","+data.id+","+data.text+","+data.state+","+writeObj(rootdata));
    	//}else{
    	//	$.messager.alert('提示', 'expand all isArray false');
    	//}
    	for(var i=0;i<childNodeList.length;i++){
    		//$('#zkTree').tree('reload',childNodeList[i].target);
    		$('#zkTree').tree('expand',childNodeList[i].target);  
    		//$.messager.alert('提示', 'expand all path='+i+childNodeList[i].attributes.path);
    		expandAllRecur(childNodeList[i]);
    	}        
    }  
    function expand(){  
        var node = $('#zkTree').tree('getSelected'); 
        $('#zkTree').tree('expand',node.target);  
    }

    function addzkNode(){
    	var _path = "/";
    	var node = $('#zkTree').tree('getSelected');  
        if (node){  
            //var s = node.text;  
            if (node.attributes){  
            	 _path = node.attributes.path ;
            }  
        }else{
        	localeMessager('alert','title','提示','nochoosenode','没选择节点！');
	  		return;
		 }
        _nodeName = $('#zkNodeName').val();
        
        var _cfg = $('#zkweb_zkcfg').datagrid('getSelected');
        
        if(_cfg){
        	$.post("zk/createNode", { nodeName: _nodeName, path: _path,cacheId:_cfg.ID},
    			function(data){
    				//alert("Data Loaded: " + data);
        			localeMessager('alert','title','提示', 'none',data+',Add Done!');
    				$('#zkweb_add_node').window('close');
    				$('#zkTree').tree('reload',node.target);
    				$('#zkTree').tree('collapse',node.target);
    				$('#zkTree').tree('expand',node.target);
        			
    			}
        	);
        }else {
        	localeMessager('alert','title','提示','mustchoosecfg','你必须选择一个配置');
        }
    }
    /****************************************************************************************************************************/
   
    function saveCfg(){
    	$.messager.progress();
	   	$('#zkweb_add_cfg_form').form('submit', {
	   		url: 'zkcfg/addZkCfg',
	   		onSubmit: function(){
	   			var isValid = $(this).form('validate');
	   			if (!isValid){
	   				$.messager.progress('close');	// hide progress bar while the form is invalid
	   			}
	   			return isValid;	// return false will stop the form submission
	   		},
	   		success: function(data){
	   			localeMessager('alert','title','提示', 'none',data+',Save Done!');
		    	$('#zkweb_zkcfg').datagrid("reload");
		    	$('#zkweb_add_cfg').window('close');
	   			$.messager.progress('close');	// hide progress bar while submit successfully
	   			$('#zkTab').tabs('close',0);
	   		}
	   	});
    }
    
    function updateCfg(){
    	 
    	$.messager.progress();
	   	$('#zkweb_up_cfg_form').form('submit', {
	   		url: 'zkcfg/updateZkCfg',
	   		onSubmit: function(){
	   			var isValid = $(this).form('validate');
	   			if (!isValid){
	   				$.messager.progress('close');	// hide progress bar while the form is invalid
	   			}
	   			return isValid;	// return false will stop the form submission
	   		},
	   		success: function(data){
	   			localeMessager('alert','title','提示', 'none',data+',Update Done!');
		    	$('#zkweb_zkcfg').datagrid("reload");
		    	$('#zkweb_up_cfg').window('close');
	   			$.messager.progress('close');	// hide progress bar while submit successfully
	   			$('#zkTab').tabs('close',0);
	   		}
	   	});
    }
    
    function openUpdateWin(){
    	
    	var _cfg = $('#zkweb_zkcfg').datagrid('getSelected');
    	if(_cfg){
    		$('#zkweb_up_cfg').window('open');
        	
        	$('#zkweb_up_cfg_form').form("load","zkcfg/queryZkCfgById?id="+_cfg.ID);
    	}else {
    		localeMessager('alert','title','提示', 'chooserow','请选择一条记录');
    	}
    	
    }
    
    function openDelWin(){
    	
    	var _cfg = $('#zkweb_zkcfg').datagrid('getSelected');
    	if(_cfg){
    		
    		localeMessager('confirm','title','提示','confirmdelcfg','确认删除这个配置吗?', function(r){  
                if (r){  
                    //alert('confirmed:'+r);  
					$.get('zkcfg/delZkCfg',{id:_cfg.ID},function(data){
						localeMessager('alert','title','提示', 'none',data+',Delete Done!');
					});
					$('#zkweb_zkcfg').datagrid("reload");
					$('#zkTab').tabs('close',0);
                    //$('#zkweb_up_cfg').window('open');
                	//$('#zkweb_up_cfg_form').form("load","zkcfg/queryZkCfgById?id="+_cfg.ID);
                }  
            }); 
    		//$('#zkweb_zkcfg').datagrid('selectRow',0);
    	}else {
    		localeMessager('alert','title','提示', 'chooserow','请选择一条记录');
    	}
    }