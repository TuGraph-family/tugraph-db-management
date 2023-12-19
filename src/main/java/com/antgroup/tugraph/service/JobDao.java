package com.antgroup.tugraph.service;

import com.antgroup.tugraph.model.AlgoResult;
import com.antgroup.tugraph.model.Job;
import java.util.List;

public interface JobDao {
    void initDB();

    Job getStatusById(Integer id);

    AlgoResult getResultById(Integer id);

    List<Job> listStatus();

    int create(Job Job);

    void update(Job Job, AlgoResult AlgoResult);

    int delete(Integer id);

    void clearAll();
}
