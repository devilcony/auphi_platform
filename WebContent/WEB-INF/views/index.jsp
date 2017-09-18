<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include/taglib.jsp"%>
<skyform:html title="${sysTitle}" showLoading="false" exportParams="true" isSubPage="false"
	exportUserinfo="true">
<skyform:import src="/resource/commonjs/extTabCloseMenu.js" />
<skyform:import src="/admin/js/index.js" />
<skyform:ext.codeStore fields="SEX"/>
<skyform:body>
	<skyform:div key="themeTreeDiv" cls="x-hidden"></skyform:div>
	<skyform:div key="previewDiv" cls="x-hidden">
		<img src="../resource/image/theme/default.jpg" />
	</skyform:div>
	<skyform:skyform.Viewport northTitle="${sysTitle}" westTitle="${westTitle}" />
</skyform:body>
</skyform:html>