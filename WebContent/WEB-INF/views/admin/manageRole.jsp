<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include/taglib.jsp"%>
<skyform:html title="角色管理与授权">
<skyform:import src="/admin/js/manageRole.js"/>
<skyform:ext.codeRender fields="ROLETYPE,LOCKED"  />
<skyform:ext.codeStore fields="LOCKED,ROLETYPE:3"/>
<skyform:body>
<skyform:div key="deptTreeDiv"></skyform:div>
</skyform:body>
<skyform:script>
   var root_deptid = '<skyform:out key="rootDeptid" scope="request"/>';
   var root_deptname = '<skyform:out key="rootDeptname" scope="request"/>';
   var login_account = '<skyform:out key="login_account" scope="request"/>';
</skyform:script>
</skyform:html>