package com.antgroup.tugraph;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TugraphManagementApplicationRunner implements ApplicationRunner {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void initDB() {
		jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS job_status("
            + "jobId INT PRIMARY KEY,"
            + "jobType VARCHAR(255),"
            + "startTime VARCHAR(255),"
            + "period VARCHAR(255),"
            + "procedureName VARCHAR(255),"
            + "procedureType VARCHAR(255),"
            + "status VARCHAR(255),"
            + "runtime VARCHAR(255),"
            + "creator VARCHAR(255),"
            + "createTime VARCHAR(255)"
            +")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS job_result("
            + "jobId INT PRIMARY KEY,"
            + "result VARCHAR(255)"
            +")");
	}

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initDB();
    }
}