package com.aofei.schedule.mapper;


import com.aofei.base.annotation.MyBatisMapper;
import com.aofei.base.mapper.BaseMapper;
import com.aofei.schedule.entity.Monitor;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;
import java.util.Map;

@MyBatisMapper
public interface MonitorMapper extends BaseMapper<Monitor> {
    /**
     * 作业耗时前五
     *
     * @param page
     * @param organizerId
     * @return
     */
    List<Map<String, Object>> getTimeConsumingTop5(Page page, Long organizerId);

    /**
     * 最近七天的错误数和完成数
     * @param organizerId
     * @return
     */
    List<Map<String, Object>> get7DayErrorsAndFinishs(Long organizerId);

    /**
     * 正在运行的作业数量
     * @param organizerId
     * @return
     */
    int countRuning(Long organizerId);

    /**
     * 运行完成作业
     * @param organizerId
     * @return
     */
    int countFinish(Long organizerId);
    /**
     * 错误总数
     * @param organizerId
     * @return
     */
    int countError(Long organizerId);
}
