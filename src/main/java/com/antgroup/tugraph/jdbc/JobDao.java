package com.antgroup.tugraph;

import java.util.List;

public interface JobDao {
    void initDB();

    JobStatus getStatusById(Integer id);

    JobResult getResultById(Integer id);

    List<JobStatus> listStatus();

    int create(JobStatus jobStatus);

    void update(JobStatus jobStatus, JobResult jobResult);

    int delete(Integer id);
}
