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
        log.info("Received a heartbeat request. Message: " + request.getRequestMsg() + ", Count: "
                     + request.getHeartbeatCount());
        return TuGraphDBManagement.HeartbeatResponse.newBuilder()
                                                    .setResponseMsg("This is a heartbeat response message.")
                                                    .setHeartbeatCount(request.getHeartbeatCount())
                                                    .build();
    }

    @Override
    public TuGraphDBManagement.JobManagementResponse handleRequest(TuGraphDBManagement.JobManagementRequest request) {
        String dbId = request.getDbHost() + ":" + request.getDbPort();
        TuGraphDBManagement.JobManagementResponse.Builder respBuilder =
            TuGraphDBManagement.JobManagementResponse
                .newBuilder();

        switch (request.getReqCase()) {
            case CREATE_JOB_REQUEST:
                TuGraphDBManagement.CreateJobResponse createJobResp =
                    handleCreateJobRequest(request.getCreateJobRequest(), dbId);
                respBuilder
                    .setCreateJobResponse(createJobResp);
                if (createJobResp.getResponseCode() == TuGraphDBManagement.ResponseCode.SUCCESS) {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS);
                } else {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
                }
                break;
            case GET_JOB_STATUS_REQUEST:
                TuGraphDBManagement.GetJobStatusResponse readJobResp =
                    handleGetJobStatusRequest(request.getGetJobStatusRequest(), dbId);
                respBuilder
                    .setGetJobStatusResponse(readJobResp);
                if (readJobResp.getResponseCode() == TuGraphDBManagement.ResponseCode.SUCCESS) {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS);
                } else {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
                }
                break;
            case GET_ALGO_RESULT_REQUEST:
                TuGraphDBManagement.GetAlgoResultResponse readAlgoResultResp =
                    handleGetAlgoResultRequest(request.getGetAlgoResultRequest(), dbId);
                respBuilder
                    .setGetAlgoResultResponse(readAlgoResultResp);
                if (readAlgoResultResp.getResponseCode() == TuGraphDBManagement.ResponseCode.SUCCESS) {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS);
                } else {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
                }
                break;
            case UPDATE_JOB_STATUS_REQUEST:
                TuGraphDBManagement.UpdateJobStatusResponse updateJobResp =
                    handleUpdateJobStatusRequest(request.getUpdateJobStatusRequest(), dbId);
                respBuilder
                    .setUpdateJobStatusResponse(updateJobResp);
                if (updateJobResp.getResponseCode() == TuGraphDBManagement.ResponseCode.SUCCESS) {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS);
                } else {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
                }
                break;
            case DELETE_JOB_REQUEST:
                TuGraphDBManagement.DeleteJobResponse deleteJobResp =
                    handleDeleteJobRequest(request.getDeleteJobRequest(), dbId);
                respBuilder
                    .setDeleteJobResponse(deleteJobResp);
                if (deleteJobResp.getResponseCode() == TuGraphDBManagement.ResponseCode.SUCCESS) {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS);
                } else {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
                }
                break;
            default:
                respBuilder
                    .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
        }

        return respBuilder.build();
    }

    public TuGraphDBManagement.CreateJobResponse handleCreateJobRequest(TuGraphDBManagement.CreateJobRequest request,
                                                                        String dbId) {
        log.info("create request db_id = " + dbId);

        TuGraphDBManagement.CreateJobResponse.Builder respBuilder = TuGraphDBManagement.CreateJobResponse.newBuilder();
        TuGraphDBManagement.CreateJobResponse resp;
        try {
            Job Job =
                new Job()
                    .setDbId(dbId)
                    .setTaskId(request.getTaskId())
                    .setStartTime(request.getStartTime())
                    .setPeriod(request.getPeriod())
                    .setProcedureName(request.getProcedureName())
                    .setProcedureType(request.getProcedureType())
                    .setUser(request.getUser())
                    .setCreateTime(request.getCreateTime());
            int jobId = jobService.create(Job);
            resp = respBuilder
                .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS)
                .build();
        } catch (Exception e) {
            resp = respBuilder
                .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED)
                .build();
        }

        return resp;
    }

    public TuGraphDBManagement.GetJobStatusResponse handleGetJobStatusRequest(
        TuGraphDBManagement.GetJobStatusRequest request, String dbId) {
        log.info("read status request db_id = " + dbId);

        TuGraphDBManagement.GetJobStatusResponse.Builder respBuilder =
            TuGraphDBManagement.GetJobStatusResponse
                .newBuilder();
        TuGraphDBManagement.GetJobStatusResponse resp;
        if (request.hasTaskId()) {
            try {
                Job tempJob = jobService.getStatusById(request.getTaskId());
                TuGraphDBManagement.Job Job = TuGraphDBManagement.Job.newBuilder()
                                                                     .setJobId(tempJob.getJobId())
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
                respBuilder.addJob(Job);
                resp = respBuilder
                    .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS)
                    .build();
            } catch (Exception e) {
                resp = respBuilder
                    .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED)
                    .build();
            }
        } else {
            try {
                List<Job> tempJobList = jobService.listStatus();
                for (Job tempJob : tempJobList) {
                    TuGraphDBManagement.Job Job =
                        TuGraphDBManagement.Job.newBuilder()
                                               .setTaskId(tempJob.getTaskId())
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
                    respBuilder.addJob(Job);
                }
                resp = respBuilder
                    .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS)
                    .build();
            } catch (Exception e) {
                resp = respBuilder
                    .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED)
                    .build();
            }
        }

        return resp;
    }

    public TuGraphDBManagement.GetAlgoResultResponse handleGetAlgoResultRequest(
        TuGraphDBManagement.GetAlgoResultRequest request, String dbId) {
        log.info("read result request db_id = " + dbId);

        TuGraphDBManagement.GetAlgoResultResponse.Builder respBuilder =
            TuGraphDBManagement.GetAlgoResultResponse.newBuilder();
        TuGraphDBManagement.GetAlgoResultResponse resp;
        try {
            AlgoResult tempResult = jobService.getResultById(request.getTaskId());
            TuGraphDBManagement.AlgoResult AlgoResult = TuGraphDBManagement.AlgoResult
                .newBuilder()
                .setTaskId(tempResult.getTaskId())
                .setResult(tempResult.getResult())
                .build();
            resp = respBuilder.setAlgoResult(AlgoResult)
                              .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS)
                              .build();
        } catch (Exception e) {
            TuGraphDBManagement.AlgoResult emptyAlgoResult =
                TuGraphDBManagement.AlgoResult.newBuilder().setTaskId("").setResult("").build();
            resp = respBuilder
                .setAlgoResult(emptyAlgoResult)
                .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED)
                .build();
        }

        return resp;
    }

    public TuGraphDBManagement.UpdateJobStatusResponse handleUpdateJobStatusRequest(
        TuGraphDBManagement.UpdateJobStatusRequest request, String dbId) {
        log.info("update request db_id = " + dbId);

        TuGraphDBManagement.UpdateJobStatusResponse.Builder respBuilder =
            TuGraphDBManagement.UpdateJobStatusResponse.newBuilder();
        TuGraphDBManagement.UpdateJobStatusResponse resp;
        try {
            Job Job = new Job().setTaskId(request.getTaskId())
                               .setStatus(request.getStatus())
                               .setRuntime(request.getRuntime());
            AlgoResult AlgoResult = new AlgoResult()
                .setTaskId(request.getTaskId())
                .setResult(request.getResult());
            jobService.update(Job, AlgoResult);
            resp = respBuilder
                .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS)
                .build();
        } catch (Exception e) {
            resp = respBuilder
                .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED)
                .build();
        }

        return resp;
    }

    public TuGraphDBManagement.DeleteJobResponse handleDeleteJobRequest(TuGraphDBManagement.DeleteJobRequest request,
                                                                        String dbId) {
        log.info("delete request db_id = " + dbId);

        TuGraphDBManagement.DeleteJobResponse.Builder respBuilder = TuGraphDBManagement.DeleteJobResponse.newBuilder();
        TuGraphDBManagement.DeleteJobResponse resp;
        try {
            jobService.delete(request.getTaskId());
            resp = respBuilder
                .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS)
                .build();
        } catch (Exception e) {
            resp = respBuilder
                .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED)
                .build();
        }

        return resp;
    }
}
