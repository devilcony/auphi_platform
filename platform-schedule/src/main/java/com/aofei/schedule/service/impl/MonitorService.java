package com.aofei.schedule.service.impl;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.service.impl.BaseService;
import com.aofei.kettle.App;
import com.aofei.schedule.entity.Monitor;
import com.aofei.schedule.mapper.MonitorMapper;
import com.aofei.schedule.model.request.MonitorRequest;
import com.aofei.schedule.model.response.DashboardResponse;
import com.aofei.schedule.model.response.MonitorResponse;
import com.aofei.schedule.model.response.RunCountResponse;
import com.aofei.schedule.model.response.RunTimesResponse;
import com.aofei.schedule.service.IMonitorService;
import com.baomidou.mybatisplus.plugins.Page;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MonitorService extends BaseService<MonitorMapper, Monitor> implements IMonitorService {

    @Override
    public Page<MonitorResponse> getPage(Page<Monitor> page, MonitorRequest request) {
        List<Monitor> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, MonitorResponse.class);
    }

    @Override
    public DashboardResponse getDashboardCount(CurrentUserResponse user) throws KettleException {
        DashboardResponse response = new DashboardResponse();
        int allCount = getAllCount(user);//所有作业总数
        response.setAllCount(allCount);

        int runCount = baseMapper.countRuning(user.getOrganizerId());//运行中作业
        response.setRunCount(runCount);

        int finishCount = baseMapper.countFinish(user.getOrganizerId());//运行完成作业
        response.setFinishCount(finishCount);

        int errorCount = baseMapper.countError(user.getOrganizerId());//运行完成作业
        response.setErrorCount(errorCount);

        Page page =  new Page(1, 5);
        RunTimesResponse runTimes = new RunTimesResponse();

        List<Map<String,Object>> list1 = baseMapper.getTimeConsumingTop5(page,user.getOrganizerId());
        for(Map<String,Object> map : list1){
            runTimes.getNames().add((String) map.get("NAME"));
            runTimes.getTimes().add((Long)map.get("TIME"));
        }
        response.setRunTimes(runTimes);


        List<Map<String,Object>> list2 = baseMapper.get7DayErrorsAndFinishs(user.getOrganizerId());
        RunCountResponse runCounts = new RunCountResponse();
        for(Map<String,Object> map : list2){
            runCounts.getDatetimes().add((String) map.get("DATETIME"));
            runCounts.getErrors().add((String) map.get("ERROR"));
            runCounts.getFinishs().add((String) map.get("FINISH"));
        }
        response.setRunCounts(runCounts);


        return response;
    }

    private int getAllCount(CurrentUserResponse user) throws KettleException {
        int count = 0;
        Repository repository = App.getInstance().getRepository();
        String root = "/"+user.getOrganizerName();
        RepositoryDirectoryInterface dir = repository.findDirectory(root);

        return countChildren(count,repository,dir);

    }

    private int countChildren(int count, Repository repository, RepositoryDirectoryInterface dir) throws KettleException {
        List<RepositoryElementMetaInterface> elements = repository.getTransformationObjects(dir.getObjectId(), false);
        count = count+elements.size();
        elements = repository.getJobObjects(dir.getObjectId(), false);
        count = count+elements.size();
        List<RepositoryDirectoryInterface> directorys = dir.getChildren();
        for(RepositoryDirectoryInterface child : directorys) {
            count = count + countChildren(count,repository,child);
        }
        return count;
    }
}
