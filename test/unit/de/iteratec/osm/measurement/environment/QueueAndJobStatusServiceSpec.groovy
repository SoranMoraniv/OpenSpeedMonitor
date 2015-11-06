package de.iteratec.osm.measurement.environment

import co.freeside.betamax.Betamax
import co.freeside.betamax.Recorder
import co.freeside.betamax.tape.yaml.OrderedPropertyComparator
import co.freeside.betamax.tape.yaml.TapePropertyUtils
import de.iteratec.osm.OsmConfiguration
import de.iteratec.osm.csi.TestDataUtil
import de.iteratec.osm.measurement.environment.wptserverproxy.HttpRequestService
import de.iteratec.osm.measurement.schedule.Job
import de.iteratec.osm.measurement.schedule.JobGroup
import de.iteratec.osm.measurement.schedule.JobService
import de.iteratec.osm.measurement.script.Script
import de.iteratec.osm.report.chart.AggregatorType
import de.iteratec.osm.report.chart.MeasuredValueInterval
import de.iteratec.osm.util.ServiceMocker
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovyx.net.http.RESTClient
import org.apache.http.HttpHost
import org.joda.time.DateTime
import org.junit.Rule
import org.yaml.snakeyaml.introspector.Property
import spock.lang.Specification

import static org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY

@TestFor(QueueAndJobStatusService)
@Mock([WebPageTestServer, Location, Job, Browser, BrowserAlias, JobGroup, Script, OsmConfiguration, AggregatorType, MeasuredValueInterval])
class QueueAndJobStatusServiceSpec extends Specification {

    @Rule
    public Recorder recorder = new Recorder(new ConfigSlurper().parse(new File('grails-app/conf/BetamaxConfig.groovy').toURL()).toProperties())

    public static final String WPTSERVER_URL = 'dev.server01.wpt.iteratec.de'
    private static final String LOCATION_IDENTIFIER_SERVER1_HETZNER = 'iteratec-dev-hetzner-win7'
    private static final String LOCATION_IDENTIFIER_SERVER1_NEXUS = 'iteratec-dev-GoogleNexus'

    public static final String WPTSERVER2_URL = 'dev.server02.wpt.iteratec.de'
    private static final String LOCATION_IDENTIFIER_SERVER2_HETZNER = 'iteratec-dev-hetzner-64bit-ssd'

    QueueAndJobStatusService serviceUnderTest

    ServiceMocker mocker

    WebPageTestServer server1
    WebPageTestServer server2

    private String labelJobWithExecutionSchedule = 'BV1 - Step 01'
    String jobGroupName

    @Betamax(tape = 'CreateChartData creates a map entry per server')
    def "CreateChartData creates a map entry per server"() {
        given:
        def start = new DateTime()
        def end = start.plusDays(1)
        mockServices()
        createTestDataCommonForAllTests()

        when:
        def resultMap = serviceUnderTest.createChartData(start, end)

        then:
        resultMap.keySet().size() == 2
    }

    @Betamax(tape = 'CreateChartData creates entry for each location')
    def "CreateChartData creates entry for each location"() {
        given:
        def start = new DateTime()
        def end = start.plusDays(1)
        mockServices()
        createTestDataCommonForAllTests()

        when:
        def resultMap = serviceUnderTest.createChartData(start, end)

        then:
        resultMap.get(server1).size() == 2
        resultMap.get(server2).size() == 1
    }

    private void mockServices() {
        serviceUnderTest = service
        mocker = ServiceMocker.create()
        mocker.mockI18nService(serviceUnderTest)
        serviceUnderTest.jobService = Mock(JobService)

        // betamax fix for sorting
        TapePropertyUtils.metaClass.sort = { Set<Property> properties, List<String> names ->
            new LinkedHashSet(properties.sort(true, new OrderedPropertyComparator(names)))
        }

        Map betamaxProps = new ConfigSlurper().parse(new File('grails-app/conf/BetamaxConfig.groovy').toURL()).flatten()
        HttpRequestService httpRequestService = new HttpRequestService()
        httpRequestService.metaClass.getRestClientFrom = { WebPageTestServer wptserver ->
            RESTClient restClient = new RESTClient(wptserver.baseUrl)
            restClient.client.params.setParameter(DEFAULT_PROXY, new HttpHost(betamaxProps['betamax.proxyHost'], betamaxProps['betamax.proxyPort'], 'http'))
            return restClient
        }

        serviceUnderTest.httpRequestService = httpRequestService
    }


    private void createTestDataCommonForAllTests() {
        TestDataUtil.createOsmConfig()
        TestDataUtil.createAggregatorTypes()
        TestDataUtil.createMeasuredValueIntervals()
        server1 = TestDataUtil.createWebPageTestServer(WPTSERVER_URL, WPTSERVER_URL, true, "http://${WPTSERVER_URL}/")
        server2 = TestDataUtil.createWebPageTestServer(WPTSERVER2_URL, WPTSERVER2_URL, true, "http://${WPTSERVER2_URL}/")
        List<Browser> browsers = TestDataUtil.createBrowsersAndAliases()
        TestDataUtil.createJobGroups()
        TestDataUtil.createLocation(server1, LOCATION_IDENTIFIER_SERVER1_HETZNER, browsers[0], true)
        TestDataUtil.createLocation(server1, LOCATION_IDENTIFIER_SERVER1_NEXUS, browsers[0], true)
        TestDataUtil.createLocation(server2, LOCATION_IDENTIFIER_SERVER2_HETZNER, browsers[1], true)
    }
}
