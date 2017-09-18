<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include/taglib.jsp"%>
<skyform:html title="操作权限授权" showLoading="false" extDisabled="true">
<skyform:body>
<skyform:skyform.RoleGrantOperationTree key="managerOperationTab" authorizelevel="1"/>
</skyform:body>
</skyform:html>