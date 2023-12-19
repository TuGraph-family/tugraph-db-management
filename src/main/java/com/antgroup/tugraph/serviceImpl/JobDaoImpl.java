package com.antgroup.tugraph.serviceImpl;

import com.antgroup.tugraph.model.AlgoResult;
import com.antgroup.tugraph.model.Job;
import com.antgroup.tugraph.service.JobDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS job("
                                 + "`jobId` INTEGER PRIMARY KEY AUTOINCREMENT,"
                                 + "`taskId` VARCHAR(40) UNIQUE,"
                                 + "`taskName` VARCHAR(40) UNIQUE,"
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
                                 + "`taskId` VARCHAR(40) PRIMARY KEY,"
                                 + "`result` TEXT"
                                 + ")");
    }

    @Override
    public Job getStatusById(String id) {
        Job Job =
            jdbcTemplate.queryForObject("select * from job where taskId = ?", new BeanPropertyRowMapper<Job>(Job.class),
                                        id);
        return Job;
    }

    @Override
    public AlgoResult getResultById(String id) {
        AlgoResult AlgoResult = jdbcTemplate.queryForObject("select * from job_result where taskId = ?",
                                                            new BeanPropertyRowMapper<AlgoResult>(AlgoResult.class),
                                                            id);
        return AlgoResult;
    }

    @Override
    public List<Job> listStatus() {
        List<Job> JobList = jdbcTemplate.query("select * from job", new BeanPropertyRowMapper<Job>(Job.class));
        return JobList;
    }

    @Override
    public int create(Job Job) {
        final String sql =
            "insert into job(taskId, taskName, dbId, startTime, period, procedureName, procedureType, status, "
                + "runtime, user, createTime) values(?,?,?,?,?,?,?,?,?,?,?)";
        Object[] params = new Object[] {
            Job.getTaskId(),
            Job.getTaskName(),
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
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            for (Object p : params) {
                ps.setObject(i, p);
                i++;
            }
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(Job Job, AlgoResult AlgoResult) {
        jdbcTemplate.update("UPDATE job SET status = ? , runtime = ? WHERE taskId=?",
                            Job.getStatus(), Job.getRuntime(), Job.getTaskId());
        jdbcTemplate.update("insert into job_result(taskId, result) values(?,?)",
                            AlgoResult.getTaskId(), AlgoResult.getResult());
    }

    @Override
    public int delete(String id) {
        return jdbcTemplate.update("DELETE FROM job WHERE taskId=?", id);
    }

    @Override
    public void clearAll() {
        jdbcTemplate.update("DELETE FROM job");
        jdbcTemplate.update("DELETE FROM job_result");
        jdbcTemplate.update("DELETE FROM sqlite_sequence");
    }
}

