package com.antgroup.tugraph.serviceImpl;

import com.antgroup.tugraph.model.AlgoResult;
import com.antgroup.tugraph.model.Job;
import com.antgroup.tugraph.service.JobDao;
import com.antgroup.tugraph.service.JobService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private JobDao jobDao;

    @Override
    public void initDB() {
        this.jobDao.initDB();
    }

    @Override
    public Job getStatusById(Integer id) {
        return this.jobDao.getStatusById(id);
    }

    @Override
    public AlgoResult getResultById(Integer id) {
        return this.jobDao.getResultById(id);
    }

    @Override
    public List<Job> listStatus() {
        return this.jobDao.listStatus();
    }

    @Override
    public int create(Job Job) {
        return this.jobDao.create(Job);
    }

    @Override
    public void update(Job Job, AlgoResult AlgoResult) {
        this.jobDao.update(Job, AlgoResult);
    }

    @Override
    public int delete(Integer id) {
        return this.jobDao.delete(id);
    }

    @Override
    public void clearAll() {
        this.jobDao.clearAll();
    }
}
