/* 
* OpenSpeedMonitor (OSM)
* Copyright 2014 iteratec GmbH
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
* 	http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software 
* distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License.
*/

package de.iteratec.osm.report.chart

import de.iteratec.osm.util.I18nService
import grails.test.mixin.TestFor
import spock.lang.Specification

import static de.iteratec.osm.util.Constants.HIGHCHART_LEGEND_DELIMITTER

/**
 * These tests test processing osm chart data.
 *
 * <b>Note: </b>Method {@link de.iteratec.osm.report.chart.OsmChartProcessingService#summarizeEventResultGraphs(java.util.List)}
 *      isn't tested here cause it is tested in class {@link de.iteratec.osm.result.SummarizedChartLegendEntriesSpec} already.
 */
@TestFor(OsmChartProcessingService)
class OsmChartProcessingServiceSpec extends Specification{

    OsmChartProcessingService serviceUnderTest

    public static final String JOB_GROUP_1_NAME = 'group 1'
    public static final String JOB_GROUP_2_NAME = 'group 2'
    public static final String JOB_GROUP_3_NAME = 'group 3'
    public static final String JOB_GROUP_4_NAME = 'group 4'
    public static final String EVENT_1_NAME = 'event 1 name'
    public static final String EVENT_2_NAME = 'event 2 name'
    public static final String EVENT_3_NAME = 'event 3 name'
    public static final String EVENT_4_NAME = 'event 4 name'
    public static final String LOCATION_1_UNIQUE_IDENTIFIER = 'unique-identifier-location1'
    public static final String LOCATION_2_UNIQUE_IDENTIFIER = 'unique-identifier-location2'
    public static final String LOCATION_3_UNIQUE_IDENTIFIER = 'unique-identifier-location3'
    public static final String LOCATION_4_UNIQUE_IDENTIFIER = 'unique-identifier-location4'
    public static final String PAGE_1_NAME = 'page 1 name'
    public static final String PAGE_2_NAME = 'page 2 name'
    public static final String PAGE_3_NAME = 'page 3 name'
    public static final String PAGE_4_NAME = 'page 4 name'

    public static final String I18N_LABEL_JOB_GROUP = 'Job Group'
    public static final String I18N_LABEL_MEASURED_EVENT = 'Measured step'
    public static final String I18N_LABEL_LOCATION = 'Location'
    public static final String I18N_LABEL_MEASURAND = 'Measurand'
    public static final String I18N_LABEL_CONNECTIVITY = 'Connectivity'

    public void setup(){
        serviceUnderTest = service
        mocksCommonForAllTests()
    }

    // event measured values ////////////////////////////////////////////////////////////////////////////

    void "event measured values, every legend part in every graph different -> no summarization"(){
        setup:
        List<OsmChartGraph> graphs = [
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, EVENT_1_NAME, LOCATION_1_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_2_NAME, EVENT_2_NAME, LOCATION_2_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_3_NAME, EVENT_3_NAME, LOCATION_3_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_4_NAME, EVENT_4_NAME, LOCATION_4_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER))
        ]

        when:
        OsmRickshawChart chart = serviceUnderTest.summarizeCsiGraphs(graphs)
        List<OsmChartGraph> resultGraphs = chart.osmChartGraphs

        then:
        resultGraphs.size() == 4
        List<String> graphLables = resultGraphs*.label
        graphLables.containsAll([
                [JOB_GROUP_1_NAME, EVENT_1_NAME, LOCATION_1_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER),
                [JOB_GROUP_2_NAME, EVENT_2_NAME, LOCATION_2_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER),
                [JOB_GROUP_3_NAME, EVENT_3_NAME, LOCATION_3_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER),
                [JOB_GROUP_4_NAME, EVENT_4_NAME, LOCATION_4_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER),
        ])
        chart.osmChartGraphsCommonLabel == ''
    }
    void "event measured values, some legend parts in every graph the same, some different -> summarization"(){
        setup:
        List<OsmChartGraph> graphs = [
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, EVENT_1_NAME, LOCATION_1_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, EVENT_1_NAME, LOCATION_2_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, EVENT_1_NAME, LOCATION_3_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, EVENT_1_NAME, LOCATION_4_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER))
        ]
        String expectedCommonLabel =
                "<b>${I18N_LABEL_JOB_GROUP}</b>: ${JOB_GROUP_1_NAME} | " +
                        "<b>${I18N_LABEL_MEASURED_EVENT}</b>: ${EVENT_1_NAME}"

        when:
        OsmRickshawChart chart = serviceUnderTest.summarizeCsiGraphs(graphs)
        List<OsmChartGraph> resultGraphs = chart.osmChartGraphs

        then:
        resultGraphs.size() == 4
        List<String> graphLables = resultGraphs*.label
        graphLables.containsAll([
                LOCATION_1_UNIQUE_IDENTIFIER,
                LOCATION_2_UNIQUE_IDENTIFIER,
                LOCATION_3_UNIQUE_IDENTIFIER,
                LOCATION_4_UNIQUE_IDENTIFIER
        ])
        chart.osmChartGraphsCommonLabel == expectedCommonLabel
    }
    void "event measured values, single legend parts in some but not all graphs the same -> no summarization"(){
        setup:
        List<OsmChartGraph> graphs = [
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, EVENT_1_NAME, LOCATION_1_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, EVENT_2_NAME, LOCATION_2_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, EVENT_3_NAME, LOCATION_2_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_4_NAME, EVENT_4_NAME, LOCATION_4_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER))
        ]

        when:
        OsmRickshawChart chart = serviceUnderTest.summarizeCsiGraphs(graphs)
        List<OsmChartGraph> resultGraphs = chart.osmChartGraphs

        then:
        resultGraphs.size() == 4
        List<String> graphLables = resultGraphs*.label
        graphLables.containsAll([
                [JOB_GROUP_1_NAME, EVENT_1_NAME, LOCATION_1_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER),
                [JOB_GROUP_1_NAME, EVENT_2_NAME, LOCATION_2_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER),
                [JOB_GROUP_1_NAME, EVENT_3_NAME, LOCATION_2_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER),
                [JOB_GROUP_4_NAME, EVENT_4_NAME, LOCATION_4_UNIQUE_IDENTIFIER].join(HIGHCHART_LEGEND_DELIMITTER),
        ])
        chart.osmChartGraphsCommonLabel == ''
    }

    // page measured values ////////////////////////////////////////////////////////////////////////////

    void "page measured values, every legend part in every graph different -> no summarization"(){
        setup:
        List<OsmChartGraph> graphs = [
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, PAGE_1_NAME].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_2_NAME, PAGE_2_NAME].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_3_NAME, PAGE_3_NAME].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_4_NAME, PAGE_4_NAME].join(HIGHCHART_LEGEND_DELIMITTER)),
        ]

        when:
        OsmRickshawChart chart = serviceUnderTest.summarizeCsiGraphs(graphs)
        List<OsmChartGraph> resultGraphs = chart.osmChartGraphs

        then:
        resultGraphs.size() == 4
        List<String> graphLables = resultGraphs*.label
        graphLables.containsAll([
                [JOB_GROUP_1_NAME, PAGE_1_NAME].join(HIGHCHART_LEGEND_DELIMITTER),
                [JOB_GROUP_2_NAME, PAGE_2_NAME].join(HIGHCHART_LEGEND_DELIMITTER),
                [JOB_GROUP_3_NAME, PAGE_3_NAME].join(HIGHCHART_LEGEND_DELIMITTER),
                [JOB_GROUP_4_NAME, PAGE_4_NAME].join(HIGHCHART_LEGEND_DELIMITTER),
        ])
        chart.osmChartGraphsCommonLabel == ''
    }
    void "page measured values, some legend parts in every graph the same, some different -> summarization"(){
        setup:
        List<OsmChartGraph> graphs = [
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, PAGE_1_NAME].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, PAGE_2_NAME].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, PAGE_3_NAME].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, PAGE_4_NAME].join(HIGHCHART_LEGEND_DELIMITTER))
        ]
        String expectedCommonLabel = "<b>${I18N_LABEL_JOB_GROUP}</b>: ${JOB_GROUP_1_NAME}"

        when:
        OsmRickshawChart chart = serviceUnderTest.summarizeCsiGraphs(graphs)
        List<OsmChartGraph> resultGraphs = chart.osmChartGraphs

        then:
        resultGraphs.size() == 4
        List<String> graphLables = resultGraphs*.label
        graphLables.containsAll([
                PAGE_1_NAME,
                PAGE_2_NAME,
                PAGE_3_NAME,
                PAGE_4_NAME
        ])
        chart.osmChartGraphsCommonLabel == expectedCommonLabel
    }
    void "page measured values, single legend parts in some but not all graphs the same -> no summarization"(){
        setup:
        List<OsmChartGraph> graphs = [
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, PAGE_1_NAME].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, PAGE_2_NAME].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_1_NAME, PAGE_3_NAME].join(HIGHCHART_LEGEND_DELIMITTER)),
                new OsmChartGraph(label: [JOB_GROUP_4_NAME, PAGE_4_NAME].join(HIGHCHART_LEGEND_DELIMITTER))
        ]

        when:
        OsmRickshawChart chart = serviceUnderTest.summarizeCsiGraphs(graphs)
        List<OsmChartGraph> resultGraphs = chart.osmChartGraphs

        then:
        resultGraphs.size() == 4
        List<String> graphLables = resultGraphs*.label
        graphLables.containsAll([
                [JOB_GROUP_1_NAME, PAGE_1_NAME].join(HIGHCHART_LEGEND_DELIMITTER),
                [JOB_GROUP_1_NAME, PAGE_2_NAME].join(HIGHCHART_LEGEND_DELIMITTER),
                [JOB_GROUP_1_NAME, PAGE_3_NAME].join(HIGHCHART_LEGEND_DELIMITTER),
                [JOB_GROUP_4_NAME, PAGE_4_NAME].join(HIGHCHART_LEGEND_DELIMITTER),
        ])
        chart.osmChartGraphsCommonLabel == ''
    }

    void mocksCommonForAllTests(){
        serviceUnderTest.i18nService = [
                msg: {String msgKey, String defaultMessage = null, List objs = null ->
                    Map i18nKeysToValues = [
                        'job.jobGroup.label':I18N_LABEL_JOB_GROUP,
                        'de.iteratec.osm.result.measured-event.label':I18N_LABEL_MEASURED_EVENT,
                        'job.location.label':I18N_LABEL_LOCATION,
                        'de.iteratec.result.measurand.label': I18N_LABEL_MEASURAND,
                        'de.iteratec.osm.result.connectivity.label': I18N_LABEL_CONNECTIVITY
                    ]
                    return i18nKeysToValues[msgKey]
                }
        ] as I18nService
    }
}
