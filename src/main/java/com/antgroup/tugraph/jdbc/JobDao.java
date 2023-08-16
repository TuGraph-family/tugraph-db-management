package com.antgroup.tugraph;

import java.util.List;

public interface JobDao {
    void initTable();

    JobStatus getStatusById(Integer id);

    JobResult getResultById(Integer id);

    List<JobStatus> listStatus();

    int create(JobStatus jobStatus);

    int update(JobStatus jobStatus, JobResult jobResult);

    int delete(Integer id);
}