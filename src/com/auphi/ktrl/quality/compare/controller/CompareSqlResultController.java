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
import com.auphi.ktrl.quality.compare.domain.CompareSqlResult;
import com.auphi.ktrl.quality.compare.service.CompareSqlColumnService;
import com.auphi.ktrl.quality.compare.service.CompareSqlResultService;
import com.auphi.ktrl.quality.compare.service.CompareSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @auther Tony
 * @create 2017-02-11 10:32
 */
@ApiIgnore
@Controller
@RequestMapping(value = "compareSqlResult")
public class CompareSqlResultController extends BaseMultiActionController {


    @Autowired
    private CompareSqlResultService mCompareSqlResultService;

    @Autowired
    private CompareSqlColumnService mCompareSqlColumnService;

    @Autowired
    private CompareSqlService mCompareSqlService;

    private final static String INDEX = "admin/compare/compareSqlResult";


    @RequestMapping(value = {"index", ""})
    public ModelAndView index(HttpServletRequest req, HttpServletResponse resp){
        return new ModelAndView(INDEX);
    }


    /**
     * 执行数据统计
     * @param resp
     * @param renovate
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"listResult", ""})
    public String listResult(HttpServletResponse resp, CompareSqlResult sqlResult, boolean renovate) throws IOException {

        try{

            if(renovate){
                Date date = new Date();
                List<CompareSql> compareSqls =  mCompareSqlService.findListByCompareSqlResult(sqlResult);
                for(CompareSql compareSql:compareSqls){
                    mCompareSqlResultService.execCompareSql(compareSql);
                }
            }
            PaginationSupport<CompareSqlResult> page = mCompareSqlResultService.findPage(sqlResult);
            String jsonString = JsonHelper.encodeObject2Json(page);
            write(jsonString, resp);

        }catch(Exception e){

            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }





}
