package de.iteratec.osm.result

import de.iteratec.osm.measurement.schedule.ConnectivityProfile
import de.iteratec.osm.report.chart.MeasuredValueInterval
import de.iteratec.osm.util.DateValueConverter
import grails.validation.Validateable
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import java.util.regex.Pattern

/**
 * <p>
 * Command of {@link EventResultDashboardController#showAll(EventResultDashboardShowAllCommand)
 *}.
 * </p>
 *
 * <p>
 * None of the properties will be <code>null</code> for a valid instance.
 * Some collections might be empty depending on the {@link #aggrGroup}
 * used.
 * </p>
 *
 * @author mze , rhe
 * @since IT-6
 */
@Validateable(nullable = true)
public class EventResultDashboardShowAllCommand {

    public final static Integer LINE_CHART_SELECTION = 0;
    public final static Integer POINT_CHART_SELECTION = 1;

    /**
     * The selected start date.
     *
     * Please use {@link #getSelectedTimeFrame()}.
     */
    Date from

    /**
     * The selected end date.
     *
     * Please use {@link #getSelectedTimeFrame()}.
     */
    Date to

    /**
     * The selected start hour of date.
     *
     * Please use {@link #getSelectedTimeFrame()}.
     */
    String fromHour

    /**
     * The selected end hour of date.
     *
     * Please use {@link #getSelectedTimeFrame()}.
     */
    String toHour

    /**
     * The name of the {@link de.iteratec.osm.report.chart.AggregatorType}.
     *
     * @deprecated Currently unused! TODO Check for usages, if not required: remove!
     */
    @Deprecated
    String aggrGroup

    /**
     * The time of the {@link de.iteratec.osm.report.chart.MeasuredValueInterval}.
     */
    Integer selectedInterval

    /**
     * A predefined time frame.
     */
    int selectedTimeFrameInterval = 259200

    /**
     * The Selected chart type (line or point)
     */
    Integer selectChartType;

    /**
     * The database IDs of the selected {@linkplain de.iteratec.osm.measurement.schedule.JobGroup CSI groups}
     * which are the systems measured for a CSI value
     */
    Collection<Long> selectedFolder = []

    /**
     * The database IDs of the selected {@linkplain de.iteratec.osm.csi.Page pages}
     * which results to be shown.
     */
    Collection<Long> selectedPages = []

    /**
     * The database IDs of the selected {@linkplain de.iteratec.osm.result.MeasuredEvent
     * measured events} which results to be shown.
     *
     * These selections are only relevant if
     * {@link #selectedAllMeasuredEvents} is evaluated to
     * <code>false</code>.
     */
    Collection<Long> selectedMeasuredEventIds = []

    /**
     * User enforced the selection of all measured events.
     * This selection <em>is not</em> reflected in
     * {@link #selectedMeasuredEventIds} cause of URL length
     * restrictions. If this flag is evaluated to
     * <code>true</code>, the selections in
     * {@link #selectedMeasuredEventIds} should be ignored.
     */
    Boolean selectedAllMeasuredEvents = true

    /**
     * The database IDs of the selected {@linkplain de.iteratec.osm.measurement.environment.Browser
     * browsers} which results to be shown.
     *
     * These selections are only relevant if
     * {@link #selectedAllBrowsers} is evaluated to
     * <code>false</code>.
     */
    Collection<Long> selectedBrowsers = []

    /**
     * User enforced the selection of all browsers.
     * This selection <em>is not</em> reflected in
     * {@link #selectedBrowsers} cause of URL length
     * restrictions. If this flag is evaluated to
     * <code>true</code>, the selections in
     * {@link #selectedBrowsers} should be ignored.
     */
    Boolean selectedAllBrowsers = true

    /**
     * The database IDs of the selected {@linkplain de.iteratec.osm.measurement.environment.Location
     * locations} which results to be shown.
     *
     * These selections are only relevant if
     * {@link #selectedAllLocations} is evaluated to
     * <code>false</code>.
     */
    Collection<Long> selectedLocations = []

    /**
     * User enforced the selection of all locations.
     * This selection <em>is not</em> reflected in
     * {@link #selectedLocations} cause of URL length
     * restrictions. If this flag is evaluated to
     * <code>true</code>, the selections in
     * {@link #selectedLocations} should be ignored.
     */
    Boolean selectedAllLocations = true

    /**
     * The database IDs of the selected {@linkplain de.iteratec.osm.measurement.schedule.ConnectivityProfile}s which results to be shown.
     *
     * These selections are only relevant if
     * {@link #selectedAllConnectivityProfiles} is evaluated to
     * <code>false</code>.
     */
    Collection<Long> selectedConnectivityProfiles = []

    /**
     * User enforced the selection of all ConnectivityProfiles.
     * This selection <em>is not</em> reflected in
     * {@link #selectedConnectivityProfiles} cause of URL length
     * restrictions. If this flag is evaluated to
     * <code>true</code>, the selections in
     * {@link #selectedConnectivityProfiles} should be ignored.
     */
    Boolean selectedAllConnectivityProfiles = true

    /**
     * Database name of the selected {@link de.iteratec.osm.report.chart.AggregatorType}, selected by the user.
     * Determines wich {@link CachedView#CACHED} results should be shown.
     */
    Collection<String> selectedAggrGroupValuesCached = []

    /**
     * Database name of the selected {@link de.iteratec.osm.report.chart.AggregatorType}, selected by the user.
     * Determines wich {@link CachedView#UNCACHED} results should be shown.
     */
    Collection<String> selectedAggrGroupValuesUnCached = []

    /**
     * Lower bound for load-time-measurands. Values lower than this will be excluded from graphs.
     */
    Integer trimBelowLoadTimes

    /**
     * Upper bound for load-time-measurands. Values greater than this will be excluded from graphs.
     */
    Integer trimAboveLoadTimes

    /**
     * Lower bound for request-count-measurands. Values lower than this will be excluded from graphs.
     */
    Integer trimBelowRequestCounts

    /**
     * Upper bound for request-count-measurands. Values greater than this will be excluded from graphs.
     */
    Integer trimAboveRequestCounts

    /**
     * Lower bound for request-sizes-measurands. Values lower than this will be excluded from graphs.
     */
    Integer trimBelowRequestSizes

    /**
     * Upper bound for request-sizes-measurands. Values greater than this will be excluded from graphs.
     */
    Integer trimAboveRequestSizes

    /**
     * If the user has been warned about a potentially long processing
     * time, did he overwrite the waring and really want to perform
     * the request?
     *
     * A value of <code>true</code> indicates that overwrite, everything
     * should be done as requested, <code>false</code> indicates that
     * the user hasn't been warned before, so there is no overwrite.
     */
    Boolean overwriteWarningAboutLongProcessingTime;

    /**
     * Flag for manual debugging.
     * Used for debugging highcharts-export-server, e.g.
     */
    Boolean debug

    /**
     * Whether or not the time of the start-date should be selected manually.
     */
    Boolean setFromHour
    /**
     * Whether or not the time of the start-date should be selected manually.
     */
    Boolean setToHour

    /**
     * Whether or not EventResults measured with native connectivity should get included.
     */
    Boolean includeNativeConnectivity

    /**
     * Whether or not EventResults measured with native connectivity should get included.
     */
    Boolean includeCustomConnectivity

    /**
     * If set, this is handled as a regular expression to select results measured with custom connectivity and whos custom
     * connectivity name matches this regex.
     */
    String customConnectivityName

    /**
     * Constraints needs to fit.
     */
    static constraints = {
        from(nullable: true, validator: { Date currentFrom, EventResultDashboardShowAllCommand cmd ->
            boolean manualTimeframe = cmd.selectedTimeFrameInterval == 0
            if (manualTimeframe && currentFrom == null) return ['de.iteratec.isr.EventResultDashboardController$ShowAllCommand.from.nullWithManualSelection']
        })
        to(nullable: true, validator: { Date currentTo, EventResultDashboardShowAllCommand cmd ->
            boolean manualTimeframe = cmd.selectedTimeFrameInterval == 0
            if (manualTimeframe && currentTo == null) return ['de.iteratec.isr.EventResultDashboardController$ShowAllCommand.to.nullWithManualSelection']
            else if (manualTimeframe && currentTo != null && cmd.from != null && currentTo.before(cmd.from)) return ['de.iteratec.isr.EventResultDashboardController$ShowAllCommand.to.beforeFromDate']
        })
        fromHour(nullable: true, validator: { String currentFromHour, EventResultDashboardShowAllCommand cmd ->
            boolean manualTimeframe = cmd.selectedTimeFrameInterval == 0
            if (manualTimeframe && currentFromHour == null) return ['de.iteratec.isr.EventResultDashboardController$ShowAllCommand.fromHour.nullWithManualSelection']
        })
        toHour(nullable: true, validator: { String currentToHour, EventResultDashboardShowAllCommand cmd ->
            boolean manualTimeframe = cmd.selectedTimeFrameInterval == 0
            if (manualTimeframe && currentToHour == null) {
                return ['de.iteratec.isr.EventResultDashboardController$ShowAllCommand.toHour.nullWithManualSelection']
            } else if (manualTimeframe && cmd.from != null && cmd.to != null && cmd.from.equals(cmd.to) && cmd.fromHour != null && currentToHour != null) {
                DateTime firstDayWithFromDaytime = getFirstDayWithTime(cmd.fromHour)
                DateTime firstDayWithToDaytime = getFirstDayWithTime(currentToHour)
                if (!firstDayWithToDaytime.isAfter(firstDayWithFromDaytime)) return ['de.iteratec.isr.EventResultDashboardController$ShowAllCommand.toHour.inCombinationWithDateBeforeFrom']
            }
        })
        selectedAggrGroupValuesCached(nullable: false, validator: { Collection<String> selectedCheckedAggregators, EventResultDashboardShowAllCommand cmd ->
            if (cmd.selectedAggrGroupValuesCached.size() < 1 && cmd.selectedAggrGroupValuesUnCached.size() < 1) return ['de.iteratec.isr.EventResultDashboardController$ShowAllCommand.selectedAggrGroupValuesCached.validator.error.selectedAggrGroupValuesCached']
        })
        selectedAllMeasuredEvents(nullable: true)
        selectedAllBrowsers(nullable: true)
        selectedAllLocations(nullable: true)

        selectedFolder(nullable: false, validator: { Collection currentCollection, EventResultDashboardShowAllCommand cmd ->
            if (currentCollection.isEmpty()) return ['de.iteratec.isr.EventResultDashboardController$ShowAllCommand.selectedFolder.validator.error.selectedFolder']
        })
        selectedPages(nullable: false, validator: { Collection currentCollection, EventResultDashboardShowAllCommand cmd ->
            if (currentCollection.isEmpty()) return ['de.iteratec.isr.EventResultDashboardController$ShowAllCommand.selectedPage.validator.error.selectedPage']
        })
        selectedBrowsers(nullable: false, validator: { Collection currentCollection, EventResultDashboardShowAllCommand cmd ->
            if (!cmd.selectedAllBrowsers && currentCollection.isEmpty()) return ['de.iteratec.isr.EventResultDashboardController$ShowAllCommand.selectedBrowsers.validator.error.selectedBrowsers']
        })
        selectedMeasuredEventIds(nullable: false, validator: { Collection currentCollection, EventResultDashboardShowAllCommand cmd ->
            if (!cmd.selectedAllMeasuredEvents && currentCollection.isEmpty()) return ['de.iteratec.isr.EventResultDashboardController$ShowAllCommand.selectedMeasuredEvents.validator.error.selectedMeasuredEvents']
        })
        selectedLocations(nullable: false, validator: { Collection currentCollection, EventResultDashboardShowAllCommand cmd ->
            if (!cmd.selectedAllLocations && currentCollection.isEmpty()) return ['de.iteratec.isr.EventResultDashboardController$ShowAllCommand.selectedLocations.validator.error.selectedLocations']
        })
        selectedAllConnectivityProfiles(nullable: true)

        overwriteWarningAboutLongProcessingTime(nullable: true)

        includeNativeConnectivity(nullable: false)

        includeCustomConnectivity(nullable: false)

        customConnectivityName(nullable: true)

        //TODO: validators for trimAbove's and -Below's

    }

    static transients = ['selectedTimeFrame']

    /**
     * <p>
     * Returns the selected time frame as {@link org.joda.time.Interval}.
     * That is the interval from {@link #from} / {@link #fromHour} to {@link #to} / {@link #toHour} if {@link #selectedTimeFrameInterval} is 0 (that means manual).
     * If {@link #selectedTimeFrameInterval} is greater 0 the returned time frame is now minus {@link #selectedTimeFrameInterval} minutes to now.
     * </p>
     *
     * @return not <code>null</code>.
     * @throws IllegalStateException
     *         if called on an invalid instance.
     */
    public Interval getSelectedTimeFrame() throws IllegalStateException {
        if (!this.validate()) {
            throw new IllegalStateException('A time frame is not available from an invalid command.')
        }

        DateTime start
        DateTime end

        Boolean manualTimeframe = this.selectedTimeFrameInterval == 0
        if (manualTimeframe && fromHour && toHour) {

            DateTime firstDayWithFromHourAsDaytime = getFirstDayWithTime(fromHour)
            DateTime firstDayWithToHourAsDaytime = getFirstDayWithTime(toHour)

            start = new DateTime(this.from.getTime())
                    .withTime(
                    firstDayWithFromHourAsDaytime.getHourOfDay(),
                    firstDayWithFromHourAsDaytime.getMinuteOfHour(),
                    0, 0
            )
            end = new DateTime(this.to.getTime())
                    .withTime(
                    firstDayWithToHourAsDaytime.getHourOfDay(),
                    firstDayWithToHourAsDaytime.getMinuteOfHour(),
                    59, 999
            )

        } else {

            end = new DateTime()
            start = end.minusSeconds(this.selectedTimeFrameInterval)

        }

        return new Interval(start, end);
    }

    /**
     * Returns a {@link DateTime} of the first day in unix-epoch with daytime respective param timeWithOrWithoutMeridian.
     * @param timeWithOrWithoutMeridian
     * 		The format can be with or without meridian (e.g. "04:45", "16:12" without or "02:00 AM", "11:23 PM" with meridian)
     * @return A {@link DateTime} of the first day in unix-epoch with daytime respective param timeWithOrWithoutMeridian.
     * @throws IllegalStateException If timeWithOrWithoutMeridian is in wrong format.
     */
    public static DateTime getFirstDayWithTime(String timeWithOrWithoutMeridian) throws IllegalStateException {

        Pattern regexWithMeridian = ~/\d{1,2}:\d\d [AP]M/
        Pattern regexWithoutMeridian = ~/\d{1,2}:\d\d/
        String dateFormatString

        if (timeWithOrWithoutMeridian ==~ regexWithMeridian) dateFormatString = "dd.MM.yyyy hh:mm"
        else if (timeWithOrWithoutMeridian ==~ regexWithoutMeridian) dateFormatString = "dd.MM.yyyy HH:mm"
        else throw new IllegalStateException("Wrong format of time: ${timeWithOrWithoutMeridian}")

        DateTimeFormatter fmt = DateTimeFormat.forPattern(dateFormatString)
        return fmt.parseDateTime("01.01.1970 ${timeWithOrWithoutMeridian}")

    }

    /**
     * <p>
     * Copies all request data to the specified map. This operation does
     * not care about the validation status of this instance.
     * For missing values the defaults are inserted.
     * </p>
     *
     * @param viewModelToCopyTo
     *         The {@link Map} the request data contained in this command
     *         object should be copied to. The map must be modifiable.
     *         Previously contained data will be overwritten.
     *         The argument might not be <code>null</code>.
     */
    public void copyRequestDataToViewModelMap(Map<String, Object> viewModelToCopyTo) {

        viewModelToCopyTo.put('selectedTimeFrameInterval', this.selectedTimeFrameInterval)
        viewModelToCopyTo.put('selectedInterval', this.selectedInterval ?: MeasuredValueInterval.RAW)

        viewModelToCopyTo.put('selectedFolder', this.selectedFolder)
        viewModelToCopyTo.put('selectedPages', this.selectedPages)

        viewModelToCopyTo.put('selectedAllMeasuredEvents', this.selectedAllMeasuredEvents)
        viewModelToCopyTo.put('selectedMeasuredEventIds', this.selectedMeasuredEventIds)

        viewModelToCopyTo.put('selectedAllBrowsers', this.selectedAllBrowsers)
        viewModelToCopyTo.put('selectedBrowsers', this.selectedBrowsers)

        viewModelToCopyTo.put('selectedAllLocations', this.selectedAllLocations)
        viewModelToCopyTo.put('selectedLocations', this.selectedLocations)

        viewModelToCopyTo.put('selectedAllConnectivityProfiles', this.selectedAllConnectivityProfiles)
        viewModelToCopyTo.put('selectedConnectivityProfiles', this.selectedConnectivityProfiles)
        viewModelToCopyTo.put('includeNativeConnectivity', this.includeNativeConnectivity)
        viewModelToCopyTo.put('includeCustomConnectivity', this.includeCustomConnectivity)
        viewModelToCopyTo.put('customConnectivityName', this.customConnectivityName)

        DateValueConverter converter = DateValueConverter.getConverter()

        viewModelToCopyTo.put('from', this.from)
        if (!this.fromHour.is(null)) {
            viewModelToCopyTo.put('fromHour', this.fromHour)
        }

        viewModelToCopyTo.put('to', this.to)
        if (!this.toHour.is(null)) {
            viewModelToCopyTo.put('toHour', this.toHour)
        }

        viewModelToCopyTo.put("selectedChartType", this.selectChartType ? POINT_CHART_SELECTION : LINE_CHART_SELECTION);

        viewModelToCopyTo.put('selectedAggrGroupValuesCached', this.selectedAggrGroupValuesCached)
        viewModelToCopyTo.put('selectedAggrGroupValuesUnCached', this.selectedAggrGroupValuesUnCached)

        viewModelToCopyTo.put('trimBelowLoadTimes', this.trimBelowLoadTimes)
        viewModelToCopyTo.put('trimAboveLoadTimes', this.trimAboveLoadTimes)
        viewModelToCopyTo.put('trimBelowRequestCounts', this.trimBelowRequestCounts)
        viewModelToCopyTo.put('trimAboveRequestCounts', this.trimAboveRequestCounts)
        viewModelToCopyTo.put('trimBelowRequestSizes', this.trimBelowRequestSizes)
        viewModelToCopyTo.put('trimAboveRequestSizes', this.trimAboveRequestSizes)
        viewModelToCopyTo.put('debug', this.debug ?: false)
        viewModelToCopyTo.put('setFromHour', this.setFromHour)
        viewModelToCopyTo.put('setToHour', this.setToHour)

    }

    /**
     * <p>
     * Creates {@link MvQueryParams} based on this command. This command
     * need to be valid for this operation to be successful.
     * </p>
     *
     * @return not <code>null</code>.
     * @throws IllegalStateException
     *         if called on an invalid instance.
     */
    public MvQueryParams createMvQueryParams() throws IllegalStateException {
        if (!this.validate()) {
            throw new IllegalStateException('Query params are not available from an invalid command.')
        }

        ErQueryParams result = new ErQueryParams();

        result.jobGroupIds.addAll(this.selectedFolder);

        if (!this.selectedAllMeasuredEvents) {
            result.measuredEventIds.addAll(this.selectedMeasuredEventIds);
        }

        result.pageIds.addAll(this.selectedPages);

        if (!this.selectedAllBrowsers) {
            result.browserIds.addAll(this.selectedBrowsers);
        }

        if (!this.selectedAllLocations) {
            result.locationIds.addAll(this.selectedLocations);
        }
        if (this.trimBelowLoadTimes) {
            result.minLoadTimeInMillisecs = this.trimBelowLoadTimes
        }
        if (this.trimAboveLoadTimes) {
            result.maxLoadTimeInMillisecs = this.trimAboveLoadTimes
        }
        if (this.trimBelowRequestCounts) {
            result.minRequestCount = this.trimBelowRequestCounts
        }
        if (this.trimAboveRequestCounts) {
            result.maxRequestCount = this.trimAboveRequestCounts
        }
        if (this.trimBelowRequestSizes) {
            result.minRequestSizeInBytes = this.trimBelowRequestSizes * 1000
        }
        if (this.trimAboveRequestSizes) {
            result.maxRequestSizeInBytes = this.trimAboveRequestSizes * 1000
        }
        result.includeNativeConnectivity = this.includeNativeConnectivity
        result.includeCustomConnectivity = this.includeCustomConnectivity
        if (this.includeCustomConnectivity){
            result.customConnectivityNameRegex = this.customConnectivityName ?: '.*'
        }
        if (this.selectedAllConnectivityProfiles){
            result.connectivityProfileIds.addAll(ConnectivityProfile.list()*.ident())
        }else if (this.selectedConnectivityProfiles.size() > 0){
            result.connectivityProfileIds.addAll(this.selectedConnectivityProfiles)
        }

        return result;
    }
}
