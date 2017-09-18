/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2017 by Auphi BI : http://www.doetl.com 

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
package com.auphi.data.hub.core.util;

import java.util.Calendar;

public class CronExpressionHepler {

	
	/**
	 * 如果选在按每周运行，月的表达式为*，天的表达式为?，小时的表达式是 
	 * 如果选择按每月运行，天的表达式为
	 * 选择的天，周的表达式为?
	 * 如果选择的是按照每天运行，月的表达式为 * ，周的表达式为? 
	 * 如果选择的时间间隔是按照分钟，分钟的表达式 如果是按照每个多少分钟执行一次，表达式：
	 * 
	 * @param second
	 * @param minute
	 * @param hour
	 * @param day
	 * @param month
	 * @param week
	 * @return
	 */
	public static String generalCron(int execType, String second, String minute,
			String hour, String day, String month, String week) {

		String secondCron = "";
		String minuteCron = "";
		String hourCron = "";
		String monthCron = "";
		String dayCron = "";
		String weekCron = "";
		int se = 0;
		switch (execType) {
		case 1:
			// 如果是按照每多少秒执行一次
			secondCron = "0/" + second;
			weekCron = "?";
			dayCron = "*";
			monthCron = "*";
			minuteCron = "0/1";
			hourCron = "*";
			break;
		case 2:
			// 如果是按照每多少分钟执行一次
			minuteCron = "0/" + minute;
			//se = Integer.parseInt(minute) * 59;
			//secondCron = se + "";
			secondCron = "1";
			hourCron = "*";
			dayCron = "*";
			monthCron = "*";
			weekCron = "?";
			break;
		case 3:
			// 如果是按照每天运行
			minuteCron = minute;
			secondCron = "1";
			weekCron = "?";
			dayCron = "*";
			monthCron = "*";
			hourCron = hour;
			break;
		case 4:
			// 如果是按照每月运行
			minuteCron = minute;
			secondCron = "1";
			weekCron = "?";
			dayCron = day;
			monthCron = "*";
			hourCron = hour;
			break;
		case 5:
			// 如果选在按每周运行
			weekCron = week;
			dayCron = "?";
			monthCron = "*";
			minuteCron = minute;
			secondCron = "01" ;
			hourCron = hour;
			break;
		}
		String cron = secondCron + " " + minuteCron + " " + hourCron + " "
				+ dayCron + " " + monthCron + " " + weekCron;
		System.out.println(cron);
		return cron;

	}
	
	
	
	public static String generalCron_(int execType, String second, String minute,
			String hour, String day, String month, String week) {

		String secondCron = "";
		String minuteCron = "";
		String hourCron = "";
		String dayCron = "";
		String monthCron = "";
		String weekCron = "";
		int se = 0;
		switch (execType) {
		case 1:
			// 如果是按照每多少秒执行一次
//			secondCron = "0/" + second;
			secondCron = second;
			minuteCron = "*";
			hourCron = "*";
			dayCron = "*";
			monthCron = "*";
			weekCron = "?";
			break;
		case 2:
			// 如果是按照每多少分钟执行一次
			minuteCron = "0/" + minute;
			secondCron = second;
			hourCron = "*";
			dayCron = "*";
			monthCron = "*";
			weekCron = "?";
			break;
		case 3:
			// 如果是按照小时运行
			hourCron = hour;
			secondCron = second;
			minuteCron = minute;
			dayCron = "*";
			monthCron = "*";
			weekCron = "?";
			break;
		case 4:
			// 如果是按照天运行
			secondCron = second;
			minuteCron = minute;
			hourCron = hour;
			dayCron = day;
			weekCron = "?";
			monthCron = "*";
			break;
		case 5:
			// 如果是按照月份运行
			minuteCron = minute;
			secondCron = second;
			weekCron = "?";
			dayCron = day;
			monthCron = month;
			hourCron = hour;
			break;
		case 6:
			// 如果选在按每周运行
			weekCron = week;
			dayCron = "?";
			monthCron = "*";
			minuteCron = minute;
			secondCron = second;
			hourCron = hour;
			break;
		}
		String cron = secondCron + " " + minuteCron + " " + hourCron + " "
				+ dayCron + " " + monthCron + " " + weekCron;
		System.out.println(cron);
		return cron;

	}
	
	/**
	 * 根据小时，生成从当前时间开始每隔多少小时执行一次任务的表达式
	 * @param hour
	 * @return
	 */
	public static String generalCronByHour(String hour){
		Calendar now = Calendar.getInstance(); 
		int curHour = now.get(Calendar.HOUR_OF_DAY);
		int curMinute = now.get(Calendar.MINUTE);
		int curSecond = now.get(Calendar.SECOND);
		String cron = curSecond + " " + curMinute + " " + curHour +"/"+ hour +" * * ?";
		return cron;
	}
}
