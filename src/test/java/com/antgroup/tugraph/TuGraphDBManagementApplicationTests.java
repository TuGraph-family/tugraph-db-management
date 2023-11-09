package com.antgroup.tugraph;

import com.antgroup.tugraph.job.*;
import com.antgroup.tugraph.service.*;
import lgraph.TuGraphDBManagement;
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
	private HeartbeatServiceImpl heartbeatService;

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
		String procedureType1 = "C++";
		String procedureType2 = "Python";
		String status = "SUCCESS";
		Long runtime = 144L;
		String user = "tester";
		Long createTime = System.currentTimeMillis();
		String result = "unit test procedure result";

		// test java time format
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
		Date dStart = new Date(startTime);
		log.info("当前时间为: " + ft.format(dStart));

		// test create job
		// set up create job request
		TuGraphDBManagement.CreateJobRequest utCreateJobRequest =
			TuGraphDBManagement.CreateJobRequest
				.newBuilder()
				.setStartTime(startTime)
				.setPeriod(period)
				.setProcedureName(procedureName)
                .setProcedureType(procedureType1)
				.setUser(user)
                .setCreateTime(createTime)
				.build();
		// call handleCreateJobRequest method, get job id
		TuGraphDBManagement.CreateJobResponse createJobResp =
            jobManagementService.handleCreateJobRequest(utCreateJobRequest, dbId);
		Integer jobId = createJobResp.getJobId();
		assertTrue(jobId == 1);
		// creat another job to test self increament jobid
		TuGraphDBManagement.CreateJobRequest utCreateJobRequest2 =
			TuGraphDBManagement.CreateJobRequest
				.newBuilder()
				.setStartTime(startTime)
				.setPeriod(period)
				.setProcedureName(procedureName)
                .setProcedureType(procedureType2)
				.setUser(user)
                .setCreateTime(createTime)
				.build();
		TuGraphDBManagement.CreateJobResponse createJobResp2 =
            jobManagementService.handleCreateJobRequest(utCreateJobRequest2, dbId);
		Integer jobId2 = createJobResp2.getJobId();
		assertTrue(jobId2 == 2);


		// test read job
		// set up read job request
		TuGraphDBManagement.GetJobStatusRequest utGetJobStatusRequest =
			TuGraphDBManagement.GetJobStatusRequest
				.newBuilder()
				.build();
		// call handleGetJobStatusRequest method, get all job status
		TuGraphDBManagement.GetJobStatusResponse readJobResp =
            jobManagementService.handleGetJobStatusRequest(utGetJobStatusRequest, dbId);
		List<TuGraphDBManagement.Job> jobList = readJobResp.getJobList();
		assertEquals(2, jobList.size());
		TuGraphDBManagement.Job job = jobList.get(0);
		// assert if the job info is correct
		assertEquals(job.getDbId(), dbId);
		assertEquals(job.getJobId(), jobId);
		assertEquals(job.getStartTime(), startTime);
		assertEquals(job.getPeriod(), period);
		assertEquals(job.getProcedureName(), procedureName);
		assertEquals(job.getProcedureType(), procedureType1);
		assertEquals(job.getStatus(), "pending");
		assertEquals(job.getRuntime(), -1L);
		assertEquals(job.getUser(), user);
		assertEquals(job.getCreateTime(), createTime);

		// test update job
		// set up update job request
		TuGraphDBManagement.UpdateJobStatusRequest utUpdateJobStatusRequest =
			TuGraphDBManagement.UpdateJobStatusRequest
				.newBuilder()
				.setJobId(jobId2)
				.setStatus(status)
				.setRuntime(runtime)
				.setResult(result)
				.build();
		// call handleUpdateJobStatusRequest method, update ut job
		TuGraphDBManagement.UpdateJobStatusResponse updateJobResp =
            jobManagementService.handleUpdateJobStatusRequest(utUpdateJobStatusRequest, dbId);
		// assert if updated job info and job result are correct
		// assert if job info is correct
		// test read job status by job id
		TuGraphDBManagement.GetJobStatusRequest utReadJobByIdRequest =
			TuGraphDBManagement.GetJobStatusRequest
				.newBuilder()
				.setJobId(jobId2)
				.build();
		TuGraphDBManagement.GetJobStatusResponse readJobByIdResp =
            jobManagementService.handleGetJobStatusRequest(utReadJobByIdRequest, dbId);
		jobList = readJobByIdResp.getJobList();
		assertEquals(1, jobList.size());
		TuGraphDBManagement.Job job2 = jobList.get(0);
		assertEquals(job2.getDbId(), dbId);
		assertEquals(job2.getJobId(), jobId2);
		assertEquals(job2.getStartTime(), startTime);
		assertEquals(job2.getPeriod(), period);
		assertEquals(job2.getProcedureName(), procedureName);
		assertEquals(job2.getProcedureType(), procedureType2);
		assertEquals(job2.getStatus(), status);
		assertEquals(job2.getRuntime(), runtime);
		assertEquals(job2.getUser(), user);
		assertEquals(job2.getCreateTime(), createTime);

		// test read job result
		// assert if job result is correct
		TuGraphDBManagement.GetAlgoResultRequest utGetAlgoResultRequest =
			TuGraphDBManagement.GetAlgoResultRequest
				.newBuilder()
				.setJobId(jobId2)
				.build();
		TuGraphDBManagement.GetAlgoResultResponse readAlgoResultResp =
            jobManagementService.handleGetAlgoResultRequest(utGetAlgoResultRequest, dbId);
		TuGraphDBManagement.AlgoResult AlgoResult = readAlgoResultResp.getAlgoResult();
		assertEquals(AlgoResult.getJobId(), jobId2);
		assertEquals(AlgoResult.getResult(), result);

		// test read job result error response
		TuGraphDBManagement.GetAlgoResultRequest utErrGetAlgoResultRequest =
			TuGraphDBManagement.GetAlgoResultRequest
				.newBuilder()
				.setJobId(-1)
				.build();
		TuGraphDBManagement.GetAlgoResultResponse errReadAlgoResultResp =
            jobManagementService.handleGetAlgoResultRequest(utErrGetAlgoResultRequest, dbId);
		assertEquals(errReadAlgoResultResp.getResponseCode(), TuGraphDBManagement.ResponseCode.FAILED);


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
            jobManagementService.handleGetJobStatusRequest(utGetJobStatusRequest, dbId);
		jobList = readJobResp.getJobList();
		assertEquals(1, jobList.size());
		assertEquals(jobId2, jobList.get(0).getJobId());
	}

	@Test
	@Transactional
	@Order(1)
	void testHeartbeatService() {
		log.info("start testing heartbeat service.");

		String reqMsg = "This is a heartbeat request message.";
        int heartbeatCount = 0;

		TuGraphDBManagement.HeartbeatRequest heartbeatRequest =
            TuGraphDBManagement.HeartbeatRequest
                .newBuilder()
				.setRequestMsg(reqMsg)
				.setHeartbeatCount(heartbeatCount)
				.build();
        TuGraphDBManagement.HeartbeatResponse heartbeatResponse =
			heartbeatService.detectHeartbeat(heartbeatRequest);

		assertEquals(heartbeatCount + 1, heartbeatResponse.getHeartbeatCount());
	}

}
