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

package de.iteratec.osm.d3Data

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class BarChartDataSpec extends Specification{

    def "bar chart data object initialised with labels and an empty list"() {
        when:
        BarChartData barChartData = new BarChartData()

        then:
        !barChartData.xLabel.isEmpty()
        !barChartData.yLabel.isEmpty()
        barChartData.bars.isEmpty()
    }

    def "addDatum adds a chart entry object to list of bars" () {
        given:
        BarChartData barChartData = new BarChartData()
        ChartEntry chartEntry = new ChartEntry()

        when:
        barChartData.addDatum(chartEntry)

        then:
        barChartData.bars.size() == 1
        barChartData.bars[0] == chartEntry
    }
}
