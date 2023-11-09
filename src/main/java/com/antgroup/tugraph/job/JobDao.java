package com.antgroup.tugraph.job;

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
