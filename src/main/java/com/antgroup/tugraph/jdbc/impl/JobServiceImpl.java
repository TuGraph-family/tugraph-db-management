package com.antgroup.tugraph;

import java.util.List;

public class JobServiceImpl implements JobService {
    private final JobDao jobDao;

    public JobServiceImpl(JobDao jobDao) {
        this.jobDao =  jobDao;
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
    public int update(JobStatus jobStatus, JobResult jobResult) {
        return this.jobDao.update(jobStatus, jobResult);
    }

    @Override
    public int delete(Integer id) {
        return this.delete(id);
    }
}