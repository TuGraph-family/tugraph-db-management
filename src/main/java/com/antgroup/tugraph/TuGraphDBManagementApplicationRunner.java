package com.antgroup.tugraph;

import com.antgroup.tugraph.service.JobService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TuGraphDBManagementApplicationRunner implements ApplicationRunner {
    @Autowired
    private JobService jobService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        jobService.initDB();
    }
}
