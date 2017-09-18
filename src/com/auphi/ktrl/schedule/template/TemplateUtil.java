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
package com.auphi.ktrl.schedule.template;

import org.pentaho.di.core.row.ValueMetaInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TemplateUtil {
	
	/**
	 * replace variables in a String
	 * now only support one variable in a String!
	 * @param str:   something like  tablename_${YYYYMM}
	 * @return
	 * @throws Exception
	 */
	public static String replaceVariable(String str, Date date, boolean reload) throws Exception
	{
		StringBuffer result = new StringBuffer();
		int startPosition=0;
		while(str.indexOf("${",startPosition)>-1 && str.indexOf("}",startPosition)>-1)
		{
			String first = str.substring(startPosition,str.indexOf("${",startPosition));
			int endPosition = str.indexOf("}",startPosition)+1;
			String variable = str.substring(str.indexOf("${",startPosition),endPosition);
			result.append(first).append(replaceTemplate(variable, date, reload));
			startPosition = endPosition;
		}
		result.append(str.substring(startPosition,str.length()));
		return result.toString();
	}
	
//	public static String replaceVariable(String str, Date date, boolean reload) throws Exception
//	{
//		if(str.indexOf("${")>-1 && str.indexOf("}")>-1)
//		{
//			String first = str.substring(0,str.indexOf("${"));
//			String variable = str.substring(str.indexOf("${"),str.indexOf("}")+1);
//			String third = str.substring(str.indexOf("}")+1,str.length());
//			return first + replaceTemplate(variable, date, reload)+third;
//		}
//		else
//			return str;
//	}

	/**
	 * 
	 * @param dateFormatTemplate
	 * ${YYYYMM+N}
	 * ${YYYYMMDD+N}
	 * ${YYYYMMDDHH+N}
	 * ${YYYYMMDDHHmm+N}
	 * @return
	 */
	private static String replaceTemplate(String dateFormatTemplate, Date date, boolean reload) throws Exception
	{
		String result;
		if(dateFormatTemplate.startsWith("${")&&dateFormatTemplate.endsWith("}"))
		{
			String templateContent = dateFormatTemplate.substring(2,dateFormatTemplate.length()-1);
			String firstPart;
			String secondPart;
			if(templateContent.indexOf("+")>0)
			{
				firstPart = templateContent.substring(0,templateContent.indexOf("+"));
				secondPart = templateContent.substring(templateContent.indexOf("+")+1,templateContent.length());				
				
			}else if(templateContent.indexOf("-")>0)
			{
				firstPart = templateContent.substring(0,templateContent.indexOf("-"));
				secondPart = templateContent.substring(templateContent.indexOf("-"),templateContent.length());			
			}else
			{
				firstPart = templateContent;
				secondPart = "0";		
			}
			
			if(reload){
				secondPart = "0";
			}
			
			int gap;
			try{
				gap = Integer.parseInt(secondPart);
			}catch (Exception e)
			{
				throw new Exception("Illegal Date Template"+dateFormatTemplate);
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			if(firstPart.equals("YYYYMM"))
			{
				calendar.add(Calendar.MONTH, gap);
				result = format.format(calendar.getTime()).substring(0,6);
			}
			else if(firstPart.equals("YYYYMMDD"))
			{
				calendar.add(Calendar.DAY_OF_MONTH, gap);
				result = format.format(calendar.getTime()).substring(0,8);
			}else if(firstPart.equals("YYYYMMDDHH"))
			{
				calendar.add(Calendar.HOUR_OF_DAY, gap);
				result = format.format(calendar.getTime()).substring(0,10);
			}
			else if(firstPart.equals("YYYYMMDDHHmm"))
			{
				calendar.add(Calendar.MINUTE, gap);
				result = format.format(calendar.getTime()).substring(0,12);
			}else
			{
				throw new Exception("Illegal Date Template: "+dateFormatTemplate);
			}
			
			
		}
		else
			throw new Exception("illegal format:"+dateFormatTemplate+"Date tamplate should starts with '${' and end with '}'");
		return result;		
	}
	
	   /**
     * This method assumes that Hive has no concept of primary 
     * and technical keys and auto increment columns.  We are 
     * ignoring the tk, pk and useAutoinc parameters.
     */
    public static String getFieldDefinition(ValueMetaInterface v) {       
        String retval="";        
        int    length    = v.getLength();
        int    precision = v.getPrecision();        
        int    type      = v.getType();
        switch(type) {        
            case ValueMetaInterface.TYPE_BOOLEAN:
                retval+="BOOLEAN";
                break;        
            //  Hive does not support DATE
            case ValueMetaInterface.TYPE_DATE:
                retval+="STRING";
                break;                
            case  ValueMetaInterface.TYPE_STRING:
                retval+="STRING";
                break;           
            case ValueMetaInterface.TYPE_NUMBER    :
            case ValueMetaInterface.TYPE_INTEGER   : 
            case ValueMetaInterface.TYPE_BIGNUMBER : 
                // Integer values...
                if (precision==0) {
                    if (length>9) {
                        if (length<19) {
                            // can hold signed values between -9223372036854775808 and 9223372036854775807
                            // 18 significant digits
                            retval+="BIGINT";
                        }
                        else {
                            retval+="FLOAT";
                        }
                    }
                    else {
                        retval+="INT";
                    }
                }
                // Floating point values...
                else {  
                    if (length>15) {
                        retval+="FLOAT";
                    }
                    else {
                        // A double-precision floating-point number is accurate to approximately 15 decimal places.
                        // http://mysql.mirrors-r-us.net/doc/refman/5.1/en/numeric-type-overview.html 
                            retval+="DOUBLE";
                    }
                }                   
                break;
            }

        return retval;
    }
	
	public static void main(String[] args)
	{
		try{
//			System.out.println(TemplateUtil.replaceTemplate("${YYYYMM}", new Date(), false));
//			System.out.println(TemplateUtil.replaceTemplate("${YYYYMMDD}", new Date(), false));
//			System.out.println(TemplateUtil.replaceTemplate("${YYYYMMDDHH}", new Date(), false));
//			System.out.println(TemplateUtil.replaceTemplate("${YYYYMMDDHHmm}", new Date(), false));
//			System.out.println(TemplateUtil.replaceTemplate("${YYYYMM-3}", new Date(), false));
//			System.out.println(TemplateUtil.replaceTemplate("${YYYYMMDD-3}", new Date(), false));
//			System.out.println(TemplateUtil.replaceTemplate("${YYYYMMDDHH-3}", new Date(), false));
//			System.out.println(TemplateUtil.replaceTemplate("${YYYYMMDDHHmm-3}", new Date(), false));
//			System.out.println(TemplateUtil.replaceTemplate("${YYYYMM+3}", new Date(), false));
//			System.out.println(TemplateUtil.replaceTemplate("${YYYYMMDD+3}", new Date(), false));
//			System.out.println(TemplateUtil.replaceTemplate("${YYYYMMDDHH+3}", new Date(), false));
//			System.out.println(TemplateUtil.replaceTemplate("${YYYYMMDDHHmm+3}", new Date(), false));
//			
//			System.out.println(TemplateUtil.replaceVariable("${YYYYMMDD+3}_table", new Date(), false));
//			System.out.println(TemplateUtil.replaceVariable("table_${YYYYMMDDHH+3}", new Date(), false));
//			System.out.println(TemplateUtil.replaceVariable("table_${YYYYMMDDHHmm+3}_table", new Date(), false));
			
			System.out.println(TemplateUtil.replaceVariable("f> ${YYYYMMDD+3} and f < ${YYYYMMDDHHmm+3}", new Date(), false));
			System.out.println(TemplateUtil.replaceVariable("f> ${YYYYMMDD+3} and f < ${YYYYMMDDHHmm+3} and 1", new Date(), false));
			System.out.println(TemplateUtil.replaceVariable("${YYYYMMDD+3}>1 and f < ${YYYYMMDDHHmm+3} and 1", new Date(), false));
			System.out.println(TemplateUtil.replaceVariable("${YYYYMMDD+3}>1 and f < ${YYYYMMDDHHmm+3}", new Date(), false));
			System.out.println(TemplateUtil.replaceVariable("f> ${YYYYMMDD+3}>1 and f < ${YYYYMMDDHHmm+3} and k> ${YYYYMMDD+3}", new Date(), false));
			System.out.println(TemplateUtil.replaceVariable("f> ${YYYYMMDD+3}>1 and f < ${YYYYMMDDHHmm+3} and k> ${YYYYMMDD+3} and 1", new Date(), false));
			
			
//			Date date = StringUtil.StringToDate("2014-11-15 15:05", "yyyy-MM-dd hh:mm");
//			System.out.println("------------------not reload------------------");
//			System.out.println(TemplateUtil.replaceVariable("table_${YYYYMMDDHHmm+3}_table", date, false));
//			System.out.println("------------------reload------------------");
			System.out.println(TemplateUtil.replaceVariable("table_${YYYYMMDDHHmm}_table", new Date(), true));
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
