<%@ page import="de.iteratec.osm.report.UserspecificDashboard" %>

<%
    def userspecificDashboardService = grailsApplication.classLoader.loadClass('de.iteratec.osm.report.UserspecificDashboard').newInstance()
%>
<%-- determine main-tab an set variable respectively --%>
<g:if test="${controllerName.equals('eventResultDashboard')||controllerName.equals('tabularResultPresentation')}"><g:set var="mainTab" value="results" /></g:if>
<g:elseif test="${controllerName.equals('csiDashboard')}"><g:set var="mainTab" value="csi" /></g:elseif>
<g:elseif test="${controllerName.equals('script')}"><g:set var="mainTab" value="management" /></g:elseif>
<g:elseif test="${controllerName.equals('job')}"><g:set var="mainTab" value="management" /></g:elseif>
<g:elseif test="${controllerName.equals('queueStatus')}"><g:set var="mainTab" value="management" /></g:elseif>
<g:elseif test="${controllerName.equals('jobSchedule')}"><g:set var="mainTab" value="management" /></g:elseif>
<g:elseif test="${controllerName.equals('connectivityProfile')}"><g:set var="mainTab" value="management" /></g:elseif>
<g:else><g:set var="mainTab" value="unnknown" /></g:else>

<div class="">

	<%-- ---------------------------------------------------------------------------------------------- tabs --%>

	<ul class="nav nav-tabs" data-role="listview" data-split-icon="gear" data-filter="true">
		<li class="controller ${mainTab.equals('management')?'active':''}">
			<g:link controller="job" action="list"> <g:message code="de.iteratec.isr.managementDashboard" default="Verwaltung" /></g:link>
		</li>
		<li class="controller ${mainTab.equals('results')?'active':''}">
			<g:link controller="eventResultDashboard" action="showAll"> <g:message code="de.iteratec.isr.measurementresults" default="Mess-Ergebnisse" /></g:link>
		</li>
		<li class="controller ${mainTab.equals('csi')?'active':''}">
			<g:link controller="csiDashboard" action="showAll"> <g:message code="de.iteratec.isocsi.csi" default="CSI" /></g:link>
		</li>
	</ul>

	<%-- ---------------------------------------------------------------------------------------------- links --%>

	<ul class="nav nav-pills">

        <%-- Measurements --%>

		<g:if test="${mainTab.equals('management')}">
			<li class="controller ${controllerName.equals('job')?'active':''}">
				<g:link controller="job" action="list"><i class="fa fa-calendar"></i> <g:message code="de.iteratec.isj.jobs" default="Jobs" /></g:link>
			</li>
			<li class="controller ${controllerName.equals('script')?'active':''}">
				<g:link controller="script" action="list"><i class="fa fa-align-left"></i> <g:message code="de.iteratec.iss.scripts" default="Skripte" /></g:link>
			</li>
			<li class="controller ${controllerName.equals('queueStatus')?'active':''}">
				<g:link controller="queueStatus" action="list"><i class="fa fa-inbox"></i> <g:message code="queue.status.label" /></g:link>
			</li>
            <li class="controller ${controllerName.equals('jobSchedule')?'active':''}">
                <g:link controller="jobSchedule" action="schedules"><i class="fa fa-clock-o"></i> <g:message code="job.Schedule.label" /></g:link>
            </li>
			<li class="controller ${controllerName.equals('connectivityProfile')?'active':''}">
				<g:link controller="connectivityProfile" action="list"><i class="fa fa-globe"></i> <g:message code="connectivityProfile.label.plural" /></g:link>
			</li>
		</g:if>

        <%-- Results --%>

		<g:elseif test="${mainTab.equals('results')}">
			<li class="controller ${controllerName.equals('eventResultDashboard')?'active':''}">
				<g:link controller="eventResultDashboard" action="showAll"><i class="fa fa-signal"></i>
                    <g:message code="de.iteratec.isocsi.eventResultDashboard" default="Dashboard" /></g:link>
			</li>
			<li class="controller ${controllerName.equals('tabularResultPresentation')?'active':''}">
	            <g:link controller="tabularResultPresentation" action="listResults"><i class="fa fa-th-list"></i> <g:message code="de.iteratec.result.title" default="Einzelergebnisse" /></g:link>
	        </li>
                    %{--Toggled until IT-701 is implemented--}%
            %{--<div class="btn-toolbar" style="margin: 0;">--}%
                %{--<div class="btn-group">--}%
                    %{--<a class="btn btn-primary btn-small dropdown-toggle" data-toggle="dropdown" href="#">--}%
                        %{--<g:message code="de.iteratec.isocsi.dashBoardControllers.custom.select.label" default="Dashboard-Ansicht ausw&auml;hlen" />--}%
                        %{--<span class="caret"></span>--}%
                    %{--</a>--}%
                    %{--<ul class="dropdown-menu" id="customDashBoardSelection">--}%
                        %{--<g:set var="availableDashboards" value="${userspecificDashboardService.getListOfAvailableDashboards("EVENT")}" />--}%
                        %{--<g:if test="${availableDashboards.size() > 0}">--}%
                            %{--<g:each in="${availableDashboards}" var="availableDashboard">--}%
                                %{--<li><a href="${availableDashboard.link}">${availableDashboard.dashboardName}</a></li>--}%
                            %{--</g:each>--}%
                        %{--</g:if>--}%
                        %{--<g:else>--}%
                            %{--<g:set var="anchorDashboardCreation" value="#"/>--}%
                            %{--<sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_SUPER_ADMIN">--}%
                                %{--<g:set var="anchorDashboardCreation" value="#bottomCommitButtons"/>--}%
                            %{--</sec:ifAnyGranted>--}%
                            %{--<li><a href="${anchorDashboardCreation}"><g:message code="de.iteratec.isocsi.dashBoardControllers.custom.select.error.noneAvailable" default="Es sind keine verf&uuml;gbar - bitte legen Sie eine an!" /></a></li>--}%
                        %{--</g:else>--}%
                    %{--</ul>--}%
                %{--</div>--}%
            %{--</div>--}%
		</g:elseif>

        <%-- CSI --%>

		<g:elseif test="${mainTab.equals('csi')}">
			<li class="controller ${actionName.equals('showAll')?'active':''}">
				<g:link controller="csiDashboard" action="showAll"><i class="fa fa-signal"></i> <g:message code="de.iteratec.isocsi.csiDashboard" default="Dashboard" /></g:link>
			</li>
                %{--Toggled until IT-701 is implemented--}%
            %{--<div class="btn-group">--}%
            %{--<a class="btn btn-primary btn-small dropdown-toggle" data-toggle="dropdown" href="#">--}%
              %{--<g:message code="de.iteratec.isocsi.dashBoardControllers.custom.select.label" default="Dashboard-Ansicht ausw&auml;hlen" />--}%
              %{--<span class="caret"></span>--}%
            %{--</a>--}%
            %{--<ul class="dropdown-menu">--}%
                %{--<g:set var="availableDashboards" value="${userspecificDashboardService.getListOfAvailableDashboards("CSI")}" />--}%

              %{--<g:if test="${availableDashboards.size() > 0}">--}%
                %{--<g:each in="${availableDashboards}" var="availableDashboard">--}%
                  %{--<li><a href="${availableDashboard.link}">${availableDashboard.dashboardName}</a></li>--}%
                %{--</g:each>--}%
              %{--</g:if>--}%
              %{--<g:else>--}%
                  %{--<g:set var="anchorDashboardCreation" value="#"/>--}%
                  %{--<sec:ifLoggedIn>--}%
                      %{--<sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_SUPER_ADMIN">--}%
                          %{--<g:set var="anchorDashboardCreation" value="#bottomCommitButtons"/>--}%
                      %{--</sec:ifAnyGranted>--}%
                  %{--</sec:ifLoggedIn>--}%
                  %{--<li><a href="${anchorDashboardCreation}"><g:message code="de.iteratec.isocsi.dashBoardControllers.custom.select.error.noneAvailable" default="Es sind keine verf&uuml;gbar - bitte legen Sie eine an!" /></a></li>--}%
              %{--</g:else>--}%
            %{--</ul>--}%
            %{--</div>--}%
			<li class="controller ${actionName.equals('showDefault')?'active':''}">
				<g:link controller="csiDashboard" action="showDefault"><i class="fa fa-picture-o"></i> <g:message code="de.iteratec.isocsi.csi.linktext.staticDashboard" default="Statische Ansicht"/></g:link>
			</li>
			<li class="controller ${actionName.equals('weights')?'active':''}">
				<g:link controller="csiDashboard" action="weights"><i class="fa fa-tasks"></i> <g:message code="de.iteratec.isocsi.weight" default="Gewichtung" /></g:link>
			</li>
		</g:elseif>
	</ul>

</div>