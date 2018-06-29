package com.auphi.ktrl.quality.api;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.ktrl.quality.compare.domain.CompareSql;
import com.auphi.ktrl.quality.compare.domain.CompareSqlResult;
import com.auphi.ktrl.quality.compare.domain.response.CompareSqlResultResponse;
import com.auphi.ktrl.quality.compare.service.CompareSqlResultService;
import com.auphi.ktrl.quality.compare.service.CompareSqlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

/**
 * @auther Tony
 * @create 2018-06-19 15:49
 */
@Api(value = "/api", description = "数据稽核接口API文档")
@RestController
@RequestMapping(value = "/api", produces = {"application/json;charset=UTF-8"})
public class CompareController extends BaseMultiActionController {


    @Autowired
    private CompareSqlService mCompareSqlService;

    @Autowired
    private CompareSqlResultService mCompareSqlResultService;

    /**
     * 获取数据几个比对结果
     * @param id
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "获取数据几个比对结果", notes = "获取数据几个比对结果", httpMethod = "GET", response = CompareSqlResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据稽核ID", paramType = "path", dataType = "Integer"),
    })
    @RequestMapping(value = "/v1/compare/{id}/result.shtml", method = RequestMethod.GET)
    @ResponseBody
    public void getResultById(HttpServletResponse resp,
            @ApiIgnore @PathVariable Integer id ) throws Exception {

        CompareSql compareSql = mCompareSqlService.getCompareSql(new CompareSql(id));
        if(compareSql != null){
            mCompareSqlResultService.execCompareSql(compareSql);

            PaginationSupport<CompareSqlResult> page = mCompareSqlResultService.findPage(new CompareSqlResult(compareSql));
            if(page.getTotal()>0){
                this.setOkTipMsg("success",new CompareSqlResultResponse(page.getRows().get(0)),resp);
            }
        }else{
            this.setFailTipMsg("数据稽核ID不存在！",resp);
        }
    }
}
