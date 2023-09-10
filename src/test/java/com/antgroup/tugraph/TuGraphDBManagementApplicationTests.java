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

		// test read job status
		// set up read job status request
		TuGraphDBManagement.ReadJobStatusRequest utReadJobStatusRequest =
			TuGraphDBManagement.ReadJobStatusRequest
				.newBuilder()
				.build();
		// call handleReadJobRequest method, get all job status
		TuGraphDBManagement.ReadJobStatusResponse readJobStatusResp =
            jobManagementService.handleReadJobStatusRequest(utReadJobStatusRequest, dbId);
		List<TuGraphDBManagement.JobStatus> jobStatusList = readJobStatusResp.getJobStatusList();
		assertEquals(1, jobStatusList.size());
		TuGraphDBManagement.JobStatus jobStatus = jobStatusList.get(0);
		// assert if the job status is correct
		assertEquals(jobStatus.getDbId(), dbId);
		assertEquals(jobStatus.getJobId(), jobId);
		assertEquals(jobStatus.getStartTime(), startTime);
		assertEquals(jobStatus.getPeriod(), period);
		assertEquals(jobStatus.getProcedureName(), procedureName);
		assertEquals(jobStatus.getProcedureType(), procedureType);
		assertEquals(jobStatus.getStatus(), "pending");
		assertEquals(jobStatus.getRuntime(), -1L);
		assertEquals(jobStatus.getUser(), user);
		assertEquals(jobStatus.getCreateTime(), createTime);

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
		// assert if updated job status and job result are correct
		// assert if job status is correct
		readJobStatusResp =
            jobManagementService.handleReadJobStatusRequest(utReadJobStatusRequest, dbId);
		jobStatusList = readJobStatusResp.getJobStatusList();
		assertEquals(1, jobStatusList.size());
		jobStatus = jobStatusList.get(jobStatusList.size() - 1);
		assertEquals(jobStatus.getDbId(), dbId);
		assertEquals(jobStatus.getJobId(), jobId);
		assertEquals(jobStatus.getStartTime(), startTime);
		assertEquals(jobStatus.getPeriod(), period);
		assertEquals(jobStatus.getProcedureName(), procedureName);
		assertEquals(jobStatus.getProcedureType(), procedureType);
		assertEquals(jobStatus.getStatus(), status);
		assertEquals(jobStatus.getRuntime(), runtime);
		assertEquals(jobStatus.getUser(), user);
		assertEquals(jobStatus.getCreateTime(), createTime);

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
		readJobStatusResp =
            jobManagementService.handleReadJobStatusRequest(utReadJobStatusRequest, dbId);
		jobStatusList = readJobStatusResp.getJobStatusList();
		assertEquals(0, jobStatusList.size());
	}

}
