<%@ page import="de.iteratec.osm.report.chart.ChartingLibrary"%>

%{--This template can be added to a view with a chart. It enables adjustments to the charts layout like adding a title, resizing the--}%
%{--whole chart or show/hide datapoints.--}%
%{--The rendering view can provide attribute chartRenderingLibrary to specify the charting library used to render the chart. If attribute is--}%
%{--missing the default charting library rickshaw is assumed.--}%

<div class="span12 accordion" id="accordion2">
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseAdjustment">
			<g:message code="de.iteratec.chart.adjustment.name" default="Diagramm anpassen"/>
			</a>
		</div>
		<div id="collapseAdjustment" class="accordion-body collapse in">
			<div class="accordion-inner">
				<div class="span11">
					<!-- Diagram-title -->
					<div class ="row">
						<div class="span2 text-right"><g:message code="de.iteratec.chart.title.name"/></div>
						<div class="span9"><input id="dia-title" class="input-xxlarge" type="text" value="${chartTitle}"></div>
					</div>
					<!-- diagram-size -->
					<div class ="row">
						<div class="span2 text-right"><g:message code="de.iteratec.chart.size.name"/></div>
						<div class="span9">
							<div class="input-prepend">
								<span class="add-on"><g:message code="de.iteratec.chart.width.name"/></span>
								<input class="span1 content-box" id="dia-width" type="text" value="${initialChartWidth}">
							</div>
							<div class="input-prepend">
								<span class="add-on"><g:message code="de.iteratec.chart.height.name"/></span>
								<input class="span1 content-box" id="dia-height" type="text" value="${initialChartHeight}">
							</div>
							<button class="btn" id="dia-change-chartsize" style="vertical-align: top;"><g:message code="de.iteratec.ism.ui.button.apply.name"/></button>
						</div>
					</div>
					<!-- Y-Axis -->
					<!-- rickshaw -->
                    <div id="adjust_chart_y_axis" class ="row">
                        <div class="span2 text-right"><g:message code="de.iteratec.chart.axis.y.name"/></div>
                        <div class="span9">
                            <div class="input-prepend">
                                <span class="add-on"><g:message code="de.iteratec.chart.axis.y.minimum.name"/></span>
                                <input class="span1 content-box" id="dia-y-axis-min" type="text" value="${yAxisMin?:'' }">
                            </div>
                            <div class="input-prepend">
                                <span class="add-on"><g:message code="de.iteratec.chart.axis.y.maximum.name"/></span>
                                <input class="span1 content-box" id="dia-y-axis-max" type="text" value="${yAxisMax?:'' }">
                            </div>
                            <button class="btn" id="dia-change-yaxis" style="vertical-align: top;"><g:message code="de.iteratec.ism.ui.button.apply.name"/></button>
                        </div>
                    </div>
                    <div class ="row">
                        <div class="span2 text-right"><g:message code="de.iteratec.isocsi.csi.show.datamarkers" default="Datenpunkte anzeigen"/></div>
                        <div class="span9"><g:checkBox id="to-enable-marker" name="toEnableMarker" checked="${markerShouldBeEnabled}" /></div>
                      </div>
                    <div class ="row">
                        <div class="span2 text-right"><g:message code="de.iteratec.isocsi.csi.show.datalabels" default="Datenlabels anzeigen"/></div>
                        <div class="span9"><g:checkBox id="to-enable-label" name="toEnableLabel" checked="${labelShouldBeEnabled}" /></div>
                      </div>
                    <div class ="row">
                        <div class="span2 text-right"><g:message code="de.iteratec.isocsi.csi.show.wideScreenDiagramMontage" default="Exportierte Diagramme für Breitbild-Darstellung optimieren"/></div>
                        <div class="span9"><g:checkBox id="wide-screen-diagram-montage" name="wideScreenDiagramMontage" checked="${params.wideScreenDiagramMontage?true:false}" /></div>
                    </div>
				</div>
			</div>
		</div>
	
    <div class="accordion-footer">
    </div>
	</div>
</div>