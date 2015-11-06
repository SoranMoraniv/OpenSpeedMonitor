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

package de.iteratec.osm.measurement.schedule

import de.iteratec.osm.measurement.environment.Browser
import de.iteratec.osm.measurement.environment.BrowserAlias
import de.iteratec.osm.measurement.environment.Location
import de.iteratec.osm.measurement.environment.WebPageTestServer
import de.iteratec.osm.measurement.script.Script
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.validation.ValidationException
import org.junit.Before
import org.junit.Test

import static org.hamcrest.Matchers.is
import static org.junit.Assert.*

/**
 * Test-suite of {@link JobService}.
 */
@TestFor(JobService)
@Mock([Job, Location, WebPageTestServer, Browser, BrowserAlias, JobGroup, Script])
class JobServiceTests {

	WebPageTestServer server1
	Location ffAgent1, ieAgent1, ieHetznerAgent1, ffHetznerAgent1, i8eHetznerAgent1
	Job job1, job2, job3, job4, job5, job6, job7, job8, job9
	String jobGroupName
	JobService serviceUnderTest
	private String labelJobWithExecutionSchedule = 'BV1 - Step 01'

	@Before
	void setUp() {

		serviceUnderTest=service

		createTestDataCommonForAllTests()
		
	}

	@Test
	void testGetCsiJobGroupOf_JobPrevioslyNotAssignedToAGroup() {
		JobGroup assignedJobGroup = serviceUnderTest.getCsiJobGroupOf(job2)
		assertNull(assignedJobGroup)
	}

	@Test
	void testGetJobGroup(){
		JobGroup jobGroup = serviceUnderTest.getCsiJobGroupOf(job1)
		assertNotNull jobGroup
		assertEquals(jobGroupName, jobGroup.name)
	}

	@Test
	void testGetAllCsiJobs(){
		List<Job> csiJobs = serviceUnderTest.getAllCsiJobs()
		assertEquals(1, csiJobs.size())
		assertEquals(job1, csiJobs[0])
	}

	@Test
	void testUpdateActivityFromFalseToTrue(){
		//get test specific data
		Job job = Job.findByLabel(labelJobWithExecutionSchedule)
		job.active = false
		job.save(failOnError: true)
		//test execution
		serviceUnderTest.updateActivity(job, true)
		//assertion
		assertThat(job.refresh().active, is(true))
	}
	@Test
	void testUpdateActivityFromFalseToFalse(){
		//get test specific data
		Job job = Job.findByLabel(labelJobWithExecutionSchedule)
		job.active = false
		job.save(failOnError: true)
		//test execution
		serviceUnderTest.updateActivity(job, false)
		//assertion
		assertThat(job.refresh().active, is(false))
	}

	@Test
	void testUpdateActivityFromTrueToFalse(){
		//get test specific data
		Job job = Job.findByLabel(labelJobWithExecutionSchedule)
		job.active = true
		job.save(failOnError: true)
		//test execution
		serviceUnderTest.updateActivity(job, false)
		//assertion
		assertThat(job.refresh().active, is(false))
	}
	@Test
	void testUpdateActivityFromTrueToTrue(){
		//get test specific data
		Job job = Job.findByLabel(labelJobWithExecutionSchedule)
		job.active = false
		job.save(failOnError: true)
		//test execution
		serviceUnderTest.updateActivity(job, true)
		//assertion
		assertThat(job.refresh().active, is(true))
	}

	@Test
	void testUpdatingExecutionScheduleWithValidSchedule(){
		//get test specific data
		Job job = Job.findByLabel(labelJobWithExecutionSchedule)
		//test execution
		String validSchedule = '0 0 */15 * * ? *'
		serviceUnderTest.updateExecutionSchedule(job, validSchedule)
		//assertion
		assertThat(job.refresh().executionSchedule, is(validSchedule))
	}
	@Test
	void testUpdatingExecutionScheduleWithInvalidSchedule(){
		//get test specific data
		Job job = Job.findByLabel(labelJobWithExecutionSchedule)
		String previousSchedule = job.executionSchedule
		String invalidSchedule = '0 0 */15 * * * *'
		//test execution
		shouldFail(ValidationException) {
			serviceUnderTest.updateExecutionSchedule(job, invalidSchedule)
		}
		//assertion
		assertThat(job.refresh().executionSchedule, is(previousSchedule))
	}

	private void createTestDataCommonForAllTests() {
		server1 = new WebPageTestServer(
				baseUrl: 'http://server1.wpt.server.de',
				active: true,
				label: 'server 1 - wpt server',
				proxyIdentifier: 'server 1 - wpt server'
		).save(failOnError: true)

		String browserName = "undefined"
		Browser.findByName(browserName) ?: new Browser(
				name: browserName,
				weight: 0)
				.addToBrowserAliases(alias: "undefined")
				.save(failOnError: true)
		browserName = "IE"
		Browser browserIE = Browser.findByName(browserName) ?: new Browser(
				name: browserName,
				weight: 45)
				.addToBrowserAliases(alias: "IE")
				.addToBrowserAliases(alias: "IE8")
				.addToBrowserAliases(alias: "Internet Explorer")
				.addToBrowserAliases(alias: "Internet Explorer 8")
				.save(failOnError: true)
		browserName = "FF"
		Browser browserFF = Browser.findByName(browserName) ?: new Browser(
				name: browserName,
				weight: 55)
				.addToBrowserAliases(alias: "FF")
				.addToBrowserAliases(alias: "FF7")
				.addToBrowserAliases(alias: "Firefox")
				.addToBrowserAliases(alias: "Firefox7")
				.save(failOnError: true)

		ffAgent1 = new Location(
				active: true,
				valid: 1,
				location: 'physNetLabAgent01-FF',
				label: 'physNetLabAgent01 - FF up to date',
				browser: browserFF,
				wptServer: server1
		).save(failOnError: true)

		ieAgent1 = new Location(
				active: true,
				valid: 1,
				location: 'physNetLabAgent01-IE8',
				label: 'physNetLabAgent01 - IE 8',
				browser: browserIE,
				wptServer: server1
		).save(failOnError: true)

		ieHetznerAgent1 = new Location(
				active: true,
				valid: 1,
				location: 'hetznerAgent01-IE8',
				label: 'hetznerAgent01 - IE 8',
				browser: browserIE,
				wptServer: server1
		).save(failOnError: true)

		i8eHetznerAgent1 = new Location(
				active: true,
				valid: 1,
				location: 'hetznerAgent01-IE8',
				label: 'hetznerAgent01 - IE 8',
				browser: browserIE,
				wptServer: server1
		).save(failOnError: true)

		ffHetznerAgent1 = new Location(
				active: true,
				valid: 1,
				location: 'hetznerAgent01-FF',
				label: 'hetznerAgent01 - FF up to date',
				browser: browserFF,
				wptServer: server1
		).save(failOnError: true)

		jobGroupName = 'CSI'
		JobGroup group = new JobGroup(
				name: jobGroupName,
				groupType: JobGroupType.CSI_AGGREGATION).save(failOnError: true)

		JobGroup nonCSIgroup = new JobGroup(
				name: 'nonCSIgroup',
				groupType: JobGroupType.RAW_DATA_SELECTION).save(failOnError: true)

		Script script = Script.createDefaultScript('Unnamed').save(failOnError: true)

		job1 = new Job(
				active: false,
				label: labelJobWithExecutionSchedule,
				description: 'This is job 01...',
				location: ffAgent1,
				frequencyInMin: 5,
				runs: 1,
				jobGroup: group,
				script: script,
				maxDownloadTimeInMinutes: 60,
				executionSchedule: '0 0 */5 * * ? *',
                customConnectivityProfile: true,
                customConnectivityName: 'Custom (6.000/512 Kbps, 50ms)',
                bandwidthDown: 6000,
                bandwidthUp: 512,
                latency: 50,
                packetLoss: 0
		).save(failOnError: true)
		job2 = new Job(
				active: false,
				label: 'BV1 - Step 02',
				description: 'This is job 02 which is not assigned to any job group',
				location: ffAgent1,
				frequencyInMin: 5,
				runs: 1,
				jobGroup: nonCSIgroup,
				script: script,
				maxDownloadTimeInMinutes: 60,
                customConnectivityProfile: true,
                customConnectivityName: 'Custom (6.000/512 Kbps, 50ms)',
                bandwidthDown: 6000,
                bandwidthUp: 512,
                latency: 50,
                packetLoss: 0
		).save(failOnError: true)
		job3 = new Job(
				active: false,
				label: 'BV1 - Step 03',
				description: 'This is job 03...',
				location: ieAgent1,
				frequencyInMin: 5,
				runs: 1,
				jobGroup: nonCSIgroup,
				script: script,
				maxDownloadTimeInMinutes: 60,
                customConnectivityProfile: true,
                customConnectivityName: 'Custom (6.000/512 Kbps, 50ms)',
                bandwidthDown: 6000,
                bandwidthUp: 512,
                latency: 50,
                packetLoss: 0
		).save(failOnError: true)
		job4 = new Job(
				active: false,
				label: 'BV1 - Step 04',
				description: 'This is job 04...',
				location: ieAgent1,
				frequencyInMin: 5,
				runs: 1,
				jobGroup: nonCSIgroup,
				script: script,
				maxDownloadTimeInMinutes: 60,
                customConnectivityProfile: true,
                customConnectivityName: 'Custom (6.000/512 Kbps, 50ms)',
                bandwidthDown: 6000,
                bandwidthUp: 512,
                latency: 50,
                packetLoss: 0
		).save(failOnError: true)

		job5 = new Job(
				active: false,
				label: 'BV1 - Step 05',
				description: 'This is job 05...',
				location: ieAgent1,
				frequencyInMin: 5,
				runs: 1,
				jobGroup: nonCSIgroup,
				script: script,
				maxDownloadTimeInMinutes: 60,
                customConnectivityProfile: true,
                customConnectivityName: 'Custom (6.000/512 Kbps, 50ms)',
                bandwidthDown: 6000,
                bandwidthUp: 512,
                latency: 50,
                packetLoss: 0
		).save(failOnError: true)

		job6 = new Job(
				active: false,
				label: 'BV1 - Step 06',
				description: 'This is job 06...',
				location: ffHetznerAgent1,
				frequencyInMin: 5,
				runs: 1,
				jobGroup: nonCSIgroup,
				script: script,
				maxDownloadTimeInMinutes: 60,
                customConnectivityProfile: true,
                customConnectivityName: 'Custom (6.000/512 Kbps, 50ms)',
                bandwidthDown: 6000,
                bandwidthUp: 512,
                latency: 50,
                packetLoss: 0
		).save(failOnError: true)
		job7 = new Job(
				active: false,
				label: 'BV1 - Step 07',
				description: 'This is job 07...',
				location: ieHetznerAgent1,
				frequencyInMin: 5,
				runs: 1,
				jobGroup: nonCSIgroup,
				script: script,
				maxDownloadTimeInMinutes: 60,
                customConnectivityProfile: true,
                customConnectivityName: 'Custom (6.000/512 Kbps, 50ms)',
                bandwidthDown: 6000,
                bandwidthUp: 512,
                latency: 50,
                packetLoss: 0
		).save(failOnError: true)

		job8 = new Job(
				active: false,
				label: 'BV1 - Step 08',
				description: 'This is job 08...',
				location: ieHetznerAgent1,
				frequencyInMin: 5,
				runs: 1,
				jobGroup: nonCSIgroup,
				script: script,
				maxDownloadTimeInMinutes: 60,
                customConnectivityProfile: true,
                customConnectivityName: 'Custom (6.000/512 Kbps, 50ms)',
                bandwidthDown: 6000,
                bandwidthUp: 512,
                latency: 50,
                packetLoss: 0
		).save(failOnError: true)

		job9 = new Job(
				active: false,
				label: 'BV1 - Step 09',
				description: 'This is job 09...',
				location: i8eHetznerAgent1,
				frequencyInMin: 5,
				runs: 1,
				jobGroup: nonCSIgroup,
				script: script,
				maxDownloadTimeInMinutes: 60,
                customConnectivityProfile: true,
                customConnectivityName: 'Custom (6.000/512 Kbps, 50ms)',
                bandwidthDown: 6000,
                bandwidthUp: 512,
                latency: 50,
                packetLoss: 0
		).save(failOnError: true)
	}
}
