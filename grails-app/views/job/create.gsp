<%=packageName%>
<g:render template="./editOrCreate" model="['mode': 'create', 'entityDisplayName': message(code: 'de.iteratec.isj.job', default: 'Job'), 'entity': job]" />