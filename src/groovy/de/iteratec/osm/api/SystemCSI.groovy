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

package de.iteratec.osm.api;

/**
 * <p>
 * Weighted CSI-mean of a system for a specific time period.
 * Is returned as JSON to rest-calls to {@link RestApiController#getSystemCsi}.
 * </p>
 * 
 * @author nkuhn
 * @author mze
 */
public class SystemCSI {

	/**
	 * CSI-mean of a system, weighted by shop-page and browser. 
	 */
	double csiValueAsPercentage;

	/**
	 * Target-CSI-value for the specified time period.
	 */
	double targetCsiAsPercentage;

	/**
	 * Difference between {@link #csiValue} and {@link #targetCSI}.
	 */
	double delta;

	/**
	 * Count of the {@link EventResult}s, underlying the calculation.
	 */
	int countOfMeasurings;
}
