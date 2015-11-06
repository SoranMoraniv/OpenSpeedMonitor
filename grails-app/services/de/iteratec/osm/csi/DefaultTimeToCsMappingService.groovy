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

import de.iteratec.osm.report.chart.RickshawHtmlCreater

/**
 * DefaultTimeToCsMappingService
 * A service class encapsulates the core business logic of a Grails application
 */
class DefaultTimeToCsMappingService {

    static transactional = false

    TimeToCsMappingCacheService timeToCsMappingCacheService

    List<DefaultTimeToCsMapping> getAll() {
        return DefaultTimeToCsMapping.list()
    }

    /**
     * Copies all Mapping values (load time to customer satisfaction) from given default mapping as actual mappings
     * for the given page.
     * <b>Note(!):</b> All existing latest mappings of the given page get deleted!
     * @param page
     * @param nameOfDefaultMapping
     *
     * //TODO: write a test for this method
     */
    void copyDefaultMappingToPage(Page page, String nameOfDefaultMapping){

        List<DefaultTimeToCsMapping> defaultMappingsToCopyToPage = DefaultTimeToCsMapping.findAllByName(nameOfDefaultMapping)
        if (defaultMappingsToCopyToPage.size() == 0)
            throw new IllegalArgumentException("No default csi mapping with name ${nameOfDefaultMapping} exists!")

        Integer actualMappingVersion
        TimeToCsMapping.withTransaction {
            List<TimeToCsMapping> actualMappings = timeToCsMappingCacheService.getActualMappingsFromDb()
            actualMappingVersion = actualMappings.size()>0 ? actualMappings[0].mappingVersion : 1
            actualMappings.findAll {it.page.ident() == page.ident()}*.delete()
        }
        TimeToCsMapping.withTransaction {
            defaultMappingsToCopyToPage.each {defaultMapping ->
                new TimeToCsMapping(
                        page: page,
                        loadTimeInMilliSecs: defaultMapping.loadTimeInMilliSecs,
                        customerSatisfaction: defaultMapping.customerSatisfactionInPercent,
                        mappingVersion: actualMappingVersion
                ).save(failOnError: true)
            }
        }
        TimeToCsMapping.withTransaction {
            timeToCsMappingCacheService.fetchMappings()
        }
    }

    def translateDefaultMappingsToJson(List<DefaultTimeToCsMapping> mappings){
        return new RickshawHtmlCreater().transformCSIMappingData(mappings)
    }
}
