package com.antgroup.tugraph;

import java.util.List;

public interface JobDao {
    void initDB();

    Job getStatusById(Integer id);

    JobResult getResultById(Integer id);

    List<Job> listStatus();

    int create(Job Job);

    void update(Job Job, JobResult jobResult);

    int delete(Integer id);

    void clearAll();
}
