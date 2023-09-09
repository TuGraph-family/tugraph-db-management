package com.antgroup.tugraph;

import com.baidu.cloud.starlight.springcloud.server.annotation.RpcService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RpcService
@Slf4j
public class JobManagementServiceImpl implements JobManagementService {
    @Autowired
    private JobService jobService;

    @Override
    public TugraphManagement.JobManagementResponse handleRequest(TugraphManagement.JobManagementRequest request) {
        String dbId = request.getDbHost() + ":" + request.getDbPort();
        TugraphManagement.JobManagementResponse.Builder respBuilder =
            TugraphManagement.JobManagementResponse
                .newBuilder();

        switch (request.getReqCase()) {
            case CREATE_JOB_REQUEST:
                TugraphManagement.CreateJobResponse createJobResp =
                    handleCreateJobRequest(request.getCreateJobRequest(), dbId);
                respBuilder
                    .setCreateJobResponse(createJobResp);
                if (createJobResp.getResponseCode() == TugraphManagement.ResponseCode.SUCCESS) {
                    respBuilder
                        .setResponseCode(TugraphManagement.ResponseCode.SUCCESS);
                } else {
                    respBuilder
                        .setResponseCode(TugraphManagement.ResponseCode.FAILED);
                }
            break;
            case READ_JOB_STATUS_REQUEST:
                TugraphManagement.ReadJobStatusResponse readJobStatusResp =
                    handleReadJobStatusRequest(request.getReadJobStatusRequest(), dbId);
                respBuilder
                    .setReadJobStatusResponse(readJobStatusResp);
                if (readJobStatusResp.getResponseCode() == TugraphManagement.ResponseCode.SUCCESS) {
                    respBuilder
                        .setResponseCode(TugraphManagement.ResponseCode.SUCCESS);
                } else {
                    respBuilder
                        .setResponseCode(TugraphManagement.ResponseCode.FAILED);
                }
            break;
            case READ_JOB_RESULT_REQUEST:
                TugraphManagement.ReadJobResultResponse readJobResultResp =
                    handleReadJobResultRequest(request.getReadJobResultRequest(), dbId);
                respBuilder
                    .setReadJobResultResponse(readJobResultResp);
                if (readJobResultResp.getResponseCode() == TugraphManagement.ResponseCode.SUCCESS) {
                    respBuilder
                        .setResponseCode(TugraphManagement.ResponseCode.SUCCESS);
                } else {
                    respBuilder
                        .setResponseCode(TugraphManagement.ResponseCode.FAILED);
                }
            break;
            case UPDATE_JOB_REQUEST:
                TugraphManagement.UpdateJobResponse updateJobResp =
                    handleUpdateJobRequest(request.getUpdateJobRequest(), dbId);
                respBuilder
                    .setUpdateJobResponse(updateJobResp);
                if (updateJobResp.getResponseCode() == TugraphManagement.ResponseCode.SUCCESS) {
                    respBuilder
                        .setResponseCode(TugraphManagement.ResponseCode.SUCCESS);
                } else {
                    respBuilder
                        .setResponseCode(TugraphManagement.ResponseCode.FAILED);
                }
            break;
            case DELETE_JOB_REQUEST:
                TugraphManagement.DeleteJobResponse deleteJobResp =
                    handleDeleteJobRequest(request.getDeleteJobRequest(), dbId);
                respBuilder
                    .setDeleteJobResponse(deleteJobResp);
                if (deleteJobResp.getResponseCode() == TugraphManagement.ResponseCode.SUCCESS) {
                    respBuilder
                        .setResponseCode(TugraphManagement.ResponseCode.SUCCESS);
                } else {
                    respBuilder
                        .setResponseCode(TugraphManagement.ResponseCode.FAILED);
                }
            break;
            default:
                respBuilder
                    .setResponseCode(TugraphManagement.ResponseCode.FAILED);
        }

        return respBuilder.build();
    }

    public TugraphManagement.CreateJobResponse handleCreateJobRequest(TugraphManagement.CreateJobRequest request, String dbId) {
        log.info("create request db_id = " + dbId);

        TugraphManagement.CreateJobResponse.Builder respBuilder =
            TugraphManagement.CreateJobResponse
                .newBuilder();
        TugraphManagement.CreateJobResponse resp;
        try {
            JobStatus jobStatus =
                new JobStatus()
                    .setDbId(dbId)
                    .setStartTime(request.getStartTime())
                    .setPeriod(request.getPeriod())
                    .setProcedureName(request.getProcedureName())
                    .setProcedureType(request.getProcedureType())
                    .setUser(request.getUser())
                    .setCreateTime(request.getCreateTime());
            int jobId = jobService.create(jobStatus);
            resp = respBuilder
                    .setJobId(jobId)
                    .setResponseCode(TugraphManagement.ResponseCode.SUCCESS)
                    .build();
        } catch (Exception e) {
            resp = respBuilder
                    .setJobId(-1)
                    .setResponseCode(TugraphManagement.ResponseCode.FAILED)
                    .build();
        }

        return resp;
    }

    public TugraphManagement.ReadJobStatusResponse handleReadJobStatusRequest(TugraphManagement.ReadJobStatusRequest request, String dbId) {
        log.info("read status request db_id = " + dbId);

        TugraphManagement.ReadJobStatusResponse.Builder respBuilder =
            TugraphManagement.ReadJobStatusResponse
                .newBuilder();
        TugraphManagement.ReadJobStatusResponse resp;
        try {
            List<JobStatus> tempJobStatusList = jobService.listStatus();
            for (JobStatus tempJobStatus: tempJobStatusList) {
                TugraphManagement.JobStatus jobStatus =
                    TugraphManagement.JobStatus.newBuilder()
                        .setDbId(tempJobStatus.getDbId())
                        .setJobId(tempJobStatus.getJobId())
                        .setStartTime(tempJobStatus.getStartTime())
                        .setPeriod(tempJobStatus.getPeriod())
                        .setProcedureName(tempJobStatus.getProcedureName())
                        .setProcedureType(tempJobStatus.getProcedureType())
                        .setStatus(tempJobStatus.getStatus())
                        .setRuntime(tempJobStatus.getRuntime())
                        .setUser(tempJobStatus.getUser())
                        .setCreateTime(tempJobStatus.getCreateTime())
                        .build();
                respBuilder.addJobStatus(jobStatus);
            }
            resp = respBuilder
                    .setResponseCode(TugraphManagement.ResponseCode.SUCCESS)
                    .build();
        } catch (Exception e) {
            resp = respBuilder
                    .setResponseCode(TugraphManagement.ResponseCode.FAILED)
                    .build();
        }

        return resp;
    }

    public TugraphManagement.ReadJobResultResponse handleReadJobResultRequest(TugraphManagement.ReadJobResultRequest request, String dbId) {
        log.info("read result request db_id = " + dbId);

        TugraphManagement.ReadJobResultResponse.Builder respBuilder =
            TugraphManagement.ReadJobResultResponse
                .newBuilder();
        TugraphManagement.ReadJobResultResponse resp;
        try {
            JobResult tempResult = jobService.getResultById(request.getJobId());
            TugraphManagement.JobResult jobResult =
                TugraphManagement.JobResult
                    .newBuilder()
                    .setJobId(tempResult.getJobId())
                    .setResult(tempResult.getResult())
                    .build();
            resp = respBuilder
                    .setJobResult(jobResult)
                    .setResponseCode(TugraphManagement.ResponseCode.SUCCESS)
                    .build();
        } catch (Exception e) {
            TugraphManagement.JobResult emptyJobResult =
                TugraphManagement.JobResult
                    .newBuilder()
                    .setJobId(-1)
                    .setResult("")
                    .build();
            resp = respBuilder
                    .setJobResult(emptyJobResult)
                    .setResponseCode(TugraphManagement.ResponseCode.FAILED)
                    .build();
        }

        return resp;
    }

    public TugraphManagement.UpdateJobResponse handleUpdateJobRequest(TugraphManagement.UpdateJobRequest request, String dbId) {
        log.info("update request db_id = " + dbId);

        TugraphManagement.UpdateJobResponse.Builder respBuilder =
            TugraphManagement.UpdateJobResponse
                .newBuilder();
        TugraphManagement.UpdateJobResponse resp;
        try {
            JobStatus jobStatus =
                new JobStatus()
                    .setJobId(request.getJobId())
                    .setStatus(request.getStatus())
                    .setRuntime(request.getRuntime());
            JobResult jobResult =
                new JobResult()
                    .setJobId(request.getJobId())
                    .setResult(request.getResult());
            jobService.update(jobStatus, jobResult);
            resp = respBuilder
                    .setResponseCode(TugraphManagement.ResponseCode.SUCCESS)
                    .build();
        } catch (Exception e) {
            resp = respBuilder
                    .setResponseCode(TugraphManagement.ResponseCode.FAILED)
                    .build();
        }

        return resp;
    }

    public TugraphManagement.DeleteJobResponse handleDeleteJobRequest(TugraphManagement.DeleteJobRequest request, String dbId) {
        log.info("delete request db_id = " + dbId);

        TugraphManagement.DeleteJobResponse.Builder respBuilder =
            TugraphManagement.DeleteJobResponse
                .newBuilder();
        TugraphManagement.DeleteJobResponse resp;
        try {
            jobService.delete(request.getJobId());
            resp = respBuilder
                    .setResponseCode(TugraphManagement.ResponseCode.SUCCESS)
                    .build();
        } catch (Exception e) {
            resp = respBuilder
                    .setResponseCode(TugraphManagement.ResponseCode.FAILED)
                    .build();
        }

        return resp;
    }
}
