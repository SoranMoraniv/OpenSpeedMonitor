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

package de.iteratec.osm.csi

class TimeToCsMapping implements RickshawTransformableCsMapping {
	Page page
	Integer loadTimeInMilliSecs
	Double customerSatisfaction
	/** each investigation of customer satisfaction with different load-times will provide a new set of TimeToCiMapping's */
	Integer mappingVersion

    static constraints = {
		page()
		loadTimeInMilliSecs()
		customerSatisfaction()
		mappingVersion()
    }

    public String retrieveGroupingCriteria(){
        return page.name
    }
    public Integer retrieveLoadTimeInMilliSecs(){
        return loadTimeInMilliSecs
    }
    public Double retrieveCustomerSatisfactionInPercent(){
        return customerSatisfaction
    }
}
