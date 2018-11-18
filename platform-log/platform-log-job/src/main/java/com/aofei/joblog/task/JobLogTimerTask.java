package com.aofei.joblog.task;

import com.aofei.kettle.JobExecutor;

import java.util.TimerTask;

public class JobLogTimerTask extends TimerTask {

    private JobExecutor JobExecutor;

    public JobLogTimerTask(JobExecutor JobExecutor){
        this.JobExecutor = JobExecutor;
    }

    @Override
    public void run() {
        if(JobExecutor !=null){
            if(JobExecutor.isFinished()){

            }else {

            }
        }
    }
}
