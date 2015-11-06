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

import static grails.async.Promises.*
import de.iteratec.osm.ConfigService
import de.iteratec.osm.InMemoryConfigService
import de.iteratec.osm.measurement.environment.QueueAndJobStatusService
import de.iteratec.osm.measurement.script.PlaceholdersUtility
import de.iteratec.osm.measurement.script.Script
import de.iteratec.osm.result.JobResult
import de.iteratec.osm.util.PerformanceLoggingService
import de.iteratec.osm.util.PerformanceLoggingService.IndentationDepth
import de.iteratec.osm.util.PerformanceLoggingService.LogLevel
import grails.async.Promise
import grails.converters.JSON
import grails.gsp.PageRenderer
import groovy.json.JsonBuilder
import groovy.time.TimeCategory

import org.quartz.CronExpression
import org.springframework.http.HttpStatus

class JobController {

	static final int LAST_N_MINUTES_TO_SHOW_SUCCESSFUL_RESULTS_IN_JOBLIST = 5
	static final int LAST_N_HOURS_TO_SHOW_FAILED_RESULTS_IN_JOBLIST = 2
	static final int LAST_N_HOURS_TO_SHOW_PENDING_OR_RUNNNG_RESULTS_IN_JOBLIST = 2

	PageRenderer groovyPageRenderer
	JobProcessingService jobProcessingService
	JobService jobService
	QueueAndJobStatusService queueAndJobStatusService
	def quartzScheduler
	def messageSource
	PerformanceLoggingService performanceLoggingService
	ConfigService configService
	InMemoryConfigService inMemoryConfigService

	private String getJobI18n() {
		return message(code: 'de.iteratec.isj.job', default: 'Job')
	}

	void redirectIfNotFound(Job job, def id) {
		def flashMessageArgs = [getJobI18n(), id]
		if (!job) {
			flash.message = message(code: 'default.not.found.message', args: flashMessageArgs)
			redirect(action: "list")
		}
	}

	private Map getListModel(boolean forceShowAllJobs = false) {
		List<Job> jobs
		boolean onlyActiveJobs = false
		// custom sort for nextExecutionTime necessary due to it being neither persisted in the
		// database nor derived. Thus it cannot be passed to database layer sorting
		if (params.sort == 'nextExecutionTime') {
			performanceLoggingService.logExecutionTime(LogLevel.DEBUG, 'sorting via nextExecutionTime: query jobs from db', IndentationDepth.ONE) {
				jobs = Job.list()
			}
			performanceLoggingService.logExecutionTime(LogLevel.DEBUG, 'sorting via nextExecutionTime: sorting', IndentationDepth.ONE) {
				jobs.sort { if (it.active) it.getNextExecutionTime() }
			}
			performanceLoggingService.logExecutionTime(LogLevel.DEBUG, 'sorting via nextExecutionTime: reversing', IndentationDepth.ONE) {
				if (params.order == 'desc') jobs.reverse(true)
			}
		} else if (params.sort || request.xhr || forceShowAllJobs) {
			performanceLoggingService.logExecutionTime(LogLevel.DEBUG, 'NOT sorting via nextExecutionTime: query jobs from db', IndentationDepth.ONE) {
				jobs = Job.list(params)
			}
		} else {
			jobs = Job.findAllByActive(true)
			onlyActiveJobs = true
		}
		def model = [
			jobs: jobs,
			jobsWithTags: jobService.listJobsWithTags(),
			onlyActiveJobs: onlyActiveJobs,
			filters: params.filters,
			measurementsEnabled: inMemoryConfigService.areMeasurementsGenerallyEnabled(),
			lastNMinutesToShowSuccessfulResultsInJoblist: LAST_N_MINUTES_TO_SHOW_SUCCESSFUL_RESULTS_IN_JOBLIST,
			lastNHoursToShowFailedResultsInJoblist: LAST_N_HOURS_TO_SHOW_FAILED_RESULTS_IN_JOBLIST,
			lastNHoursToShowPendingOrRunnngResultsInJoblist: LAST_N_HOURS_TO_SHOW_PENDING_OR_RUNNNG_RESULTS_IN_JOBLIST]
		if (forceShowAllJobs)
			model << ['filters': ['filterInactiveJobs': true]]
		if (request.xhr){
//            render(template: 'jobTable', model: model)
            String templateAsPlainText = g.render(template: 'jobTable', model: model)
            sendSimpleResponseAsStream(response, HttpStatus.OK, templateAsPlainText)
		}else{
            return model
		}
	}

	public Map<String, Object> list() {
		getListModel()
	}

	def index() {
		redirect(action: 'list')
	}

	def create() {
		Job job = new Job(params)
		job.maxDownloadTimeInMinutes = configService.getDefaultMaxDownloadTimeInMinutes()
		return [job: job] << getStaticModelPartForEditOrCreateView()
	}

	def save() {
		Job job = new Job(params)
		setVariablesOnJob(params.variables, job)

        if ((params.executionSchedule != null) && (!(CronExpression.isValidExpression(params.executionSchedule)))) {
            job.errors.reject(
                'job.executionSchedule.executionScheduleInvalid',
                ['', '', params.executionSchedule.substring(params.executionSchedule.indexOf(" ") + 1)] as Object[],
                '[{2} is not a valid Cron expression]')
            render(view: 'create', model: [job: job] << getStaticModelPartForEditOrCreateView())
            return
        } else {

    		if (!job.save(flush: true)) {
    			render(view: 'create', model: [job: job] << getStaticModelPartForEditOrCreateView())
    			return
    		} else {
    			// Tags can only be set after first successful save.
    			// This is why Job needs to be saved again.
    			job.tags = params.list('tags')
    			job.save(flush: true)

    			def flashMessageArgs = [getJobI18n(), job.label]
    			Map<Long, Object> massExecutionResults = [:]
    			massExecutionResults[job.id] = [ status: 'success', message: message(code: 'default.created.message', args: flashMessageArgs)]
    			render(view: 'list', model: getListModel(!job.active) << ['massExecutionResults': massExecutionResults])
    		}
        }
	}

	def edit() {
		Job job = Job.get(params.id)
		redirectIfNotFound(job, params.id)
        return [job: job] << getStaticModelPartForEditOrCreateView()
	}

	def update() {
		Job job = Job.get(params.id)

        if ((params.executionSchedule != null) && (!(CronExpression.isValidExpression(params.executionSchedule)))) {
            job.errors.reject(
                'job.executionSchedule.executionScheduleInvalid',
                ['', '', params.executionSchedule.substring(params.executionSchedule.indexOf(" ") + 1)] as Object[],
                '[{2} is not a valid Cron expression]')
            render(
                    view: 'edit',
                    model: [job: job] << getStaticModelPartForEditOrCreateView()
            )
            return
        } else {
    		def flashMessageArgs = [getJobI18n(), job.label]
    		redirectIfNotFound(job, params.id)

    		if (params.version) {
    			def version = params.version.toLong()
    			if (job.version > version) {
    				job.errors.rejectValue("version", "default.optimistic.locking.failure", [getJobI18n()] as Object[],
    						  "Another user has updated this job while you were editing")
    				render(
                            view: 'edit',
                            model: [job: job] << getStaticModelPartForEditOrCreateView()
                    )
    				return
    			}
    		}

    		job.properties = params
    		setVariablesOnJob(params.variables, job)
    		job.tags = params.list('tags')
    		if (!job.save()) {
    			render(view: 'edit', model: [job: job] << getStaticModelPartForEditOrCreateView())
    			return
    		} else {
    			Map<Long, Object> massExecutionResults = [:]
    			massExecutionResults[job.id] = [ status: 'success', message: message(code: 'default.updated.message', args: flashMessageArgs)]
    			render(view: 'list', model: getListModel(!job.active) << ['massExecutionResults': massExecutionResults])
    		}
        }
	}

    /**
     * Creates a text to represent which data will be gone if the job with the given id will be deleted
     * @param id Job id
     * @return
     */
    def createDeleteConfirmationText(int id){
        Job job1 = Job.get(id)
        def query = JobResult.where {job == job1}
        List<Date> dateList = JobResult.createCriteria().get {
            eq("job",job1)
            projections {
                min "date"
                max "date"
            }
        }
        Date minDate
        Date maxDate
        if(dateList.size()>1){
            minDate = dateList[0]
            maxDate = dateList[1]
        }
        int count = query.count()

        String first = minDate ? "${g.message(code: "de.iteratec.osm.measurement.schedule.JobController.firstResult", default: "Date of first result")}: ${minDate.format('dd.MM.yy')} <br>":""
        String last = maxDate ? "${g.message(code: "de.iteratec.osm.measurement.schedule.JobController.lastResult", default: "Date of last result")}: ${maxDate.format('dd.MM.yy')} <br>" :""
        render("$first$last"+ "${g.message(code: "de.iteratec.osm.measurement.schedule.JobController.resultAmount", default: "Amount of results")}: ${count}")
    }

    def delete() {
        Job job = Job.get(params.id)

        Promise p = task {
			jobService.deleteJob(job)
        }
        p.onComplete {
            log.info("Deletion of Job ${job} completed.")
        }
        redirect(controller: "batchActivity", action: "list", params: [max:10])
    }


    /**
	 * Execute handler for each job selected using the checkboxes
	 * @param handler A closure which gets the corresponding job as first parameter
	 */
	void handleSelectedJobs(Closure handler) {
		Map<Long, Object> massExecutionResults = [:]
		if (params.selected) {
			List<Long> selectedIds = params.selected.findAll { jobId -> jobId.key.isLong() && "on".equals(jobId.value) }
											  .collect { jobId -> jobId.key.toLong() }
			selectedIds.each() {
				Job job = Job.get(it)
				handler(job, massExecutionResults)
			}
			render(view: 'list', model: getListModel(true) << ['selectedIds': selectedIds, 'massExecutionResults': massExecutionResults, filters: params.filters])
		} else {
			redirect(action: 'list', model: [filters: params.filters])
		}
	}

	def execute() {
		if (params.id) {
			Job job = new Job(params)
			setVariablesOnJob(params.variables, job)
			try {
				redirect(url: jobProcessingService.launchJobRunInteractive(job))
			} catch (JobExecutionException e) {
				render e.htmlResult
			}
		} else {
			handleSelectedJobs { Job job, Map<Long, Object> massExecutionResults ->
				if (jobProcessingService.launchJobRun(job))
					massExecutionResults[job.id] = [ status: 'success' ]
				else
					massExecutionResults[job.id] = [ status: 'failure' ]
			}
		}
	}

	def toggleActive(boolean active) {
		handleSelectedJobs { Job job, Map<Long, Object> massExecutionResults ->
			println job.label
			job.active = active
			if (job.save(flush: true)) {
				massExecutionResults[job.id] = [ status: 'success',
												 message: message(code: 'de.iteratec.isj.job.' + (active ? 'activated' : 'deactivated' )) ]
			} else {
				massExecutionResults[job.id] = [ status: 'failure',
												 message: active && job.errors.hasFieldErrors('active') ? message(code: 'de.iteratec.isj.job.cannotActivate') : job.errors.allErrors.toString() ]
			}
		}
	}

	def activate() {
		toggleActive(true)
	}

	def deactivate() {
		toggleActive(false)
	}

	def nextExecution() {
		Map model = [:]
		model['cronstring'] = params.value
		if (!params.noprepend)
			model['prepend'] = message(code: 'job.nextRun.label')
		render(template: 'timeago', model: model)
	}

	def getLastRun(long jobId) {
		Job job = Job.get(jobId)
		render(template: 'timeago', model: ['date': job.lastRun, 'defaultmessage': message(code: 'job.lastRun.label.never', default: 'Noch nie')])
	}

	def getPlaceholdersUsedInScript(long scriptId) {
		Script script = Script.get(scriptId)
		render(contentType: "application/json") {
			PlaceholdersUtility.getPlaceholdersUsedInScript(script)
		}
	}

	def mergeDefinedAndUsedPlaceholders(long jobId, long scriptId) {
		Script script = Script.get(scriptId)
		Job job = Job.get(jobId)
		render(template: 'variables', model: [variables: PlaceholdersUtility.mergeDefinedAndUsedPlaceholders(job, script), script: script])
	}

	def getScriptSource(long scriptId) {
		Script script = Script.get(scriptId)
		sendSimpleResponseAsStream(response, HttpStatus.OK, script?.navigationScript)
	}
	private void sendSimpleResponseAsStream(javax.servlet.http.HttpServletResponse response, HttpStatus httpStatus, String message) {
		response.setContentType('text/plain;charset=UTF-8')
		response.status=httpStatus.value()

		Writer textOut = new OutputStreamWriter(response.getOutputStream())
		textOut.write(message)

		textOut.flush()
		response.getOutputStream().flush()
	}


	def getRunningAndRecentlyFinishedJobs() {
		// the following does not work due to unresolved bug in Grails:
		// http://jira.grails.org/browse/GPCONVERTERS-10

//		render(contentType: "application/json") {
//			use (TimeCategory) {
//				jobProcessingService.getRunningAndRecentlyFinishedJobs(new Date() - 5.days)
//			}
//		}

		// thus this workaround:
		response.setContentType('application/json')
		Map jobs
		use (TimeCategory) {
			jobs = queueAndJobStatusService.getRunningAndRecentlyFinishedJobs(
				new Date() - LAST_N_MINUTES_TO_SHOW_SUCCESSFUL_RESULTS_IN_JOBLIST.minutes,
				new Date() - LAST_N_HOURS_TO_SHOW_FAILED_RESULTS_IN_JOBLIST.hours,
				new Date() - LAST_N_HOURS_TO_SHOW_PENDING_OR_RUNNNG_RESULTS_IN_JOBLIST.hours)
		}
		jobs.each {
			jobId, jobResults ->
				jobResults.each {
					if (it.status < 200)
					it.cancelLinkHtml = groovyPageRenderer.render(template: '/job/cancelLink', model: [jobId: jobId, testId: it.testId])
				}
		}
		if (request.xhr)
			render new JsonBuilder(jobs).toString()
		else
			render new JsonBuilder(jobs).toPrettyString()
	}

	def cancelJobRun(long jobId, String testId) {
		Job job = Job.get(jobId)
		jobProcessingService.cancelJobRun(job, testId)
	}

	/**
	 * List tags starting with term
	 */
	def tags(String term) {
		render Job.findAllTagsWithCriteria([max:5]) { ilike('name', "${term}%") } as JSON
	}

    def activateMeasurementsGenerally(){
        inMemoryConfigService.activateMeasurementsGenerally()
        redirect(action: 'list')
    }

	private void setVariablesOnJob(Map variables, Job job) {
		job.firstViewOnly = !params.repeatedView

		job.variables = [:]
		variables.each {
			if (it.value)
				job.variables[it.key] = it.value
		}
	}

    Map getStaticModelPartForEditOrCreateView(){
        return [
                defaultMaxDownloadTimeInMinutes: configService.getDefaultMaxDownloadTimeInMinutes(),
                connectivites: ConnectivityProfile.list(),
                customConnNameForNative: ConnectivityProfileService.CUSTOM_CONNECTIVITY_NAME_FOR_NATIVE
        ]
    }
}