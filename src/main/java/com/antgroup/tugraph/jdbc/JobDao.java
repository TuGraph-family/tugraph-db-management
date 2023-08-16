package com.antgroup.tugraph;

public interface JobDao {
    JobStatus getStatusById(Integer id);

    JobResult getResultById(Integer id);

    List<JobStatus> listStatus();

    int create(JobStatus jobStatus);

    int update(JobStatus jobStatus, JobResult jobResult);

    int delete(Integer id);
}