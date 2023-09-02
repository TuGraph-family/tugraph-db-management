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

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TugraphManagementApplicationTests {

	@Autowired
	private JobManagementServiceImpl jobManagementService;

	@Test
	@Transactional
	@Order(0)
	void testJobManagementService() {
		log.info("start testing job management service.");

		// set up ut job info
		String dbId = "127.0.0.1:1234";
		String startTime = "2023-09-01 14:33:57.200";
		String period = "IMMEDIATE";
		String procedureName = "Unit Test Procedure";
		String procedureType = "Khop";
		String status = "SUCCESS";
		String runtime = "144";
		String creator = "tester";
		String createTime = "2023-09-01 14:33:57.200";
		String result = "unit test procedure result";

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
		assertTrue(jobId > 0);
		log.info("job id is: " + Integer.toString(jobId));

		// test read job
		// set up read job status request
		TugraphManagement.ReadJobRequest utReadAllJobRequest =
			TugraphManagement.ReadJobRequest
				.newBuilder()
				.setReadType(TugraphManagement.ReadJobRequest.ReadType.READ_ALL)
				.build();
		// call handleReadJobRequest method, get all job status
		TugraphManagement.ReadJobResponse readAllJobResp =
            jobManagementService.handleReadJobRequest(utReadAllJobRequest, dbId);
		List<TugraphManagement.JobStatus> jobStatusList = readAllJobResp.getJobStatusList();
		Integer curJobCount = jobStatusList.size();
		log.info("total job count: " + Integer.toString(curJobCount));
		TugraphManagement.JobStatus jobStatus = jobStatusList.get(jobStatusList.size() - 1);
		// assert if the job status is correct
		assertEquals(jobStatus.getDbId(), dbId);
		assertEquals(jobStatus.getJobId(), jobId);
		assertEquals(jobStatus.getStartTime(), startTime);
		assertEquals(jobStatus.getPeriod(), period);
		assertEquals(jobStatus.getProcedureName(), procedureName);
		assertEquals(jobStatus.getProcedureType(), procedureType);
		assertEquals(jobStatus.getStatus(), "pending");
		assertEquals(jobStatus.getRuntime(), "-");
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
		readAllJobResp =
            jobManagementService.handleReadJobRequest(utReadAllJobRequest, dbId);
		jobStatusList = readAllJobResp.getJobStatusList();
		assertEquals(curJobCount, jobStatusList.size());
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
		// assert if job result is correct
		TugraphManagement.ReadJobRequest utReadJobResultRequest =
			TugraphManagement.ReadJobRequest
				.newBuilder()
				.setReadType(TugraphManagement.ReadJobRequest.ReadType.READ_RESULT)
				.setJobId(jobId)
				.build();
		TugraphManagement.ReadJobResponse readJobResultResp =
            jobManagementService.handleReadJobRequest(utReadJobResultRequest, dbId);
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
		readAllJobResp =
            jobManagementService.handleReadJobRequest(utReadAllJobRequest, dbId);
		jobStatusList = readAllJobResp.getJobStatusList();
		assertEquals(curJobCount, jobStatusList.size() + 1);
	}

}
