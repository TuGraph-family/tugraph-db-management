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
    public JobStatus getStatusById(Integer id) {
        return this.jobDao.getStatusById(id);
    }

    @Override
    public JobResult getResultById(Integer id) {
        return this.jobDao.getResultById(id);
    }

    @Override
    public List<JobStatus> listStatus() {
        return this.jobDao.listStatus();
    }

    @Override
    public int create(JobStatus jobStatus) {
        return this.jobDao.create(jobStatus);
    }

    @Override
    public void update(JobStatus jobStatus, JobResult jobResult) {
        this.jobDao.update(jobStatus, jobResult);
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
