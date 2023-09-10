package com.antgroup.tugraph;

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
    public JobResult getResultById(Integer id) {
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
    public void update(Job Job, JobResult jobResult) {
        this.jobDao.update(Job, jobResult);
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
