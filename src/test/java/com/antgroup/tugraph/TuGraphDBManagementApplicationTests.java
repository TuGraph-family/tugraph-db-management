package com.antgroup.tugraph;

import com.antgroup.tugraph.service.JobService;
import com.antgroup.tugraph.serviceImpl.JobManagementServiceImpl;
import lgraph.management.TuGraphDBManagement.*;
import org.junit.jupiter.api.Test;

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
        String uuid1 = "utTaskId1";
        String taskName1 = "name1";
        String taskName2 = "name2";
        String uuid2 = "utTaskId2";
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
        log.info("Current time: " + ft.format(dStart));

        // test create job
        jobManagementService.handleCreateJobRequest(CreateJobRequest.newBuilder()
                                                                    .setTaskId(uuid1)
                                                                    .setTaskName(taskName1)
                                                                    .setStartTime(startTime)
                                                                    .setPeriod(period)
                                                                    .setProcedureName(procedureName)
                                                                    .setProcedureType(procedureType1)
                                                                    .setUser(user)
                                                                    .setCreateTime(createTime)
                                                                    .build(), dbId);
        jobManagementService.handleCreateJobRequest(CreateJobRequest.newBuilder()
                                                                    .setTaskId(uuid2)
                                                                    .setTaskName(taskName2)
                                                                    .setStartTime(startTime)
                                                                    .setPeriod(period)
                                                                    .setProcedureName(procedureName)
                                                                    .setProcedureType(procedureType2)
                                                                    .setUser(user)
                                                                    .setCreateTime(createTime)
                                                                    .build(), dbId);

        // test read job status
        JobManagementResponse readJobResp =
            jobManagementService.handleGetJobStatusRequest(GetJobStatusRequest.newBuilder().build(),
                                                           dbId);
        List<Job> jobList = readJobResp.getGetJobStatusResponse().getJobList();
        assertEquals(2, jobList.size());
        Job job = jobList.get(0);
        assertEquals(job.getJobId(), 1);
        assertEquals(job.getDbId(), dbId);
        assertEquals(job.getTaskId(), uuid1);
        assertEquals(job.getStartTime(), startTime);
        assertEquals(job.getPeriod(), period);
        assertEquals(job.getProcedureName(), procedureName);
        assertEquals(job.getProcedureType(), procedureType1);
        assertEquals(job.getStatus(), "pending");
        assertEquals(job.getRuntime(), -1L);
        assertEquals(job.getUser(), user);
        assertEquals(job.getCreateTime(), createTime);

        // test update job
        jobManagementService.handleUpdateJobStatusRequest(UpdateJobStatusRequest
                                                              .newBuilder()
                                                              .setTaskId(uuid2)
                                                              .setStatus(status)
                                                              .setRuntime(runtime)
                                                              .setResult(result)
                                                              .build(), dbId);

        // assert if updated job info and job result are correct
        // test read job status by job id
        GetJobStatusRequest utReadJobByIdRequest =
            GetJobStatusRequest.newBuilder().setTaskId(uuid2).build();
        JobManagementResponse readJobByIdResp =
            jobManagementService.handleGetJobStatusRequest(utReadJobByIdRequest, dbId);
        jobList = readJobByIdResp.getGetJobStatusResponse().getJobList();
        assertEquals(1, jobList.size());
        Job job2 = jobList.get(0);
        assertEquals(job2.getDbId(), dbId);
        assertEquals(job2.getJobId(), 2);
        assertEquals(job2.getTaskId(), uuid2);
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
        JobManagementResponse readAlgoResultResp = jobManagementService.handleGetAlgoResultRequest(GetAlgoResultRequest
                                                                                                       .newBuilder()
                                                                                                       .setTaskId(uuid2)
                                                                                                       .build(), dbId);
        AlgoResult AlgoResult = readAlgoResultResp.getGetAlgoResultResponse().getAlgoResult();
        assertEquals(AlgoResult.getTaskId(), uuid2);
        assertEquals(AlgoResult.getResult(), result);

        // test read job result error response
        GetAlgoResultRequest utErrGetAlgoResultRequest = GetAlgoResultRequest.newBuilder().setTaskId("").build();
        JobManagementResponse errReadAlgoResultResp =
            jobManagementService.handleGetAlgoResultRequest(utErrGetAlgoResultRequest, dbId);
        assertEquals(errReadAlgoResultResp.getResponseCode(), ResponseCode.FAILED);

        // test delete job
        jobManagementService.handleDeleteJobRequest(DeleteJobRequest
                                                        .newBuilder()
                                                        .setTaskId(uuid1)
                                                        .build(), dbId);
        // assert if the job has been deleted
        readJobResp =
            jobManagementService.handleGetJobStatusRequest(GetJobStatusRequest.newBuilder().build(), dbId);
        jobList = readJobResp.getGetJobStatusResponse().getJobList();
        assertEquals(1, jobList.size());
        assertEquals(uuid2, jobList.get(0).getTaskId());
    }

    @Test
    @Transactional
    @Order(1)
    void testHeartbeatService() {
        log.info("start testing heartbeat service.");

        String reqMsg = "This is a heartbeat request message.";
        int heartbeatCount = 0;

        HeartbeatResponse heartbeatResponse =
            jobManagementService.detectHeartbeat(HeartbeatRequest
                                                     .newBuilder()
                                                     .setRequestMsg(reqMsg)
                                                     .setHeartbeatCount(heartbeatCount)
                                                     .build());

        assertEquals(heartbeatCount, heartbeatResponse.getHeartbeatCount());
    }

}
