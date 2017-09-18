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
package com.auphi.ktrl.util;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class
 * @author 
 *
 */
public class StringUtil
{
	
	private static Logger logger = Logger.getLogger(StringUtil.class);

    /**
     * null to ""
     * @param str
     * @return
     */
    public static String nullToSpace(String str)
    {

        if (str == null)
        {
            return "";
        }
        else
        {
            return str;
        }
    }

    /**
     * "" to " "
     * @param str
     * @return
     */
    public static String spaceToBlank(String str)
    {

        if ("".equals(nullToSpace(str)))
        {
            return " ";
        }
        else
        {
            return str;
        }
    }

    /**
     * list to string，add seprate
     * @param strList
     * @param sep
     * @return
     */
    public static String ListToString(String[] strList, String sep)
    {

        if (strList == null)
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strList.length; i++)
        {
            if (i == 0)
            {
                sb.append(strList[i]);
            }
            else
            {
                sb.append(sep + strList[i]);
            }
        }
        return sb.toString();

    }
    
    /**
     * list to string，add seprate
     * @param strList
     * @param sep
     * @return
     */
    public static String ListToString(List<String> strList, String sep)
    {

        if (strList == null)
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strList.size(); i++)
        {
            if (i == 0)
            {
                sb.append(strList.get(i));
            }
            else
            {
                sb.append(sep + strList.get(i));
            }
        }
        return sb.toString();

    }

    /**
     * array to string ,add '' around the string for hsql
     * @param strList
     * @param sep
     * @return
     */
    public static String ListToStringAdd(String[] strList, String sep)
    {

        if (strList == null)
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strList.length; i++)
        {
            if (i == 0)
            {
                sb.append("'" + strList[i] + "'");
            }
            else
            {
                sb.append(sep + "'" + strList[i] + "'");
            }
        }
        return sb.toString();
    }

    /**
     * list to string ,add '' around the string for hsql
     * @param list list
     * @param sep 
     * @param name 
     * @return
     */
    public static String ListToStringAdd(List<?> list, String sep, String name)
    {
        if (list == null)
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++)
        {
            Map<?, ?> map = (Map<?, ?>) list.get(i);
            String s = map.get(name) == null ? "" : map.get(name).toString();
            String[] ss = s.split(sep);
            for (int j = 0; j < ss.length; j++)
            {
                if ("".equals(sb.toString()))
                {
                    sb.append("'" + ss[j] + "'");
                }
                else
                {
                    sb.append(sep + "'" + ss[j] + "'");
                }
            }
        }
        if ("".equals(sb.toString()))
        {
            sb.append("''");
        }
        return sb.toString();
    }

    /**
     * string to date
     * @param str
     * @param format
     * @return
     */
    public static Date StringToDate(String str, String format)
    {
    	if(str != null && !"".equals(str)){
    		SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = null;
            try
            {
                date = sdf.parse(str);
            }
            catch (ParseException e)
            {
                logger.error(e.getMessage(),e);
            }
            return date;
    	}else {
    		return null;
    	}
    }

    /**
     * date to string 
     * @param date
     * @param format
     * @return
     */
    public static String DateToString(Date date, String format)
    {
    	String s = "";
    	if(date != null){
    		SimpleDateFormat sdf = new SimpleDateFormat(format);
            s = sdf.format(date);
    	}
    	
    	return s;
    }

    /**
     * count the days between two days
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getBetweenDays(Date startDate, Date endDate)
    {

        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        startCal.setTime(startDate);
        endCal.setTime(endDate);

        //if(startCal.after(endCal)) {
        //	startCal.setTime(endDate);
        //	endCal.setTime(startDate);
        //}

        int betweenYears = endCal.get(Calendar.YEAR)
            - startCal.get(Calendar.YEAR);
        int betweenDays = endCal.get(Calendar.DAY_OF_YEAR)
            - startCal.get(Calendar.DAY_OF_YEAR);

        for (int i = 0; i < betweenYears; i++)
        {
            startCal.set(Calendar.YEAR, (startCal.get(Calendar.YEAR) + 1)); //X
            betweenDays += startCal.getActualMaximum(Calendar.DAY_OF_YEAR);
        }

        return betweenDays;

    }

    /**
     * get the date n days later
     * @param date
     * @return
     */
    public static Date getNextDate(Date date, int n)
    {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, day + n);
        return cal.getTime();

    }

    /**
     * get the date n days before
     * @param date
     * @return
     */
    public static Date getBeforeDate(Date date, int n)
    {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, day - n);
        return cal.getTime();

    }

    public static List<Date> getDateList(String start, String end)
    {
        List<Date> returnList = new ArrayList<Date>();

        Date startDate = StringToDate(start, "yyyy-MM-dd");
        Date endDate = StringToDate(end, "yyyy-MM-dd");

        for (Date s = startDate; s.before(endDate); s = getNextDate(s, 1))
        {
            returnList.add(s);
        }
        return returnList;
    }

    public static Date getEndOfMonth(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        cal.roll(Calendar.DATE, -1);

        return cal.getTime();
    }

    /**
     * get the day to remind or not
     * @param date the day to check
     * @param i remind i days ago
     * @return
     */
    public static boolean isRemind(Date date, int i)
    {
        boolean b = false;

        date = getBeforeDate(date, i);

        if (date.compareTo(new Date()) >= 0)
        {
            b = true;
        }

        return b;
    }

    /**
     * weekend or not
     * @param date
     * @return
     */
    public static boolean isWeekEnd(Date date)
    {
        boolean b = false;

        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int dayOfWeek = ca.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == 1 || dayOfWeek == 7)
        {
            b = true;
        }

        return b;
    }

    /**
     * generate string using 0 to 9
     * @param length length of the string
     * @return
     */
    public static String createNumberString(int length)
    {
        final String numberChar = "0123456789";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++)
        {
            sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
        }
        return sb.toString();
    }

    /**
     * format string to array
     * @param s 
     * @param splitechars 
     * @return 
     */
    public static String[] splite(String s, String[] splitechars)
    {
        for (String ch : splitechars)
        {
            if (s.indexOf(ch) > 0 && !" ".equals(ch))
            {
                s.replaceAll(" ", "");
            }
            s = s.replaceAll(ch, "@");
        }
        String r[] = s.split("@");

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < r.length; i++)
        {
            if (!"".equals(r[i]))
            {
                list.add(r[i]);
            }
        }
        String[] returnS = new String[list.size()];
        int i = 0;
        for (String o : list)
        {
            returnS[i++] = o;
        }
        return returnS;
    }

    /**
     * ip between ips or not
     * @param ipSection ips seperate with '-'
     * @param ip 
     * @return
     */
    public static boolean ipIsValid(String ipSection, String ip)
    {
        if (ipSection == null)
            throw new NullPointerException("IP\u6bb5\u4e0d\u80fd\u4e3a\u7a7a\uff01");
        if (ip == null)
            throw new NullPointerException("IP\u4e0d\u80fd\u4e3a\u7a7a\uff01");
        ipSection = ipSection.trim();
        ip = ip.trim();
        final String REGX_IP = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
        final String REGX_IPB = REGX_IP + "\\-" + REGX_IP;
        if (!ipSection.matches(REGX_IPB) || !ip.matches(REGX_IP))
            return false;
        int idx = ipSection.indexOf('-');
        String[] sips = ipSection.substring(0, idx).split("\\.");
        String[] sipe = ipSection.substring(idx + 1).split("\\.");
        String[] sipt = ip.split("\\.");
        long ips = 0L, ipe = 0L, ipt = 0L;
        for (int i = 0; i < 4; ++i)
        {
            ips = ips << 8 | Integer.parseInt(sips[i]);
            ipe = ipe << 8 | Integer.parseInt(sipe[i]);
            ipt = ipt << 8 | Integer.parseInt(sipt[i]);
        }
        if (ips > ipe)
        {
            long t = ips;
            ips = ipe;
            ipe = t;
        }
        return ips <= ipt && ipt <= ipe;
    }

    public static String changeNumToDate(String s)
    {
        String rtn = "1900-01-01";
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date1 = new java.util.Date();
            date1 = format.parse("1900-01-01");
            long i1 = date1.getTime();

            //to subtract 2 (Long.parseLong(s)-2) maybe excel makes it 
            //java strats 1970-01-01
            //excle start at 1900-01-01
            i1 = i1 / 1000 + ((Long.parseLong(s) - 2) * 24 * 3600);
            date1.setTime(i1 * 1000);
            rtn = format.format(date1);
        }
        catch (Exception e)
        {
            rtn = "1900-01-01";
        }
        return rtn;

    }
    
    public static String toUNICODE(String s)
    {
        if(null==s)
            return null;
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<s.length();i++)
        {
            if(s.charAt(i)<=256)
            {
                sb.append("\\u00");
            }
            else
            {
                sb.append("\\u");
            }
            sb.append(Integer.toHexString(s.charAt(i)));
        }
        return sb.toString();
    }

    
    public static String UnicodeToString(String str)
    {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find())
        {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    
    /**
     * format datetime from long to string
     * @param datetime
     * @return
     */
    public static String getDateTime(String datetime){
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(Long.parseLong(datetime));
        
        return StringUtil.DateToString(ca.getTime(), "yyyy-MM-dd");
    }
    
    /**
     * determine weather the string composed by numeric
     * @param str
     * @return
     */
    public static boolean isNumeric(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches() || "".equals(str))
        {
            return false;
        }
        return true;
    }

    public static boolean isDouble(String str) {
        try
        {
            Double.parseDouble(str);
            return true;
        }
        catch(NumberFormatException ex){}
        return false;
    }
}
