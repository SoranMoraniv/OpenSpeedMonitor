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

package de.iteratec.osm.report.chart;

/**
 * Application time charts can be built with these libraries.
 * Rickshaw is published under a free license so it's the default charting library.
 *
 * 2015-10-08, nkuhn: HIGHCHARTS is removed.
 *      So at the moment rickshaw is only charting library in addition to native d3.js.
 * 
 * @author wilhelm
 * @see http://code.shutterstock.com/rickshaw/
 *
 */
public enum ChartingLibrary {
	RICKSHAW
}