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

import java.io.Serializable;

import com.auphi.ktrl.i18n.Messages;


public class PageInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int PAGESIZE = Integer.parseInt(Constants.get("PageSize", "2"));
	int curPage;
	int maxPage;
	int maxRowCount;
	int rowsPerPage;

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	public int getMaxRowCount() {
		return maxRowCount;
	}

	public void setMaxRowCount(int maxRowCount) {
		this.maxRowCount = maxRowCount;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public PageInfo(int page, int count) {
		rowsPerPage = PAGESIZE;
		curPage = page;
		maxRowCount = count;
		maxPage = ((maxRowCount + rowsPerPage) - 1) / rowsPerPage;
	}

	public String getHtml(String servletURL) {
		return getHtml(servletURL, "listForm");
	}
	
	public String getHtml(String servletURL, String formName)
    {
        StringBuffer sb = new StringBuffer();
        String inlineJS="<script type='text/javascript'>function submitTo(url,formName){var sForm=document.getElementById(formName);sForm.action=url;sForm.submit();}</script>";
        sb.append(inlineJS);
        sb.append("<div style='background:#FFEE99;padding:2px;margin:1px;'>");
        sb.append("&nbsp;&nbsp;&nbsp;<span>"+Messages.getString("PageInfo.Toolbar.total", String.valueOf(maxRowCount),String.valueOf(maxPage),String.valueOf(rowsPerPage),String.valueOf(curPage))+"</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        if(curPage > 1)
        {
            sb.append("<A HREF=\"javascript:submitTo('"+servletURL+"&page=1','"+formName+"');\" >"+Messages.getString("PageInfo.Toolbar.frontpage")+"</A>&nbsp;");
            sb.append("<A HREF=\"javascript:submitTo('"+servletURL+"&page="+(curPage - 1)+"','"+formName+"');\" >"+Messages.getString("PageInfo.Toolbar.prepage")+"</A>&nbsp;");
        } else
        {
            sb.append(Messages.getString("PageInfo.Toolbar.frontpage")+"&nbsp;");
            sb.append(Messages.getString("PageInfo.Toolbar.prepage")+"&nbsp;");
        }
        if(curPage < maxPage)
        {
            sb.append("<A HREF=\"javascript:submitTo('"+servletURL+"&page="+(curPage + 1)+"','"+formName+"');\" >"+Messages.getString("PageInfo.Toolbar.nextpage")+"</A>&nbsp;");
            sb.append("<A HREF=\"javascript:submitTo('"+servletURL+"&page="+maxPage+"','"+formName+"');\" >"+Messages.getString("PageInfo.Toolbar.lastpage")+"</A>&nbsp;");
        } else
        {
            sb.append(Messages.getString("PageInfo.Toolbar.nextpage")+"&nbsp;");
            sb.append(Messages.getString("PageInfo.Toolbar.lastpage")+"&nbsp;");
        }
        sb.append(Messages.getString("PageInfo.Toolbar.direct"));
        sb.append("<select id=\"page\" name=\"page\" class=\"TextStyle\" onchange=\"javascript:submitTo('"+servletURL+"&page='+document.getElementById('page').value,'"+formName+"')\" >");
        sb.append(getPageOptions(maxPage, curPage)+"</select></div>");
        return sb.toString();
    }
	
	private String getPageOptions(int maxPage, int curPage) {
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i <= maxPage; i++)
			sb.append("<option value='" + i + "' " + (i == curPage ? "selected" : "") + "><span  class='fountblack14'>"+Messages.getString("PageInfo.Toolbar.pagenum", String.valueOf(i))+"</span></option>");
		return sb.toString();
	}
}
