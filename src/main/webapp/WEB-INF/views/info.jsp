<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<jsp:include page="head.jsp"></jsp:include>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style type="text/css">
#htd { font-size: 12px;width:100px }
#tdv { font-size: 12px;width:100% }
#tdhpx { font-size: 12px;width:80px }
#tdh { font-size: 12px;width:200px }
#tabsize { border:1px solid DarkGray;width:99%;height:100%;cellspacing:1px }
th {
     font-size: 12px;
}
body {
	margin-left: 10px;
}
</style>

<script type="text/javascript">
	$(function(){
		$('#ff').form({
			success:function(data){
				$.messager.alert('Server', data);
			}
		});
		
	});
</script>
</head>
<body >
<table border=0 >
	<tr>
		<td colspan="2">
			<table border=0 cellspacing="0" >
		    	<tr>
		    		<td><b><font color="blue"><label>Path：</label></font></b></td>
		    		<td id="tdv"><b><font color="red">${zkpath}</font></b></td>
		    	</tr>
		    </table>
		</td>
	</tr>
	<tr>
		<td align="left" valign="top"  >
		    <table border=0 cellspacing="0" width=300>
		    	<tr><td id="htd" colspan="2"><b><font color="blue"><label>Node ACLs</label></font></b></td></tr>
		    		<c:forEach items="${acls }" var="acl">
						<tr>
							<td id="htd"><label >Scheme：</label></td>
							<td id="tdhpx">${acl.scheme }</td>
						</tr>
						<tr>
							<td id="htd"><label >Id：</label></td>
							<td id="tdhpx">${acl.id }</td>
						</tr>
						<tr>
							<td id="htd" valign="top"><label >Permissions：</label></td>
							<td id="tdhpx">${acl.perms }</td>
						</tr>
					</c:forEach>
			</table>
			</td>
			<td align="left" valign="top" rowspan="2">
				<form id="ff" action="zk/saveData" method="post">
			    <input type="hidden" value="${cacheId}" name="cacheId" />
			    <input type="hidden" value="${path}" name="path" />
				<table border=0 cellspacing="0">
			    	<tr>
			    	<td><b><font color="blue"><label>Node Data</label></font></b></td>
			    	<td align="right"><a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="javascript:$('#ff').submit();"><span data-locale-html="save">保存</span></a></td>
			    	</tr>
			    	<tr>
						<td align="left" valign="top" colspan="2">
						<textarea rows="20" name="data" cols="70" style="border:1px solid CornflowerBlue ;width:98%;margin: 3px;">${data}</textarea>
						</td>
					</tr>
			    </table>
				</form>
			</td>
		</tr>
		<tr>
			<td align="left" valign="top"  >
			<table border=0 cellspacing="0"  width=300>
		    	<tr><td colspan="2"><b><font color="blue"><label>Node Metadata</label></font></b></td></tr>
		    		<tr><td id="htd"><label >Data Length：</label></td><td id="tdh">${dataLength }</td></tr>
					<tr><td id="htd"><label >Creation Time：</label></td><td id="tdh">${ctime }</td></tr>
					<tr><td id="htd"><label >Last Modified Time：</label></td><td id="tdh">${mtime }</td></tr>
					<tr><td id="htd"><label >Data Version：</label></td><td id="tdh">${version }</td></tr>
					<tr><td id="htd"><label >Children Version：</label></td><td id="tdh">${cversion }</td></tr>
					<tr><td id="htd"><label >ACL Version：</label></td><td id="tdh">${aversion }</td></tr>
					<tr><td id="htd"><label >Ephemeral Owner：</label></td><td id="tdh">${ephemeralOwner }</td></tr>
					<tr><td id="htd"><label >Number Of Children：</label></td><td id="tdh">${numChildren }</td></tr>
					<tr><td id="htd"><label >Node Zxid：</label></td><td id="tdh">${pzxid }</td></tr>
					<tr><td id="htd"><label >Creation Zxid：</label></td><td id="tdh">${czxid }</td></tr>
					<tr><td id="htd"><label >Modified Zxid：</label></td><td id="tdh">${mzxid }</td></tr>
					
			</table>
		</td>
		
	</tr>
</table>
    
	
		
	
	<%-- 	${data } ${czxid } ${mzxid } ${ctime } ${mtime } ${version } ${cversion
	} ${aversion } ${ephemeralOwner } ${dataLength } ${numChildren }
	${pzxid } --%>

</body>
</html>