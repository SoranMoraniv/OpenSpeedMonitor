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

import static de.iteratec.osm.util.Constants.*

import org.joda.time.DateTime

import de.iteratec.osm.report.chart.MeasuredValueDaoService
import de.iteratec.osm.report.chart.MeasuredValueUtilService
import de.iteratec.osm.measurement.schedule.JobGroup
import de.iteratec.osm.measurement.schedule.JobGroupType
import de.iteratec.osm.measurement.schedule.JobService
import de.iteratec.osm.report.chart.AggregatorType
import de.iteratec.osm.report.chart.MeasuredValue
import de.iteratec.osm.report.chart.MeasuredValueInterval
import de.iteratec.osm.report.chart.MeasuredValueUpdateEvent
import de.iteratec.osm.report.chart.MeasuredValueUpdateEventDaoService
import de.iteratec.osm.csi.weighting.WeightFactor
import de.iteratec.osm.csi.weighting.WeightedCsiValue
import de.iteratec.osm.csi.weighting.WeightingService
import de.iteratec.osm.result.EventResult
import de.iteratec.osm.result.MeasuredValueTagService
import de.iteratec.osm.result.MvQueryParams
import de.iteratec.osm.result.dao.MeasuredEventDaoService
import de.iteratec.osm.util.PerformanceLoggingService
import de.iteratec.osm.util.PerformanceLoggingService.IndentationDepth
import de.iteratec.osm.util.PerformanceLoggingService.LogLevel
import de.iteratec.osm.measurement.environment.Browser
import de.iteratec.osm.measurement.environment.Location

class PageMeasuredValueService {

	EventMeasuredValueService eventMeasuredValueService
	JobService jobService
	CustomerSatisfactionWeightService customerSatisfactionWeightService
	CsiHelperService csiHelperService
	MeanCalcService meanCalcService
	MeasuredValueTagService measuredValueTagService
	PerformanceLoggingService performanceLoggingService
	MeasuredEventDaoService measuredEventDaoService
	MeasuredValueDaoService measuredValueDaoService
	MeasuredValueUtilService measuredValueUtilService
	WeightingService weightingService
	MeasuredValueUpdateEventDaoService measuredValueUpdateEventDaoService

	/**
	 * Just gets {@link MeasuredValue}s from DB. No creation or calculation.
	 * @param fromDate
	 * @param toDate
	 * @param targetInterval
	 * @return
	 */
	List<MeasuredValue> findAll(Date fromDate, Date toDate, MeasuredValueInterval targetInterval) {
		List<MeasuredValue> result = []

		def query = MeasuredValue.where {
			started >= fromDate
			started <= toDate
			interval == targetInterval
			aggregator == AggregatorType.findByName(AggregatorType.PAGE)
		}
		result = query.list()
		return result
	}

	/**
	 * <p>
	 * Finds page {@link MeasuredValue}s from DB. 
	 * No creation or calculation is performed.
	 * </p>
	 * 
	 * <p>
	 * <em>Note:</em> Passing an empty list to {@code groups} or {@code pages}
	 * at least one of the arguments will cause that no result is found.
	 * </p>
	 * 
	 * @param fromDate 
	 *         First {@link Date} (inclusive) of a test start date for that 
	 *         result measured values to find, 
	 *         not <code>null</code>.
	 * @param toDate 
	 *         Last {@link Date} (inclusive) of a test start date for that 
	 *         result measured values to find, 
	 *         not <code>null</code>.
	 * @param targetInterval 
	 *         The interval of the page measured values to find, currently only
	 *         {@link MeasuredValueInterval#WEEKLY} is supported, 
	 *         not <code>null</code> (this argument is deprecated).
	 * @param groups 
	 *         The groups for that measured values should be found, 
	 *         not <code>null</code>.
	 * @param pages 
	 *         The pages for that measured values should be found, 
	 *         not <code>null</code>.
	 * 
	 * @return Found (weekly) page measured values, not <code>null</code>.
	 * 
	 * @see MeasuredValue#getStarted()
	 */
	public List<MeasuredValue> findAll(Date fromDate, Date toDate, 
		@Deprecated
		MeasuredValueInterval targetInterval, 
		List<JobGroup> groups, List<Page> pages) {
		List<MeasuredValue> result = []
		if (groups.size() == 0 || pages.size() == 0) {
			return result
		}
		String tagPattern = measuredValueTagService.getTagPatternForWeeklyPageMvsWithJobGroupsAndPages(groups, pages)
		result = measuredValueDaoService.getMvs(
			fromDate,
			toDate,
			tagPattern,
			targetInterval,
			AggregatorType.findByName(AggregatorType.PAGE))
		return result
	}
	
	/**
	 * Marks {@link MeasuredValue}s which depend from param newResult and who's interval contains newResult as outdated.
	 * @param start
	 * 			00:00:00 of the respective interval.
	 * @param newResult
	 *			New {@link EventResult}.
	 */
	void markMvAsOutdated(DateTime start, EventResult newResult, MeasuredValueInterval interval){
		String pageTag = measuredValueTagService.createPageAggregatorTagByEventResult(newResult)
		if (pageTag) {
			MeasuredValue pageMv = ensurePresence(start, interval, pageTag)
			measuredValueUpdateEventDaoService.createUpdateEvent(pageMv.ident(), MeasuredValueUpdateEvent.UpdateCause.OUTDATED)
		}
	}
	
	/**
	 * Provides {@link MeasuredValue}s for given {@link JobGroup}s and a {@link MeasuredValueInterval} between toDate and fromDate.
	 * Non-existent {@link MeasuredValue}s will be created.
	 * All {@link MeasuredValue}s with @{link MeasuredValue.Calculated.Not} will be calculated and persisted with @{link MeasuredValue.Calculated.Yes}
	 * or @{link MeasuredValue.Calculated.YesNoData}.
	 * @param fromDateTime
	 * @param toDateTime
	 * @param interval
	 * @param csiGroups
	 * @return
	 */
	List<MeasuredValue> getOrCalculatePageMeasuredValues(Date fromDate, Date toDate, MeasuredValueInterval interval, List<JobGroup> csiGroups) {
		return getOrCalculatePageMeasuredValues(fromDate, toDate, interval, csiGroups, Page.list());
	}
	
	/**
	 * Provides {@link MeasuredValue}s for given {@link Page}s, {@link JobGroup}s and a {@link MeasuredValueInterval} between toDate and fromDate.
	 * Non-existent {@link MeasuredValue}s will be created.
	 * All {@link MeasuredValue}s with @{link MeasuredValue.Calculated.Not} will be calculated and persisted with @{link MeasuredValue.Calculated.Yes}
	 * or @{link MeasuredValue.Calculated.YesNoData}.
	 * @param fromDateTime
	 * @param toDateTime
	 * @param interval
	 * @param csiGroups
	 * @param pages
	 * @return
	 */
	List<MeasuredValue> getOrCalculatePageMeasuredValues(Date fromDate, Date toDate, MeasuredValueInterval interval, List<JobGroup> csiGroups, List<Page> pages) {
		
		DateTime toDateTime = new DateTime(toDate)
		DateTime fromDateTime = new DateTime(fromDate)
		if (fromDateTime.isAfter(toDateTime)) {
			throw new IllegalArgumentException("toDate must not be later than fromDate: fromDate=${fromDate}; toDate=${toDate}")
		}
		
		Integer numberOfIntervals = measuredValueUtilService.getNumberOfIntervals(fromDateTime, toDateTime, interval)
		List<MeasuredValue> existingMeasuredValues = findAll(fromDateTime.toDate(), toDateTime.toDate(), interval, csiGroups, pages)
		
		List<MeasuredValue> openMeasuredValues = existingMeasuredValues.findAll{ ! it.closedAndCalculated } 
		Boolean allMeasuredValuesExist = existingMeasuredValues.size() == numberOfIntervals * csiGroups.size() * pages.size() 
		if (allMeasuredValuesExist && openMeasuredValues.size() == 0) {
			return existingMeasuredValues
		}
		
		List<MeasuredValueUpdateEvent> updateEvents = []
		if (openMeasuredValues.size() > 0) updateEvents.addAll(measuredValueDaoService.getUpdateEvents(openMeasuredValues*.ident()))
		List<MeasuredValue> measuredValuesToBeCalculated = openMeasuredValues.findAll{it.hasToBeCalculatedAccordingEvents(updateEvents)}
		
		if (allMeasuredValuesExist && measuredValuesToBeCalculated.size() == 0) {
			
			return existingMeasuredValues;
			
		} else {
		
			List<MeasuredValue> calculatedMeasuredvalues = []
			DateTime currentDateTime = fromDateTime
			while (!currentDateTime.isAfter(toDateTime)) {
				performanceLoggingService.logExecutionTime(LogLevel.INFO, " get/create/calculate ${interval.name} page-MeasureValue for: ${currentDateTime}", IndentationDepth.TWO){
					List<MeasuredValue> existingMvsOfCurrentTime = existingMeasuredValues.findAll{new DateTime(it.started) == currentDateTime}
					List<MeasuredValue> mvsToBeCalculatedOfCurrentTime = measuredValuesToBeCalculated.findAll{new DateTime(it.started) == currentDateTime}
					if (existingMvsOfCurrentTime.size() == csiGroups.size() * pages.size() && 
						mvsToBeCalculatedOfCurrentTime.size() == 0) {
						
						calculatedMeasuredvalues.addAll(existingMvsOfCurrentTime)
						
					}else{
					
						calculatedMeasuredvalues.addAll(
							getOrCalculatePageMvs(currentDateTime, interval, csiGroups, pages, updateEvents)
						)
						
					}
				}
				currentDateTime = measuredValueUtilService.addOneInterval(currentDateTime, interval.intervalInMinutes)
				
			}
			return calculatedMeasuredvalues
		}
	}
	
	private List<MeasuredValue> getOrCalculatePageMvs(DateTime toGetMvsFor, MeasuredValueInterval interval, List<JobGroup> csiGroups, List<Page> csiPages, List<MeasuredValueUpdateEvent> updateEvents) {
		List<MeasuredValue> calculatedPageMvs = []
		MvCachingContainer cachingContainer = new MvCachingContainer()
		cachingContainer.hmvsByCsiGroupPageCombination = getHmvsByCsiGroupPageCombinationMap(csiGroups, csiPages, toGetMvsFor, toGetMvsFor.plusMinutes(interval.getIntervalInMinutes()))
		csiGroups.each{JobGroup group ->
			cachingContainer.csiGroupToCalcMvFor = group
			csiPages.each{Page page ->
				cachingContainer.pageToCalcMvFor = page
					String tag = measuredValueTagService.createPageAggregatorTag(group, page)
					calculatedPageMvs.add(ensurePresenceAndCalculation(toGetMvsFor, interval, tag, cachingContainer, updateEvents))
			}
		}
		return calculatedPageMvs
	}
	Map<String, List<MeasuredValue>> getHmvsByCsiGroupPageCombinationMap(List<JobGroup> csiGroups, List<Page> csiPages, DateTime startDateTime, DateTime endDateTime){
		List<MeasuredValue> hourlyMeasuredValues
		performanceLoggingService.logExecutionTime(LogLevel.DEBUG, "  calcMvForPageAggregator - getHmvs: getting", IndentationDepth.FOUR){
			MvQueryParams queryParams = new MvQueryParams();
			queryParams.jobGroupIds.addAll(csiGroups*.ident()); 
			queryParams.pageIds.addAll(csiPages*.ident());
			queryParams.measuredEventIds.addAll(measuredEventDaoService.getEventsFor(csiPages)*.ident());
			queryParams.browserIds.addAll(Browser.list()*.ident());
			queryParams.locationIds.addAll(Location.list()*.ident()); 
			hourlyMeasuredValues = eventMeasuredValueService.getHourylMeasuredValues(startDateTime.toDate(), endDateTime.toDate(), queryParams)
		}
		Map<String, List<MeasuredValue>> hmvsByCsiGroupPageCombination
		performanceLoggingService.logExecutionTime(LogLevel.DEBUG, "  calcMvForPageAggregator - getHmvs: iterate and write to map", IndentationDepth.FOUR){
			hmvsByCsiGroupPageCombination = [:].withDefault {[]}
			hourlyMeasuredValues.each{hmv ->
				JobGroup groupOfHmv = measuredValueTagService.findJobGroupOfHourlyEventTag(hmv.tag)
				Page pageOfHmv = measuredValueTagService.findPageOfHourlyEventTag(hmv.tag)
				if (groupOfHmv && pageOfHmv && csiGroups.contains(groupOfHmv) && csiPages.contains(pageOfHmv)) {
					hmvsByCsiGroupPageCombination["${groupOfHmv.ident()+UNIQUE_STRING_DELIMITTER+pageOfHmv.ident()}"].add(hmv)
				}
			}
		}
		return hmvsByCsiGroupPageCombination
	}
	/**
	 * Creates respective {@link MeasuredValue} if it doesn't exist and calculates it.
	 * After calculation status is {@link MeasuredValue.Calculated.Yes} or {@link MeasuredValue.Calculated.YesNoData}.
	 * @param startDate
	 * @param interval
	 * @param tag
	 * @param cachingContainer
	 * @return
	 */
	MeasuredValue ensurePresenceAndCalculation(DateTime startDate, MeasuredValueInterval interval, String tag, MvCachingContainer cachingContainer, List<MeasuredValueUpdateEvent> updateEvents) {
		MeasuredValue toCreateAndOrCalculate
		performanceLoggingService.logExecutionTime(LogLevel.DEBUG, "ensurePresence", IndentationDepth.THREE){
			toCreateAndOrCalculate = ensurePresence(startDate, interval, tag)
		}
		if (toCreateAndOrCalculate.hasToBeCalculatedAccordingEvents(updateEvents)) {
			performanceLoggingService.logExecutionTime(LogLevel.DEBUG, "calculateCustomerSatisfactionMeasuredValue (interval=${interval.intervalInMinutes}; aggregator=page)", IndentationDepth.THREE){
				toCreateAndOrCalculate = calcMv(toCreateAndOrCalculate, cachingContainer)
			}
		}
		return toCreateAndOrCalculate
	}
	private MeasuredValue ensurePresence(DateTime startDate, MeasuredValueInterval interval, String tag) {
		MeasuredValue toCreateAndOrCalculate
		AggregatorType pageAggregator = AggregatorType.findByName(AggregatorType.PAGE)
		performanceLoggingService.logExecutionTime(LogLevel.DEBUG, "ensurePresence.findByStarted", IndentationDepth.FOUR){
			toCreateAndOrCalculate = MeasuredValue.findByStartedAndIntervalAndAggregatorAndTag(startDate.toDate(), interval, pageAggregator, tag)
			log.debug("MeasuredValue.findByStartedAndIntervalAndAggregatorAndTag delivered ${toCreateAndOrCalculate?'a':'no'} result")
		}
		if (!toCreateAndOrCalculate) {
			toCreateAndOrCalculate = new MeasuredValue(
				started: startDate.toDate(),
				interval: interval,
				aggregator: pageAggregator,
				tag: tag,
				value: null,
				resultIds: ''
			).save(failOnError: true)
		}
		return toCreateAndOrCalculate
	}
	/**
	 * Calculates the given {@link MeasuredValue} toBeCalculated.
	 * @return The calculated {@link MeasuredValue}.
	 */
	MeasuredValue calcMv(MeasuredValue toBeCalculated, MvCachingContainer cachingContainer) {
		JobGroup targetCsiGroup
		Page targetPage
		performanceLoggingService.logExecutionTime(LogLevel.DEBUG, "  calcMvForPageAggregator - getJobGroupAndPageFromCachingContainer", IndentationDepth.FOUR){
			targetCsiGroup = cachingContainer.csiGroupToCalcMvFor
			targetPage = cachingContainer.pageToCalcMvFor
		}
		if (toBeCalculated == null || !targetCsiGroup || !targetPage) {
			log.error("MeasuerdValue can't be calculated: ${toBeCalculated}. targetCsiGroup=${targetCsiGroup}, targetPage=${targetPage}")
			return toBeCalculated
		}
		List<MeasuredValue> hmvsOfTargetCsiGroupAndPage = cachingContainer.hmvsByCsiGroupPageCombination["${targetCsiGroup.ident()+UNIQUE_STRING_DELIMITTER+targetPage.ident()}"]
		log.debug("Calculating Page-MeasuredValue: Calculation-database are ${hmvsOfTargetCsiGroupAndPage.size()} hourly Event-MeasuredValues.")
		
		List<WeightedCsiValue> weightedCsiValues = []
		if (hmvsOfTargetCsiGroupAndPage.size() > 0) {
			weightedCsiValues = weightingService.getWeightedCsiValues(hmvsOfTargetCsiGroupAndPage, [WeightFactor.HOUROFDAY, WeightFactor.BROWSER] as Set)  
			log.debug("weightedCsiValues.size()=${weightedCsiValues.size()}")
		}
		
		performanceLoggingService.logExecutionTime(LogLevel.DEBUG, "  calcMvForPageAggregator - calculation wmv: calc weighted mean", IndentationDepth.FOUR){
			if (weightedCsiValues.size() > 0) {
				toBeCalculated.value = meanCalcService.calculateWeightedMean(weightedCsiValues*.weightedValue)
			}
			measuredValueUpdateEventDaoService.createUpdateEvent(toBeCalculated.ident(), MeasuredValueUpdateEvent.UpdateCause.CALCULATED)
		}
		return toBeCalculated
	}
	private Double getHourlyWeightFrom(Date dateOfHour){
		return customerSatisfactionWeightService.getHoursOfDay()[new DateTime(dateOfHour).getHourOfDay()]
	}
	
	
	/**
	 * Provides weekly page-{@link MeasuredValue}s for all pages and all csi-groups between toDate and fromDate.
	 * Non-existent {@link MeasuredValue}s will be created.
	 * All {@link MeasuredValue}s with @{link MeasuredValue.Calculated.Not} will be calculated and persisted with @{link MeasuredValue.Calculated.Yes}
	 * or @{link MeasuredValue.Calculated.YesNoData}.
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @deprecated Use {@link #getOrCalculateWeeklyPageMeasuredValues(Date, Date, List, List)}
	 */
	@Deprecated
	List<MeasuredValue> getOrCalculateWeeklyPageMeasuredValues(Date fromDate, Date toDate) {
		MeasuredValueInterval weeklyInterval = MeasuredValueInterval.findByIntervalInMinutes(MeasuredValueInterval.WEEKLY);
		return getOrCalculatePageMeasuredValues(fromDate, toDate, weeklyInterval, JobGroup.findAllByGroupType(JobGroupType.CSI_AGGREGATION), Page.list())
	}
	
	/**
	 * Provides weekly page-{@link MeasuredValue}s for all pages and given csi-groups between toDate and fromDate.
	 * Non-existent {@link MeasuredValue}s will be created.
	 * All {@link MeasuredValue}s with @{link MeasuredValue.Calculated.Not} will be calculated and persisted with @{link MeasuredValue.Calculated.Yes}
	 * or @{link MeasuredValue.Calculated.YesNoData}.
	 * @param fromDate
	 * @param toDate
	 * @param csiGroups
	 * @return
	 *
	 * @deprecated Use {@link #getOrCalculateWeeklyPageMeasuredValues(Date, Date, List, List)}
	 */
	@Deprecated
	List<MeasuredValue> getOrCalculateWeeklyPageMeasuredValues(Date fromDate, Date toDate, List<JobGroup> csiGroups) {
		MeasuredValueInterval weeklyInterval = MeasuredValueInterval.findByIntervalInMinutes(MeasuredValueInterval.WEEKLY);
		return getOrCalculatePageMeasuredValues(fromDate, toDate, weeklyInterval, csiGroups, Page.list());
	}
}
