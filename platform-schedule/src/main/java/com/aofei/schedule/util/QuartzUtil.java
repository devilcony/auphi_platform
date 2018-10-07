/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2018 by Auphi BI : http://www.doetl.com

 * Support：support@pentahochina.com
 *
 *******************************************************************************
 *
 * Licensed under the LGPL License, Version 3.0 the "License";
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/LGPL-3.0 

 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package com.aofei.schedule.util;

import com.aofei.base.exception.ApplicationException;
import com.aofei.schedule.model.request.GeneralScheduleRequest;
import com.aofei.utils.DateUtils;
import com.aofei.utils.StringUtils;
import org.quartz.*;

import java.util.Calendar;
import java.util.Date;

public class QuartzUtil {

    public static final int MODE_ONCE = 1;
    public static final int MODE_SECOND = 2;
    public static final int MODE_MINUTE = 3;
    public static final int MODE_HOUR = 4;
    public static final int MODE_DAY = 5;
    public static final int MODE_WEEK = 6;
    public static final int MODE_MONTH = 7;
    public static final int MODE_YEAR = 8;

    public static Trigger getTrigger(GeneralScheduleRequest request, String group){
        String cronString = "";
        Date satrtDate = getSatrtDate(request);
        Date endDate = getEndDate(request);
        String name = request.getJobName();

        Calendar ca = Calendar.getInstance();
        ca.setTime(satrtDate);
        Trigger trigger;

        switch (request.getCycle()) {
            case MODE_ONCE:
                long repeatInterval = ca.getTime().getTime() - new Date().getTime();
                if(repeatInterval>0){
                    // 创建一个触发器
                     trigger = TriggerBuilder.newTrigger()
                            .withIdentity(name, group)
                            .startAt(satrtDate)
                            .endAt(endDate)
                            .withSchedule(SimpleScheduleBuilder
                                    .simpleSchedule()
                                    .withIntervalInMilliseconds(repeatInterval)//每隔一秒执行一次
                                    //重复执行的次数，因为加入任务的时候马上执行了，所以不需要重复，否则会多一次。
                                    .withRepeatCount(0))
                            .build();
                    return trigger;
                }else{

                }

                break;
            case MODE_SECOND:
                // 创建一个触发器
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(name, group)
                        .startAt(satrtDate)
                        .endAt(endDate)
                        .withSchedule(SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInSeconds(Integer.parseInt(request.getCycleNum()))
                                //重复执行的次数，因为加入任务的时候马上执行了，所以不需要重复，否则会多一次。
                                .withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY))
                        .build();
                return trigger;

            case MODE_MINUTE:

                // 创建一个触发器
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(name, group)
                        .startAt(satrtDate)
                        .endAt(endDate)
                        .withSchedule(SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInMinutes(Integer.valueOf(request.getCycleNum()))//每隔一秒执行一次
                                //重复执行的次数，因为加入任务的时候马上执行了，所以不需要重复，否则会多一次。
                                .withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY))
                        .build();
                return trigger;
            case MODE_HOUR:

                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(name, group)
                        .startAt(satrtDate)
                        .endAt(endDate)
                        .withSchedule(SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInHours(Integer.valueOf(request.getCycleNum()))//每隔一秒执行一次
                                //重复执行的次数，因为加入任务的时候马上执行了，所以不需要重复，否则会多一次。
                                .withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY))
                        .build();

                return trigger;
            case MODE_DAY:


                if(1 == request.getDayType()){
                    cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) +
                            " " + ca.get(Calendar.HOUR_OF_DAY) + " ? * MON-FRI";
                }else if(2 == request.getDayType()){
                    cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) +
                            " " + ca.get(Calendar.HOUR_OF_DAY) + " ? * *";
                }else{
                    throw new ApplicationException();
                }

                break;
            case MODE_WEEK:
                //create cron string
                cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) +
                        " " + ca.get(Calendar.HOUR_OF_DAY) + " ? * " + request.getCycleNum();

                break;
            case MODE_MONTH:
                if(1 == request.getMonthType()){
                    cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) +
                            " " + ca.get(Calendar.HOUR_OF_DAY) + " " + request.getCycleNum() + " * ?";
                }else if(2 == request.getMonthType()){
                    String weeknum = StringUtils.defaultString(request.getWeekNum()) ;
                    String daynum = StringUtils.defaultString(request.getDayNum());

                    if(!"L".equals(weeknum)){
                        weeknum = "#" + weeknum;
                    }

                    cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) +
                            " " + ca.get(Calendar.HOUR_OF_DAY) + " ? * " + daynum + weeknum;
                }


                break;
            case MODE_YEAR:

                if(1 == request.getYearType()){//month and day
                    String[] monthAndDay = request.getCycleNum().split("-");
                    cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) +
                            " " + ca.get(Calendar.HOUR_OF_DAY) + " " + monthAndDay[1] + " " + monthAndDay[0] + " ?";
                }else if(2 == request.getYearType()){//month week and day
                    String monthnum = StringUtils.defaultString(request.getMonthNum()) ;
                    String weeknum = StringUtils.defaultString(request.getWeekNum()) ;
                    String daynum = StringUtils.defaultString(request.getDayNum());

                    if(!"L".equals(weeknum)){
                        weeknum = "#" + weeknum;
                    }

                    cronString = ca.get(Calendar.SECOND) + " " + ca.get(Calendar.MINUTE) +
                            " " + ca.get(Calendar.HOUR_OF_DAY) + " ? " + monthnum + " " + daynum + weeknum;
                }

                break;
        }
        trigger = TriggerBuilder.newTrigger()
                .withIdentity(name, group)
                .startAt(satrtDate)
                .endAt(endDate)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronString))
                .build();
        return trigger;

    }

    private static Date getEndDate(GeneralScheduleRequest request) {
        return StringUtils.isEmpty(request.getEndDate()) ? null: DateUtils.format(request.getEndDate()+" 23:59:59");
    }

    private static Date getSatrtDate(GeneralScheduleRequest request){
        if(!StringUtils.isEmpty(request.getStartDate()) && !StringUtils.isEmpty(request.getStartTime())){
          return   DateUtils.format(request.getStartDate() +" "+request.getStartTime());
        }

        return null;
    }

}
