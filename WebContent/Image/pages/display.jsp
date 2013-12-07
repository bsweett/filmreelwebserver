<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="k" uri="/struts-tags"%>
<html>
<head></head>
<body>
	<h1>Struts 2 Dynamic Image Example</h1>
 
	<img src=" <s:url action="ImageAction?">
					<s:param name="imageId" value="messageStore.message"/>
				</s:url> "></img>
 
</body>
</html>
<!--  Pass message store from display as paramter to ImageAction -->