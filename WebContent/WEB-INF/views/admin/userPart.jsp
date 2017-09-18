<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include/taglib.jsp"%>
<skyform:html title="UI元素人员授权">
<skyform:import src="/arm/js/userPart.js"/>
<skyform:body>
<skyform:div key="deptTreeDiv" />
<skyform:ext.codeRender fields="CMPTYPE"/>
<skyform:ext.codeStore fields="CMPTYPE"/>
<skyform:ext.codeRender fields="PARTAUTHTYPE"/>
<skyform:ext.codeStore fields="PARTAUTHTYPE"/>
</skyform:body>
<skyform:script>
   var root_deptid = '<skyform:out key="rootDeptid" scope="request"/>';
   var root_deptname = '<skyform:out key="rootDeptname" scope="request"/>';
   var root_menuname = '<skyform:out key="rootMenuName" scope="request"/>';
</skyform:script>
</skyform:html>