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
            + "`user` TEXT,"
            + "`createTime` INTEGER"
            + ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS job_result("
            + "`jobId` INTEGER PRIMARY KEY,"
            + "`result` TEXT"
            + ")");
	}

    @Override
    public Job getStatusById(Integer id) {
        Job Job  = jdbcTemplate.queryForObject("select * from job_status where jobId = ?", new BeanPropertyRowMapper<Job>(Job.class), id);
        return Job;
    }

    @Override
    public JobResult getResultById(Integer id) {
        JobResult jobResult  = jdbcTemplate.queryForObject("select * from job_result where jobId = ?", new BeanPropertyRowMapper<JobResult>(JobResult.class), id);
        return jobResult;
    }

    @Override
    public List<Job> listStatus() {
        List<Job> JobList = jdbcTemplate.query("select * from job_status", new BeanPropertyRowMapper<Job>(Job.class));
        return JobList;
    }

    @Override
    public int create(Job Job) {
        final String sql = "insert into job_status(dbId, startTime, period, procedureName, procedureType, status, runtime, user, createTime) values(?,?,?,?,?,?,?,?,?)";
        Object[] params = new Object[]{
            Job.getDbId(),
            Job.getStartTime(),
            Job.getPeriod(),
            Job.getProcedureName(),
            Job.getProcedureType(),
            "pending",
            -1L,
            Job.getUser(),
            Job.getCreateTime()
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
    public void update(Job Job, JobResult jobResult) {
        jdbcTemplate.update("UPDATE job_status SET status = ? , runtime = ? WHERE jobId=?",
                Job.getStatus(), Job.getRuntime(), Job.getJobId());

        if (Job.getStatus().equals("SUCCESS")) {
            jdbcTemplate.update("insert into job_result(jobId, result) values(?,?)",
                jobResult.getJobId(), jobResult.getResult());
        }
    }

    @Override
    public int delete(Integer id) {
        return jdbcTemplate.update("DELETE FROM job_status WHERE jobId=?", id);
    }

    @Override
    public void clearAll() {
        jdbcTemplate.update("DELETE FROM job_status");
        jdbcTemplate.update("DELETE FROM job_result");
        jdbcTemplate.update("DELETE FROM sqlite_sequence");
    }
}

