package com.antgroup.tugraph.service;

import com.antgroup.tugraph.model.AlgoResult;
import com.antgroup.tugraph.model.Job;
import java.util.List;

public interface JobService {
    void initDB();

    Job getStatusById(String id);

    AlgoResult getResultById(String id);

    List<Job> listStatus();

    int create(Job Job);

    void update(Job Job, AlgoResult AlgoResult);

    int delete(String id);

    void clearAll();
}
