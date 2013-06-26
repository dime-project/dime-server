package eu.dime.ps.communications.notifier;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Job to deal notification to UI
 * 
 * @author mplanaguma
 *
 */
public class InternalNotifyJob extends QuartzJobBean {

	private InternalNotifySchedule internalNotifySchedule;

	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		// do internatNotifySchedule actual work
	    internalNotifySchedule.dealNotifications();
	}

	public void setInternalNotifySchedule(InternalNotifySchedule internalNotifySchedule) {
		this.internalNotifySchedule = internalNotifySchedule;
	}

}
