package com.aofei.base.model.vo;

import com.aofei.base.model.response.IgnoreResponse;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DataGrid的数据模型封装. <br/>
 */
@Getter
@Setter
public class DataGrid<T> extends IgnoreResponse {

	public DataGrid() {
		super();
	}

	public DataGrid(Page<T> page) {
		super();
		this.list = page.getRecords();
		this.currPage = page.getCurrent();
		this.pageSize = page.getSize();
		this.totalCount = page.getTotal();
		this.totalPage = page.getPages();
	}

	/**
	 * 当前页记录数
	 */
	@ApiModelProperty(value = "列表数据")
	private List<T> list;
	/**
	 * 当前页
	 */
	@ApiModelProperty(value = "当前页码")
	private int currPage;

	@ApiModelProperty(value = "总页数")
	private int pageSize;

	@ApiModelProperty(value = "数据总数")
	private int totalCount;

	@ApiModelProperty(value = "数据总页数")
	private int totalPage;




}
