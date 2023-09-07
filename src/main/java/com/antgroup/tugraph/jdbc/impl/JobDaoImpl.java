package com.antgroup.tugraph;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.PreparedStatementCreator;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Repository
@Slf4j
public class JobDaoImpl implements JobDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void initDB() {
		jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS job_status("
            + "`jobId` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`dbId` VARCHAR(21),"
            + "`startTime` INTEGER,"
            + "`period` VARCHAR(9),"
            + "`procedureName` TEXT,"
            + "`procedureType` TEXT,"
            + "`status` VARCHAR(7),"
            + "`runtime` INTEGER,"
            + "`creator` TEXT,"
            + "`createTime` INTEGER"
            + ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS job_result("
            + "`jobId` INTEGER PRIMARY KEY,"
            + "`result` TEXT"
            + ")");
	}

    @Override
    public JobStatus getStatusById(Integer id) {
        JobStatus jobStatus  = jdbcTemplate.queryForObject("select * from job_status where jobId = ?", new BeanPropertyRowMapper<JobStatus>(JobStatus.class), id);
        return jobStatus;
    }

    @Override
    public JobResult getResultById(Integer id) {
        JobResult jobResult  = jdbcTemplate.queryForObject("select * from job_result where jobId = ?", new BeanPropertyRowMapper<JobResult>(JobResult.class), id);
        return jobResult;
    }

    @Override
    public List<JobStatus> listStatus() {
        List<JobStatus> jobStatusList = jdbcTemplate.query("select * from job_status", new BeanPropertyRowMapper<JobStatus>(JobStatus.class));
        return jobStatusList;
    }

    @Override
    public int create(JobStatus jobStatus) {
        final String sql = "insert into job_status(dbId, startTime, period, procedureName, procedureType, status, runtime, creator, createTime) values(?,?,?,?,?,?,?,?,?)";
        Object[] params = new Object[]{
            jobStatus.getDbId(),
            jobStatus.getStartTime(),
            jobStatus.getPeriod(),
            jobStatus.getProcedureName(),
            jobStatus.getProcedureType(),
            "pending",
            "-",
            jobStatus.getCreator(),
            jobStatus.getCreateTime()
        };
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                int i = 1;
                for (Object p : params) {
                    ps.setObject(i, p);
                    i++;
                }
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(JobStatus jobStatus, JobResult jobResult) {
        jdbcTemplate.update("UPDATE job_status SET status = ? , runtime = ? WHERE jobId=?",
                jobStatus.getStatus(), jobStatus.getRuntime(), jobStatus.getJobId());

        if (jobStatus.getStatus().equals("SUCCESS")) {
            jdbcTemplate.update("insert into job_result(jobId, result) values(?,?)",
                jobResult.getJobId(), jobResult.getResult());
        }
    }

    @Override
    public int delete(Integer id) {
        return jdbcTemplate.update("DELETE FROM job_status WHERE jobId=?", id);
    }
}

