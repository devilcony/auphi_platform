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

import java.util.List;

import com.auphi.ktrl.schedule.view.FieldMappingView;

public abstract class BaseTemplate4 implements Template {
	
	protected String generateOracelFieldsStr(List<FieldMappingView> fieldMappingList,String enclosedChar) {
		String sourceFields = "";
		for(int i = 0; i<fieldMappingList.size();i++)
		{
			int endIndex = fieldMappingList.get(i).getEndIndex();
			int startIndex = fieldMappingList.get(i).getStartIndex();
			if(startIndex>=0 && endIndex>=0 && (endIndex>startIndex))
			{
				sourceFields+=(" substr( ");
				sourceFields+=(enclosedChar);
				sourceFields+=fieldMappingList.get(i).getSourceColumnName();
				sourceFields+=(enclosedChar);
				
				sourceFields+=",";
				sourceFields+=fieldMappingList.get(i).getStartIndex();
				sourceFields+=",";
				sourceFields+=fieldMappingList.get(i).getEndIndex();
				sourceFields+=(" ) ");
			}
			else	
			{
				sourceFields+=(enclosedChar);
				sourceFields+=fieldMappingList.get(i).getSourceColumnName();
				sourceFields+=(enclosedChar);
			}
			sourceFields+=(" AS ");
			
			sourceFields+=(enclosedChar);
			sourceFields+=fieldMappingList.get(i).getDestColumuName();
			sourceFields+=(enclosedChar);
			
			if(i!=fieldMappingList.size()-1)
				sourceFields+=",";
		}
		return sourceFields;
	}
	
	protected String generateSourceFieldNames(List<FieldMappingView> fieldMappingList,String databaseDesc) {
		
		String str = "";
//		if(databaseDesc.indexOf("HIVE")>-1)
//		{
//			for(int i = 0; i<fieldMappingList.size();i++)
//			{
//				str+="substr(";
//				str+=fieldMappingList.get(i).getSourceColumnName();
//				str+=",0,";
//				str+=fieldMappingList.get(i).getDestLength();
//				str+=")";
//				str+=" as ";
//				str+=fieldMappingList.get(i).getSourceColumnName();
//				if(i!=fieldMappingList.size()-1)
//					str+=",";
//			}
//		}
//		else
		{
			for(int i = 0; i<fieldMappingList.size();i++)
			{
				str+=fieldMappingList.get(i).getSourceColumnName();
				if(i!=fieldMappingList.size()-1)
					str+=",";
			}
		}
		return str;		
	}
	
	protected String generateDestFieldNames(List<FieldMappingView> fieldMappingList) {
		String str = "";
		for(int i = 0; i<fieldMappingList.size();i++)
		{
			str+=fieldMappingList.get(i).getDestColumuName();
			if(i!=fieldMappingList.size()-1)
				str+=",";
		}
		return str;
	}
	
	protected String generateDestFieldTypes(List<FieldMappingView> fieldMappingList) {
		String str = "";
		for(int i = 0; i<fieldMappingList.size();i++)
		{
			str+=fieldMappingList.get(i).getDestColumnType();
			if(i!=fieldMappingList.size()-1)
				str+=",";
		}
		return str;
	}

	protected String generateDestFieldLengths(List<FieldMappingView> fieldMappingList) {
		String str = "";
		for(int i = 0; i<fieldMappingList.size();i++)
		{
			str+=fieldMappingList.get(i).getDestLength();
			if(i!=fieldMappingList.size()-1)
				str+=",";
		}
		return str;
	}

	protected String generateDestFieldScales(List<FieldMappingView> fieldMappingList) {
		String str = "";
		for(int i = 0; i<fieldMappingList.size();i++)
		{
			if(fieldMappingList.get(i).getDestColumnType().contains("Number"))
				str+="5";
			else
				str+="0";
			
			if(i!=fieldMappingList.size()-1)
				str+=",";
		}
		return str;
	}
	
	@Override
	public abstract boolean execute(int monitorId, int execType, String remoteServer, String ha) throws Exception;

	@Override
	public abstract void bind(String fastConfigJson, String fieldMappingJson)  throws Exception;
	
//	private String generateFieldsStr(List<FieldMappingView> fieldMappingList) {
//		String sourceFields = "";
//		for(int i = 0; i<fieldMappingList.size();i++)
//		{
//			sourceFields+=fieldMappingList.get(i).getSourceColumnName();
//			if(i!=fieldMappingList.size()-1)
//				sourceFields+=",";
//		}
//		return sourceFields;
//	}

}
