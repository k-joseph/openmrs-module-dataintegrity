<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Run Integrity Checks" otherwise="/login.htm" redirect="/admin/index.htm" />

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<h2><spring:message code="dataintegrity.runMultipleChecks.link"/></h2>
<br />

<script type="text/javascript">
	function checkAll(field)
	{
		for (i = 0; i < field.length; i++) {
			field[i].checked = true ;
		}
	}
	
	function uncheckAll(field)
	{
		for (i = 0; i < field.length; i++) {
			field[i].checked = false ;
		}
	}

</script>


<b class="boxHeader"><spring:message code="dataintegrity.runMultipleChecks.title"/></b>
<form class="box" method="post" name="multipleCheckForm">
	<c:if test="${not empty runMultipleChecksList}">
	<table>
		<tr>
			<th></th>
			<th align="left"> <spring:message code="dataintegrity.checksList.columns.name"/> </th>
		</tr>
		<c:forEach items="${runMultipleChecksList}" var="integrityChecksObj">
			<tr>
				<td valign="top"><input type="checkbox" name="integrityCheckId" value="${integrityChecksObj.integrityCheckId}"></td>
				<td align="left">${integrityChecksObj.integrityCheckName}</td>
			</tr>
			</c:forEach>
		<tr>
			<td colspan="2">
				<table><tr>
					<td>
						<a href="#" onclick="checkAll(document.multipleCheckForm.integrityCheckId)"><spring:message code="dataintegrity.runMultipleChecks.selectAll"/></a>
					</td>
					<td>
						<a href="#" onclick="uncheckAll(document.multipleCheckForm.integrityCheckId)"><spring:message code="dataintegrity.runMultipleChecks.selectNone"/></a>
					</td>
				</tr></table>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<br />
				<input type="submit" value="<spring:message code="dataintegrity.runMultipleChecks.run"/>"/> 
			</td>
		</tr>
	</table>
	</c:if>
	
	<c:if test="${empty runMultipleChecksList}"><spring:message code="dataintegrity.checksList.empty"/></c:if>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>