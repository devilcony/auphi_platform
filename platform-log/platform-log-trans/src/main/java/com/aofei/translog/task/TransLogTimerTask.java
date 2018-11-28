package com.aofei.translog.task;

import com.alibaba.fastjson.JSON;
import com.aofei.kettle.TransExecutor;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.translog.entity.LogTrans;
import com.aofei.translog.entity.LogTransStep;
import org.pentaho.di.core.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class TransLogTimerTask extends TimerTask {

    private static Logger logger = LoggerFactory.getLogger(TransLogTimerTask.class);

    private TransExecutor transExecutor;
    private boolean first = true;
    private LogTrans logTrans;
    private List<LogTransStep> logTransSteps;
    public TransLogTimerTask(TransExecutor transExecutor,LogTrans logTrans){
        this.transExecutor = transExecutor;
        this.logTrans = logTrans;
    }

    @Override
    public void run() {

        try {

            if(transExecutor!=null){

                if(first){
                    first = false;
                    logTransSteps = new ArrayList<>();
                    logTrans.setTransConfigId(Long.valueOf(transExecutor.getTransMeta().getObjectId().getId()));
                    logTrans.insertAllColumn();

                }else{
                    logTrans.setTransConfigId(Long.valueOf(transExecutor.getTransMeta().getObjectId().getId()));
                    logTrans.setLoginfo(transExecutor.getExecutionLog());
                    logTrans.updateById();
                    JSONArray jsonArray = transExecutor.getStepMeasure();
                    for(int i = 0;i< jsonArray.size();i++ ){
                        JSONArray childArray = (JSONArray) jsonArray.get(i);
                        LogTransStep logTransStep = getLogTransStep(i);
                        for(int j = 0;j< childArray.size();j++){
                            logTransStep.setChannelId(transExecutor.getExecutionId());
                            logTransStep.setTransname(logTrans.getTransname());
                            logTransStep.setLogTransId(logTrans.getLogTransId());
                            logTransStep.setStepname(String.valueOf(childArray.get(0)));
                            logTransStep.setStepCopy(Integer.valueOf(childArray.get(1).toString()));
                            logTransStep.setLinesRead(Long.valueOf(childArray.get(2).toString()));
                            logTransStep.setLinesWritten(Long.valueOf(childArray.get(3).toString()));
                            logTransStep.setLinesInput(Long.valueOf(childArray.get(4).toString()));
                            logTransStep.setLinesOutput(Long.valueOf(childArray.get(5).toString()));
                            logTransStep.setLinesUpdated(Long.valueOf(childArray.get(6).toString()));
                            logTransStep.setLinesRejected(Long.valueOf(childArray.get(7).toString()));
                            logTransStep.setErrors(Long.valueOf(childArray.get(8).toString()));
                            logTransStep.setStatus(childArray.get(9).toString());
                            logTransStep.setCosttime(childArray.get(10).toString());
                            logTransStep.setSpeed(childArray.get(11).toString());

                            //logTransStep.setStepCopy(childArray[1]);
                            logTransStep.insertOrUpdate();
                        }
                    }
                }

                if(transExecutor.isFinished()){
                    logger.info(transExecutor.getStepMeasure().toString());
                    Result result = transExecutor.getTrans().getResult();
                    if(result!=null && result.getNrErrors() == 0){

                        logTrans.setStatus("end");
                        logTrans.setLinesInput(result.getNrLinesInput());
                        logTrans.setLinesOutput(result.getNrLinesOutput());
                        logTrans.setLinesRead(result.getNrLinesRead());
                    }else{
                        logTrans.setStatus("stop");
                    }
                    logTrans.setEnddate(transExecutor.getTrans().getEndDate());
                    logTrans.setErrors(transExecutor.getErrCount());
                    logTrans.setLoginfo(transExecutor.getExecutionLog());
                    logTrans.updateById();
                    cancel();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private LogTransStep getLogTransStep(int i) {
        try {
            return logTransSteps.get(i);

        }catch (Exception e){
            LogTransStep logTransStep = new LogTransStep();
            logTransSteps.add(logTransStep);
            return getLogTransStep(i);

        }
    }
}
