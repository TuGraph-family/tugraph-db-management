package com.antgroup.tugraph;

import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class JobDaoImpl implements JobDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public JobStatus getStatusById(Integer id) {
        JobStatus jobStatus  = jdbcTemplate.queryForObject("select * from job_status where jobId = ?", new BeanPropertyRowMapper<JobStatus>(JobStatus.class), id);
        return jobStatus;
    }

    @Override
    public List<JobStatus> listStatus() {
        List<JobStatus> jobStatus = jdbcTemplate.query("select * from job_status", new BeanPropertyRowMapper<JobStatus>(JobStatus.class));
        return jobStatus;
    }

    @Override
    public int create(JobStatus jobStatus) {
        return 0;
    }

    @Override
    public int update(JobStatus jobStatus, JobResult jobResult) {
        return 0;
    }

    @Override
    public int delete(Integer id) {
        return 0;
    }
}