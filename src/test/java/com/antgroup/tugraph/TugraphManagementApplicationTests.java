package com.antgroup.tugraph;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TugraphManagementApplicationTests {

	@Autowired
	private JobManagementServiceImpl jobManagementService;

	// @Test
	// void contextLoads() {
	// 	log.info("开始单元测试1");
	// 	Assert.assertEquals(0, 1);
	// }

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
		JobStatus utJobStatus =
            new JobStatus()
                .setDbId(dbId)
                .setStartTime(startTime)
                .setPeriod(period)
                .setProcedureName(procedureName)
                .setProcedureType(procedureType)
				.setStatus(status)
				.setRuntime(runtime)
                .setCreator(creator)
                .setCreateTime(createTime);

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
		// test handleCreateJobRequest method, get job id
		TugraphManagement.CreateJobResponse createJobResp =
            jobManagementService.handleCreateJobRequest(utCreateJobRequest, dbId);
		Integer jobId = createJobResp.getJobId();
		assertTrue(jobId > 0);

		// test read job
		// call
		// test update job
		// test delete job
	}

}
