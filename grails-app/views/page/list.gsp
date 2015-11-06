
<%@ page import="de.iteratec.osm.csi.Page" %>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'page.label', default: 'Page')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>

<body>
	
<section id="list-page" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>
			
				<g:sortableColumn property="name" title="${message(code: 'page.name.label', default: 'Name')}" />
			
				<g:sortableColumn property="weight" title="${message(code: 'page.weight.label', default: 'Weight')}" />
			
			</tr>
		</thead>
		<tbody>
		<g:each in="${pageInstanceList}" status="i" var="pageInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td><g:link action="show" id="${pageInstance.id}">${fieldValue(bean: pageInstance, field: "name")}</g:link></td>
			
				<td>${fieldValue(bean: pageInstance, field: "weight")}</td>
			
			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${pageInstanceTotal}" />
	</div>
</section>

</body>

</html>
