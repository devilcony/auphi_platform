<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include/taglib.jsp"%>
<skyform:html title="部门管理">
<skyform:import src="/admin/js/manageDepartment.js"/>
<skyform:ext.codeRender fields="LEAF"/>
<skyform:body>
<skyform:div key="deptTreeDiv"></skyform:div>

</skyform:body>
<skyform:script>
   var root_deptid = '<skyform:out key="rootDeptid" scope="request"/>';
   var root_deptname = '<skyform:out key="rootDeptname" scope="request"/>';
</skyform:script>
</skyform:html>