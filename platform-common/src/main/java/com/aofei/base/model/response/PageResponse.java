package com.aofei.base.model.response;

import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 分页响应
 * @param
 */
@ApiModel(description = "分页对象")
public class PageResponse<T> {


    /*** 总数量 */
    @ApiModelProperty(value = "总数量")
    private Integer total = 0;


    /*** 总页数 */
    @ApiModelProperty(value = "总页数")
    private Integer pages = 0;


    /*** 每页显示数量 */
    //@JSONField(serialize = false)
    @ApiModelProperty(value = "每页显示数量")
    private Integer size = 0;

    /*** 当前页码 */
    //@JSONField(serialize = false)
    @ApiModelProperty(value = "当前页码")
    private Integer pagination = 0;


    /*** 数据结果集 */
    @ApiModelProperty(value = "数据结果集")
    private List<T> result;

    public PageResponse() {
    }

    public PageResponse(Integer totalCount, Integer totalPage, Integer size, Integer pagination, Boolean hasPrev, Boolean hasNext, List<T> result) {
        this.total = totalCount;
        this.pages = totalPage;
        this.size = size;
        this.pagination = pagination;

        this.result = result;
    }

    public PageResponse(Page<T> page) {
        this.setResult(page.getRecords())
                .setPages(page.getPages())
                .setSize(page.getSize())
                .setTotal(page.getTotal())
                .setPagination(page.getCurrent());
    }

    public Integer getTotal() {
        return total;
    }

    public PageResponse setTotal(Integer total) {
        this.total = total;
        return this;
    }

    public Integer getPages() {
        return pages;
    }

    public PageResponse setPages(Integer pages) {
        this.pages = pages;
        return this;
    }

    public Integer getSize() {
        return size;
    }

    public PageResponse setSize(Integer size) {
        this.size = size;
        return this;
    }

    public Integer getPagination() {
        return pagination;
    }

    public PageResponse setPagination(Integer pagination) {
        this.pagination = pagination;
        return this;
    }




    public List<T> getResult() {
        return result;
    }

    public PageResponse setResult(List<T> result) {
        this.result = result;
        return this;
    }

    @Override
    public String toString() {
        return "Page{" +
                "total=" + total +
                ", pages=" + pages +
                ", size=" + size +
                ", pagination=" + pagination +
                ", result=" + result +
                '}';
    }
}
