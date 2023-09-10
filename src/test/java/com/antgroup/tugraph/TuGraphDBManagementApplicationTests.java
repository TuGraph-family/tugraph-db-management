package com.antgroup.tugraph;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TuGraphDBManagementApplicationTests {

	@Autowired
	private JobManagementServiceImpl jobManagementService;

	@Autowired
    private JobService jobService;

	@Test
	@Transactional
	@Order(0)
	void testJobManagementService() {
		log.info("start testing job management service.");

		// clear all table in db
		jobService.clearAll();

		// set up ut job info
		String dbId = "127.0.0.1:1234";
		Long startTime = System.currentTimeMillis();
		String period = "IMMEDIATE";
		String procedureName = "Unit Test Procedure";
		String procedureType = "Khop";
		String status = "SUCCESS";
		Long runtime = 144L;
		String user = "tester";
		Long createTime = System.currentTimeMillis();
		String result = "unit test procedure result";

		// test java time format
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
		Date dStart = new Date(startTime);
		log.info("当前时间为: " + ft.format(dStart));

		// test creat job
		// set up creat job request
		TuGraphDBManagement.CreateJobRequest utCreateJobRequest =
			TuGraphDBManagement.CreateJobRequest
				.newBuilder()
				.setStartTime(startTime)
				.setPeriod(period)
				.setProcedureName(procedureName)
                .setProcedureType(procedureType)
				.setUser(user)
                .setCreateTime(createTime)
				.build();
		// call handleCreateJobRequest method, get job id
		TuGraphDBManagement.CreateJobResponse createJobResp =
            jobManagementService.handleCreateJobRequest(utCreateJobRequest, dbId);
		Integer jobId = createJobResp.getJobId();
		assertTrue(jobId == 1);
		log.info("job id is: " + Integer.toString(jobId));

		// test read job
		// set up read job request
		TuGraphDBManagement.ReadJobRequest utReadJobRequest =
			TuGraphDBManagement.ReadJobRequest
				.newBuilder()
				.build();
		// call handleReadJobRequest method, get all job status
		TuGraphDBManagement.ReadJobResponse readJobResp =
            jobManagementService.handleReadJobRequest(utReadJobRequest, dbId);
		List<TuGraphDBManagement.Job> JobList = readJobResp.getJobList();
		assertEquals(1, JobList.size());
		TuGraphDBManagement.Job Job = JobList.get(0);
		// assert if the job info is correct
		assertEquals(Job.getDbId(), dbId);
		assertEquals(Job.getJobId(), jobId);
		assertEquals(Job.getStartTime(), startTime);
		assertEquals(Job.getPeriod(), period);
		assertEquals(Job.getProcedureName(), procedureName);
		assertEquals(Job.getProcedureType(), procedureType);
		assertEquals(Job.getStatus(), "pending");
		assertEquals(Job.getRuntime(), -1L);
		assertEquals(Job.getUser(), user);
		assertEquals(Job.getCreateTime(), createTime);

		// test update job
		// set up update job request
		TuGraphDBManagement.UpdateJobRequest utUpdateJobRequest =
			TuGraphDBManagement.UpdateJobRequest
				.newBuilder()
				.setJobId(jobId)
				.setStatus(status)
				.setRuntime(runtime)
				.setResult(result)
				.build();
		// call handleUpdateJobRequest method, update ut job
		TuGraphDBManagement.UpdateJobResponse updateJobResp =
            jobManagementService.handleUpdateJobRequest(utUpdateJobRequest, dbId);
		// assert if updated job info and job result are correct
		// assert if job info is correct
		readJobResp =
            jobManagementService.handleReadJobRequest(utReadJobRequest, dbId);
		JobList = readJobResp.getJobList();
		assertEquals(1, JobList.size());
		Job = JobList.get(JobList.size() - 1);
		assertEquals(Job.getDbId(), dbId);
		assertEquals(Job.getJobId(), jobId);
		assertEquals(Job.getStartTime(), startTime);
		assertEquals(Job.getPeriod(), period);
		assertEquals(Job.getProcedureName(), procedureName);
		assertEquals(Job.getProcedureType(), procedureType);
		assertEquals(Job.getStatus(), status);
		assertEquals(Job.getRuntime(), runtime);
		assertEquals(Job.getUser(), user);
		assertEquals(Job.getCreateTime(), createTime);

		// test read job result
		// assert if job result is correct
		TuGraphDBManagement.ReadJobResultRequest utReadJobResultRequest =
			TuGraphDBManagement.ReadJobResultRequest
				.newBuilder()
				.setJobId(jobId)
				.build();
		TuGraphDBManagement.ReadJobResultResponse readJobResultResp =
            jobManagementService.handleReadJobResultRequest(utReadJobResultRequest, dbId);
		TuGraphDBManagement.JobResult jobResult = readJobResultResp.getJobResult();
		assertEquals(jobResult.getJobId(), jobId);
		assertEquals(jobResult.getResult(), result);

		// test read job result error response
		TuGraphDBManagement.ReadJobResultRequest utErrReadJobResultRequest =
			TuGraphDBManagement.ReadJobResultRequest
				.newBuilder()
				.setJobId(-1)
				.build();
		TuGraphDBManagement.ReadJobResultResponse errReadJobResultResp =
            jobManagementService.handleReadJobResultRequest(utErrReadJobResultRequest, dbId);
		assertEquals(errReadJobResultResp.getResponseCode(), TuGraphDBManagement.ResponseCode.FAILED);


		// test delete job
		// set up delete job request
		TuGraphDBManagement.DeleteJobRequest utDeleteJobRequest =
			TuGraphDBManagement.DeleteJobRequest
				.newBuilder()
				.setJobId(jobId)
				.build();
		// call handleDeleteJobRequest method, delete ut job
		TuGraphDBManagement.DeleteJobResponse deleteJobResp =
            jobManagementService.handleDeleteJobRequest(utDeleteJobRequest, dbId);
		// assert if the job has been deleted
		readJobResp =
            jobManagementService.handleReadJobRequest(utReadJobRequest, dbId);
		JobList = readJobResp.getJobList();
		assertEquals(0, JobList.size());
	}

}
