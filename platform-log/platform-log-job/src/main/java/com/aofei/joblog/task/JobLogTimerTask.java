package com.aofei.joblog.task;

import com.aofei.joblog.entity.LogJob;
import com.aofei.joblog.entity.LogJobStep;
import com.aofei.kettle.JobExecutor;
import com.aofei.kettle.utils.JSONArray;
import org.pentaho.di.core.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class JobLogTimerTask extends TimerTask {

    private JobExecutor jobExecutor;
    private LogJob logJob;
    private boolean first = true;
    private List<LogJobStep> logJobSteps;

    private static Logger logger = LoggerFactory.getLogger(JobLogTimerTask.class);

    public JobLogTimerTask(JobExecutor jobExecutor, LogJob logJob){

        this.jobExecutor = jobExecutor;
        this.logJob = logJob;
    }

    @Override
    public void run() {

        try {
            Date now =   new Date();
            if(jobExecutor !=null){
                if(first){
                    first = false;
                    logJobSteps = new ArrayList<>();
                    logJob.setJobConfigId(Long.valueOf(jobExecutor.getJob().getObjectId().getId()));

                    logJob.insertAllColumn();

                }else{

                    logJob.setChannelId(jobExecutor.getExecutionId());
                    logJob.setJobLog(jobExecutor.getExecutionLog());
                    logJob.setLogdate(new Date());
                    logJob.updateById();
                    JSONArray jsonArray = jobExecutor.getJobMeasure();
                    for(int i = 0;i< jsonArray.size();i++ ){
                        JSONArray childArray = (JSONArray) jsonArray.get(i);
                        LogJobStep logJobStep = getLogJobStep(i);
                        for(int j = 0;j< childArray.size();j++){
                            logJobStep.setChannelId(jobExecutor.getExecutionId());
                            logJobStep.setJobname(logJob.getJobName());
                            logJobStep.setLogJobId(logJob.getLogJobId());
                            logJobStep.setStepname(String.valueOf(childArray.get(0)));
                            logJobStep.setLinesRead(Long.valueOf(childArray.get(2).toString()));
                            logJobStep.setLinesWritten(Long.valueOf(childArray.get(3).toString()));
                            logJobStep.setLinesInput(Long.valueOf(childArray.get(4).toString()));
                            logJobStep.setLinesOutput(Long.valueOf(childArray.get(5).toString()));
                            logJobStep.setLinesUpdated(Long.valueOf(childArray.get(6).toString()));
                            logJobStep.setLinesRejected(Long.valueOf(childArray.get(7).toString()));
                            logJobStep.setErrors(Long.valueOf(childArray.get(8).toString()));
                            logJobStep.setLogDate(now);

                            logJobStep.insertOrUpdate();
                        }
                    }
                }
                if(jobExecutor.isFinished()){
                    logger.info(jobExecutor.getJob().toString());
                    Result result = jobExecutor.getJob().getResult();
                    if(result!=null && result.getNrErrors() == 0){

                        logJob.setStatus("end");
                        logJob.setLinesInput(result.getNrLinesInput());
                        logJob.setLinesOutput(result.getNrLinesOutput());
                        logJob.setLinesRead(result.getNrLinesRead());
                    }else{
                        logJob.setStatus("stop");
                    }
                    logJob.setEnddate(jobExecutor.getJob().getEndDate());
                    logJob.setErrors(jobExecutor.getErrCount());
                    logJob.setJobLog(jobExecutor.getExecutionLog());
                    logJob.setLogdate(now);
                    logJob.updateById();
                    cancel();
                }
            }

        }catch (Exception e){
            logger.error(e.getMessage());
        }


    }

    private LogJobStep getLogJobStep(int i) {
        try {
            return logJobSteps.get(i);

        }catch (Exception e){
            LogJobStep logJobStep = new LogJobStep();
            logJobSteps.add(logJobStep);
            return getLogJobStep(i);

        }
    }
}
