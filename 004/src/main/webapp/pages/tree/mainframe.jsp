<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%  
	String path = request.getContextPath();  
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"; 
	System.out.println(basePath);
	String companyId=(String)application.getAttribute("companyId");
	String companyName=(String)application.getAttribute("companyName");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<style type="text/css">
a:link,a:visited{
 text-decoration:none;  /*超链接无下划线*/
}
a:hover{
 text-decoration:underline;  /*鼠标放上去有下划线*/
}
p {
 text-align: center;
}
h2.htest {
  text-align: center;
}
</style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="/004/js/jquery-easyui-1.4/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="/004/js/jquery-easyui-1.4/themes/icon.css">
   <link rel="stylesheet" type="text/css" href="/004/js/demo.css">
	<script type="text/javascript" src="/004/js/jquery-easyui-1.4/jquery.min.js"></script>
	<script type="text/javascript" src="/004/js/jquery-easyui-1.4/jquery.easyui.min.js"></script>
<title><%=companyName %>公司记账科目管理</title>
</head>
<div style="margin:20px 0;"></div>

            <!--<div class="easyui-panel" style="padding:5px;">  -->
    <div class="easyui-panel" data-options="style:{margin:'0 auto'}" style="width:500px"> 
        <a href="<%=basePath %>" class="easyui-linkbutton" data-options="plain:true">首页</a>
        <a href="<%=basePath %>pages/monsettleprog/monSettleProgList.jsp" class="easyui-menubutton" data-options="menu:'#mm1',iconCls:'icon-edit'">记账动作</a>
        <a href="#" class="easyui-menubutton" data-options="menu:'#mm2',iconCls:'icon-help'">科目管理</a>
        <a href="<%=basePath %>/loginCompany.html" class="easyui-linkbutton" data-options="plain:true">重选公司名</a>
    </div>
    <h2 class="htest"><%=companyName %>科目管理</h2>
    <div id="mm1" style="width:150px;">
        
        <div data-options="iconCls:'icon-redo'"><a href="<%=basePath %>pages/monsettleprog/monSettleProgList.jsp" class="easyui-menubutton" data-options="menu:'#mm1',iconCls:'icon-edit'">记账状态</a></div>
        <div data-options="iconCls:'icon-redo'"><a href="<%=basePath %>pages/mondetail/mondetailList.jsp" class="easyui-menubutton" data-options="menu:'#mm1',iconCls:'icon-edit'">科目明细</a></div>
    </div>
    <div id="mm2" style="width:100px;"> <a href="<%=basePath %>pages/tree/mainframe.jsp"  >科目管理</a>
    <div data-options="iconCls:'icon-undo'" > <a href="<%=basePath %>pages/company/uploadfile.jsp"  >初始化科目数据</a></div>
    </div>	

 
<body onload='init()'>
<div class="generalframe"style="margin:20px 0;width:800px; margin:0 auto;">
<iframe id="treeshow" name="treeshow" src='tree.jsp' width = "200" height="500" ></iframe>
<iframe id="treeedit" name="treeedit" src="" width = "480" height="500"></iframe>
</div>
</body>
<script type="text/javascript">
function init()
{
  var grid = document.getElementById("treeshow");
  window.resizeTo(grid.offsetWidth, grid.offsetHeight);
  var grid2 = document.getElementById("treeedit");
  window.resizeTo(grid2.offsetWidth, grid2.offsetHeight);
}
</script>
</html>