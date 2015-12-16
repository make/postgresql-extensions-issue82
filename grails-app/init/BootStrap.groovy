import postgresql.extensions.issue82.JobStatus;

class BootStrap {

    def init = { servletContext ->
		log.info 'Checking job statuses'
		JobStatus.withNewSession { session ->
			def statuses = JobStatus.findAll{ running == true }
			statuses*.setRunning(false)
			statuses*.save(flush:true)
			if(statuses)
				log.info "Cleared job statuses"
			else
				log.info 'All job statuses are OK'
		}
    }
    def destroy = {
    }
}
