<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.joda.org/joda/time/tags" prefix="joda" %>
<%@taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<% pageContext.setAttribute("now", new org.joda.time.DateTime()); %>
<html>
<head>
    <title>Hot Deploy Tool</title>
    <style type="text/css" media="all">
	@import url("styles/deploy.css");
	</style>
    <script language="JavaScript">
    function addFileField(){
    	var optionIndex = 1;
    	var parentObj = document.getElementById("fileListDiv");
    	var currentOptionCount = parentObj.getElementsByTagName("tr").length;
    	addField(currentOptionCount);
    }
    function addField(index) {
    	var obj2clone = document.getElementById("fileRow");
    	var newNode = obj2clone.cloneNode(true);
    	newNode.id="fileRow" + index;
    	var tableTD = newNode.getElementsByTagName("td");
    	var fieldLable = tableTD[0].firstChild;
    	fieldLable.id = "files[" + index + "]";
    	fieldLable.name = "files[" + index + "]";
    	fieldLable.value = null;
    	document.getElementById("filesListTable").appendChild(newNode);
    }
    </script>
</head>
<body>
 
<div id="breadcrumbs"><span id="link-sign-out"><a href="<spring:url value="/j_spring_security_logout" htmlEscape="true" />">Logout</a></span></div>
<h2>Site Project Patch Files</h2>
<c:if test="${param.deployError != null}" >
	<div class="error">${param.deployError}</div>
</c:if>
 
<form:form method="post" action="add" commandName="patchFile">
 
 <form:hidden path="id" />
 <input type="hidden" id="userName" name="userName" value="${user}" />
 <c:choose>
 	<c:when test="${ not empty patchFile.creationDate}">
 		<input type="hidden" id="createDate" name="createDate" value="${patchFile.creationDate}" />
 	</c:when>
 	<c:otherwise>
 		<input type="hidden" id="createDate" name="createDate" value="<joda:format value='${now}' style='SS' />" />
 	</c:otherwise>
 </c:choose>
 	
 <form:hidden path="environment" />
    <table>
    <tr>
        <td><form:label path="Note"><spring:message code="label.note"/></form:label></td>
        <td><form:textarea path="note" cols="40" rows="10" /></td>
        <td><spring:message code="note.note" /></td>
    </tr>
    <tr>
    	<td><form:label path="FileName"><spring:message code="label.filename"/></form:label></td>
    	<td><form:input path="fileName" size="40" /></td>
    	<td><spring:message code="note.filename" /></td>
    </tr>
    <tr>
    	<td colspan="2"><h4>Patch File list</h4></td>
    </tr>
    <tr>
    	<td><form:label path="Source"/><spring:message code="label.source" /></td>
    	<td><form:select path="source">
    			<form:option value="trunk"></form:option>
    			<form:option value="branch"></form:option>
    		</form:select></td>
    	<td><form:label path="BranchVersion"/><spring:message code="label.branch" /><form:input path="branchVersion"/></td>
    </tr>
</table>
<div id="fileListDiv">
<h5>enter the path to the file from webapp, for example &quot;<i>WEB-INF/pages/inactives/inactives.jsp</i>&quot;</h5>
<table id="filesListTable">
    <tr id="fileRow">
    	<td ><input id="files[0]" name="files[0]" size="60" /></td>
    </tr>
</table>
</div>
<table><tr><td>
     	<input type="button" onclick="javascript:addFileField();" value="Add File" />
        <input type="submit" value="<spring:message code="label.addpatchfile"/>"/>
        </td></tr></table>
</form:form>
 
<h3>Patch Files</h3>
	<display:table name="patchFileList" id="patchFile" class="content-datagrid">
		<display:column property="userName" title="User Name" />
		<display:column property="note" title="Note" />
		<display:column property="creationDate" title="Created" format="{0,date,M/d/y h:mm a}" />
		<display:column property="environment" title="Environment" />
		<display:column title="Actions">
			<a href="delete/${patchFile.id}" onclick="return confirm('Are you sure you want to delete this patchfile?  This will only remove the record from the database, the actual patch file will still exist in the patchfile hangar.')">delete</a> | 
			<c:choose>
				<c:when test="${patchFile.environment == 'stage'}">
					<a href="promote/${patchFile.id}/prod" onclick="return confirm('Are you sure you want to Deploy To the PRODUCTION env?')">Deploy to PROD</a> 
				</c:when>
				<c:when test="${patchFile.environment == 'prod' }">
				</c:when>
				<c:otherwise>
					<a href="promote/${patchFile.id}/stage" onclick="return confirm('Are you sure you want to deploy to Stage?')">Deploy to Stage</a>
				</c:otherwise>
			</c:choose>
		</display:column>
	</display:table>
 
</body>
</html>
