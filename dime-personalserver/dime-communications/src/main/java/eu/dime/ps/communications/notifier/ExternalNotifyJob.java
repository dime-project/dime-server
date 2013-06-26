package eu.dime.ps.communications.notifier;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Deals notifications betwenn PS - PS
 * 
 * @author mplanaguma
 *
 */
public class ExternalNotifyJob extends QuartzJobBean {

    private ExternalNotifySchedule externalNotifySchedule;

    protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
	// do the actual work
	externalNotifySchedule.dealNotifications();
    }

    public void setExternalNotifySchedule(ExternalNotifySchedule externalNotifySchedule) {
	this.externalNotifySchedule = externalNotifySchedule;
    }

}
