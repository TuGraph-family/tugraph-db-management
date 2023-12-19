package com.antgroup.tugraph.serviceImpl;

import com.antgroup.tugraph.model.*;
import com.antgroup.tugraph.service.JobManagementService;
import com.antgroup.tugraph.service.JobService;
import com.baidu.cloud.starlight.springcloud.server.annotation.RpcService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

import lgraph.management.TuGraphDBManagement;

@RpcService(serviceId = "lgraph.management.JobManagementService")
@Slf4j
public class JobManagementServiceImpl implements JobManagementService {
    @Autowired
    private JobService jobService;

    @Override
    public TuGraphDBManagement.HeartbeatResponse detectHeartbeat(TuGraphDBManagement.HeartbeatRequest request) {
        log.info("[Heartbeat Request] Message: " + request.getRequestMsg() + ", Count: "
                     + request.getHeartbeatCount());
        return TuGraphDBManagement.HeartbeatResponse.newBuilder()
                                                    .setResponseMsg("This is a heartbeat response message.")
                                                    .setHeartbeatCount(request.getHeartbeatCount())
                                                    .build();
    }

    @Override
    public TuGraphDBManagement.JobManagementResponse handleRequest(TuGraphDBManagement.JobManagementRequest request) {
        String dbId = request.getDbHost() + ":" + request.getDbPort();
        switch (request.getReqCase()) {
            case CREATE_JOB_REQUEST:
                return handleCreateJobRequest(request.getCreateJobRequest(), dbId);
            case GET_JOB_STATUS_REQUEST:
                return handleGetJobStatusRequest(request.getGetJobStatusRequest(), dbId);
            case GET_ALGO_RESULT_REQUEST:
                return handleGetAlgoResultRequest(request.getGetAlgoResultRequest(), dbId);
            case UPDATE_JOB_STATUS_REQUEST:
                return handleUpdateJobStatusRequest(request.getUpdateJobStatusRequest(), dbId);
            case DELETE_JOB_REQUEST:
                return handleDeleteJobRequest(request.getDeleteJobRequest(), dbId);
            default:
                return TuGraphDBManagement.JobManagementResponse.newBuilder().setResponseCode(
                    TuGraphDBManagement.ResponseCode.FAILED).setMessage("Unrecognized request type").build();
        }
    }

    public TuGraphDBManagement.JobManagementResponse handleCreateJobRequest(
        TuGraphDBManagement.CreateJobRequest request, String dbId) {
        log.info("[CreateJob] Request call from db: " + dbId);

        TuGraphDBManagement.JobManagementResponse.Builder respBuilder =
            TuGraphDBManagement.JobManagementResponse.newBuilder();
        try {
            Job Job = new Job()
                .setDbId(dbId)
                .setTaskId(request.getTaskId())
                .setTaskName(request.getTaskName())
                .setStartTime(request.getStartTime())
                .setPeriod(request.getPeriod())
                .setProcedureName(request.getProcedureName())
                .setProcedureType(request.getProcedureType())
                .setUser(request.getUser())
                .setCreateTime(request.getCreateTime());
            int jobId = jobService.create(Job);
            respBuilder.setCreateJobResponse(
                TuGraphDBManagement.CreateJobResponse.newBuilder().setJobId(jobId).build());
            respBuilder.setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS);
            respBuilder.setMessage("[CreateJob] Succeed");
        } catch (Exception e) {
            String errorMessage = "[CreateJob] Respond Error. Reason: " + e.getMessage();
            log.info(errorMessage);
            respBuilder.setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
            respBuilder.setMessage(e.getMessage());
        }

        return respBuilder.build();
    }

    public TuGraphDBManagement.JobManagementResponse handleGetJobStatusRequest(
        TuGraphDBManagement.GetJobStatusRequest request, String dbId) {
        log.info("[GetJobStatus] Request call from db: " + dbId);

        TuGraphDBManagement.JobManagementResponse.Builder respBuilder =
            TuGraphDBManagement.JobManagementResponse.newBuilder();
        try {
            TuGraphDBManagement.GetJobStatusResponse.Builder jobStatusBuilder =
                TuGraphDBManagement.GetJobStatusResponse.newBuilder();
            if (request.hasTaskId()) {
                Job tempJob = jobService.getStatusById(request.getTaskId());
                TuGraphDBManagement.Job Job = TuGraphDBManagement.Job.newBuilder()
                                                                     .setJobId(tempJob.getJobId())
                                                                     .setTaskName(tempJob.getTaskName())
                                                                     .setTaskId(tempJob.getTaskId())
                                                                     .setDbId(tempJob.getDbId())
                                                                     .setStartTime(tempJob.getStartTime())
                                                                     .setPeriod(tempJob.getPeriod())
                                                                     .setProcedureName(tempJob.getProcedureName())
                                                                     .setProcedureType(tempJob.getProcedureType())
                                                                     .setStatus(tempJob.getStatus())
                                                                     .setRuntime(tempJob.getRuntime())
                                                                     .setUser(tempJob.getUser())
                                                                     .setCreateTime(tempJob.getCreateTime())
                                                                     .build();
                jobStatusBuilder.addJob(Job);
            } else {
                List<Job> tempJobList = jobService.listStatus();
                for (Job tempJob : tempJobList) {
                    TuGraphDBManagement.Job Job = TuGraphDBManagement.Job.newBuilder()
                                                                         .setTaskId(tempJob.getTaskId())
                                                                         .setTaskName(tempJob.getTaskName())
                                                                         .setDbId(tempJob.getDbId())
                                                                         .setJobId(tempJob.getJobId())
                                                                         .setStartTime(tempJob.getStartTime())
                                                                         .setPeriod(tempJob.getPeriod())
                                                                         .setProcedureName(tempJob.getProcedureName())
                                                                         .setProcedureType(tempJob.getProcedureType())
                                                                         .setStatus(tempJob.getStatus())
                                                                         .setRuntime(tempJob.getRuntime())
                                                                         .setUser(tempJob.getUser())
                                                                         .setCreateTime(tempJob.getCreateTime())
                                                                         .build();
                    jobStatusBuilder.addJob(Job);
                }
            }
            log.info("[GetJobStatus] Respond with " + jobStatusBuilder.getJobCount() + " jobs");
            respBuilder.setGetJobStatusResponse(jobStatusBuilder.build());
            respBuilder.setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS);
            respBuilder.setMessage("[GetJobStatus] Succeed");
        } catch (Exception e) {
            String errorMessage = "[GetJobStatus] Respond Error. Reason: " + e.getMessage();
            log.info(errorMessage);
            respBuilder.setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
            respBuilder.setMessage(errorMessage);
        }

        return respBuilder.build();
    }

    public TuGraphDBManagement.JobManagementResponse handleGetAlgoResultRequest(
        TuGraphDBManagement.GetAlgoResultRequest request, String dbId) {
        log.info("[GetAlgoResult] Request call from db: " + dbId);

        TuGraphDBManagement.JobManagementResponse.Builder respBuilder =
            TuGraphDBManagement.JobManagementResponse.newBuilder();
        try {
            AlgoResult tempResult = jobService.getResultById(request.getTaskId());
            TuGraphDBManagement.AlgoResult AlgoResult = TuGraphDBManagement.AlgoResult
                .newBuilder()
                .setTaskId(tempResult.getTaskId())
                .setResult(tempResult.getResult())
                .build();
            respBuilder.setGetAlgoResultResponse(
                TuGraphDBManagement.GetAlgoResultResponse.newBuilder().setAlgoResult(AlgoResult).build());
            respBuilder.setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS);
            respBuilder.setMessage("[GetAlgoResult] Succeed");
        } catch (Exception e) {
            String errorMessage = "[GetAlgoResult] Respond Error. Reason: " + e.getMessage();
            log.info(errorMessage);
            respBuilder.setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
            respBuilder.setMessage(errorMessage);
        }

        return respBuilder.build();
    }

    public TuGraphDBManagement.JobManagementResponse handleUpdateJobStatusRequest(
        TuGraphDBManagement.UpdateJobStatusRequest request, String dbId) {
        log.info("[UpdateJobStatus] Request call from db: " + dbId);

        TuGraphDBManagement.JobManagementResponse.Builder respBuilder =
            TuGraphDBManagement.JobManagementResponse.newBuilder();
        try {
            Job Job = new Job().setTaskId(request.getTaskId())
                               .setStatus(request.getStatus())
                               .setRuntime(request.getRuntime());
            AlgoResult AlgoResult = new AlgoResult()
                .setTaskId(request.getTaskId())
                .setResult(request.getResult());
            jobService.update(Job, AlgoResult);
            respBuilder.setUpdateJobStatusResponse(TuGraphDBManagement.UpdateJobStatusResponse.newBuilder().build());
            respBuilder.setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS);
            respBuilder.setMessage("[UpdateJobStatus] Succeed");
        } catch (Exception e) {
            String errorMessage = "[UpdateJobStatus] Respond Error. Reason: " + e.getMessage();
            log.info(errorMessage);
            respBuilder.setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
            respBuilder.setMessage(errorMessage);
        }

        return respBuilder.build();
    }

    public TuGraphDBManagement.JobManagementResponse handleDeleteJobRequest(
        TuGraphDBManagement.DeleteJobRequest request, String dbId) {
        log.info("[DeleteJob] Request call from db: " + dbId);

        TuGraphDBManagement.JobManagementResponse.Builder respBuilder =
            TuGraphDBManagement.JobManagementResponse.newBuilder();

        try {
            jobService.delete(request.getTaskId());
            respBuilder.setDeleteJobResponse(TuGraphDBManagement.DeleteJobResponse.newBuilder().build());
            respBuilder.setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS);
            respBuilder.setMessage("[DeleteJob] Succeed");
        } catch (Exception e) {
            String errorMessage = "[DeleteJob] Respond Error. Reason: " + e.getMessage();
            log.info(errorMessage);
            respBuilder.setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
            respBuilder.setMessage(errorMessage);
        }

        return respBuilder.build();
    }
}
