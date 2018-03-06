<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include/taglib.jsp"%>
<skyform:html title="人员管理与授权">
<skyform:import src="/admin/js/manageUser.js"/>
<skyform:ext.codeRender fields="SEX,LOCKED,USERTYPE"/>
<skyform:ext.codeStore fields="SEX,LOCKED,USERTYPE:3"/>
<skyform:body>
<skyform:div key="deptTreeDiv"></skyform:div>
</skyform:body>
<skyform:script>
   var root_deptid = '<skyform:out key="rootDeptid" scope="request"/>';
   var root_deptname = '<skyform:out key="rootDeptname" scope="request"/>';
   var login_account = '<skyform:out key="login_account" scope="request"/>';
</skyform:script>
</skyform:html>