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
package com.auphi.ktrl.quality.compare.controller;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.ktrl.quality.compare.domain.CompareSql;
import com.auphi.ktrl.quality.compare.domain.CompareSqlColumn;
import com.auphi.ktrl.quality.compare.service.CompareSqlColumnService;
import com.auphi.ktrl.quality.compare.service.CompareSqlResultService;
import com.auphi.ktrl.quality.compare.service.CompareSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @auther Tony
 * @create 2017-02-11 10:31
 */

@Controller
@RequestMapping(value = "compareSql")
public class CompareSqlController extends BaseMultiActionController {

    @Autowired
    private CompareSqlResultService mCompareSqlResultService;

    @Autowired
    private CompareSqlColumnService mCompareSqlColumnService;

    @Autowired
    private CompareSqlService mCompareSqlService;


    /**
     * 获取sql字段信息
     * @param resp
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"save", ""})
    public String save(HttpServletResponse resp, CompareSql compareSql) throws IOException {
        try{
            mCompareSqlService.save(compareSql);

        }catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;

    }


    @ResponseBody
    @RequestMapping(value = {"getCompareSql", ""})
    public CompareSql getCompareSql(HttpServletRequest req, HttpServletResponse resp, CompareSql compareSql) throws IOException {
        try{
            compareSql = mCompareSqlService.getCompareSql(compareSql);


            return compareSql;

        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }

    /**
     * 获取sql字段信息
     * @param resp
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"getCompareSqlCombo", ""})
    public String getCompareSqlList(HttpServletResponse resp, CompareSql compareSql) throws IOException {
        try{

            List<CompareSql>  list = mCompareSqlService.findComboList(compareSql);

            PaginationSupport<CompareSql> page = new PaginationSupport<CompareSql>(list, list.size());
            String jsonString = JsonHelper.encodeObject2Json(page);
            write(jsonString, resp);

        }catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;

    }


    @RequestMapping(value = {"delete", ""})
    public String delete(CompareSqlColumn column, String dl, HttpServletResponse resp) throws IOException {
        try{
            List<CompareSqlColumn> columns = mCompareSqlColumnService.findList(column);

            if("t".equals(dl)){// 删除
                Set<CompareSql> tables = new HashSet<>();
                for(CompareSqlColumn ptc:columns ){
                    tables.add(ptc.getCompareSql());
                }
                for (CompareSql sql : tables) {
                    mCompareSqlService.delete(sql);
                }

            }else if("c".equals(dl)){
                for(CompareSqlColumn sqlColumn:columns ){
                    mCompareSqlColumnService.delete(sqlColumn);
                }
            }

            this.setOkTipMsg("操作成功", resp);

        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }


}
