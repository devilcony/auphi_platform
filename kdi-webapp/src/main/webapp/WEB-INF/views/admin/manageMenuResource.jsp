<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include/taglib.jsp"%>
<skyform:html title="菜单资源管理">
<skyform:import src="/admin/js/manageMenuResource.js"/>
<skyform:ext.codeRender fields="MENUTYPE,LEAF,EXPAND"/>
<skyform:ext.codeStore fields="EXPAND"/>
<skyform:body>
<skyform:div key="menuTreeDiv"></skyform:div>
</skyform:body>
<skyform:script>
   var root_menuname = '<skyform:out key="rootMenuName" scope="request"/>';
</skyform:script>
</skyform:html>