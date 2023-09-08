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
class TugraphManagementApplicationTests {

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
		String creator = "tester";
		Long createTime = System.currentTimeMillis();
		String result = "unit test procedure result";

		// test java time format
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
		Date dStart = new Date(startTime);
		log.info("当前时间为: " + ft.format(dStart));

		// test creat job
		// set up creat job request
		TugraphManagement.CreateJobRequest utCreateJobRequest =
			TugraphManagement.CreateJobRequest
				.newBuilder()
				.setStartTime(startTime)
				.setPeriod(period)
				.setProcedureName(procedureName)
                .setProcedureType(procedureType)
				.setCreator(creator)
                .setCreateTime(createTime)
				.build();
		// call handleCreateJobRequest method, get job id
		TugraphManagement.CreateJobResponse createJobResp =
            jobManagementService.handleCreateJobRequest(utCreateJobRequest, dbId);
		Integer jobId = createJobResp.getJobId();
		assertTrue(jobId == 1);
		log.info("job id is: " + Integer.toString(jobId));

		// test read job status
		// set up read job status request
		TugraphManagement.ReadJobStatusRequest utReadJobStatusRequest =
			TugraphManagement.ReadJobStatusRequest
				.newBuilder()
				.build();
		// call handleReadJobRequest method, get all job status
		TugraphManagement.ReadJobStatusResponse readJobStatusResp =
            jobManagementService.handleReadJobStatusRequest(utReadJobStatusRequest, dbId);
		List<TugraphManagement.JobStatus> jobStatusList = readJobStatusResp.getJobStatusList();
		assertEquals(1, jobStatusList.size());
		TugraphManagement.JobStatus jobStatus = jobStatusList.get(0);
		// assert if the job status is correct
		assertEquals(jobStatus.getDbId(), dbId);
		assertEquals(jobStatus.getJobId(), jobId);
		assertEquals(jobStatus.getStartTime(), startTime);
		assertEquals(jobStatus.getPeriod(), period);
		assertEquals(jobStatus.getProcedureName(), procedureName);
		assertEquals(jobStatus.getProcedureType(), procedureType);
		assertEquals(jobStatus.getStatus(), "pending");
		assertEquals(jobStatus.getRuntime(), -1L);
		assertEquals(jobStatus.getCreator(), creator);
		assertEquals(jobStatus.getCreateTime(), createTime);

		// test update job
		// set up update job request
		TugraphManagement.UpdateJobRequest utUpdateJobRequest =
			TugraphManagement.UpdateJobRequest
				.newBuilder()
				.setJobId(jobId)
				.setStatus(status)
				.setRuntime(runtime)
				.setResult(result)
				.build();
		// call handleUpdateJobRequest method, update ut job
		TugraphManagement.UpdateJobResponse updateJobResp =
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
		assertEquals(jobStatus.getCreator(), creator);
		assertEquals(jobStatus.getCreateTime(), createTime);

		// test read job result
		// assert if job result is correct
		TugraphManagement.ReadJobResultRequest utReadJobResultRequest =
			TugraphManagement.ReadJobResultRequest
				.newBuilder()
				.setJobId(jobId)
				.build();
		TugraphManagement.ReadJobResultResponse readJobResultResp =
            jobManagementService.handleReadJobResultRequest(utReadJobResultRequest, dbId);
		TugraphManagement.JobResult jobResult = readJobResultResp.getJobResult();
		assertEquals(jobResult.getJobId(), jobId);
		assertEquals(jobResult.getResult(), result);

		// test delete job
		// set up delete job request
		TugraphManagement.DeleteJobRequest utDeleteJobRequest =
			TugraphManagement.DeleteJobRequest
				.newBuilder()
				.setJobId(jobId)
				.build();
		// call handleDeleteJobRequest method, delete ut job
		TugraphManagement.DeleteJobResponse deleteJobResp =
            jobManagementService.handleDeleteJobRequest(utDeleteJobRequest, dbId);
		// assert if the job has been deleted
		readJobStatusResp =
            jobManagementService.handleReadJobStatusRequest(utReadJobStatusRequest, dbId);
		jobStatusList = readJobStatusResp.getJobStatusList();
		assertEquals(0, jobStatusList.size());
	}

}
