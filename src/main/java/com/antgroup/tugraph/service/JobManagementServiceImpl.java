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
            case READ_JOB_STATUS_REQUEST:
                TuGraphDBManagement.ReadJobStatusResponse readJobStatusResp =
                    handleReadJobStatusRequest(request.getReadJobStatusRequest(), dbId);
                respBuilder
                    .setReadJobStatusResponse(readJobStatusResp);
                if (readJobStatusResp.getResponseCode() == TuGraphDBManagement.ResponseCode.SUCCESS) {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS);
                } else {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
                }
            break;
            case READ_JOB_RESULT_REQUEST:
                TuGraphDBManagement.ReadJobResultResponse readJobResultResp =
                    handleReadJobResultRequest(request.getReadJobResultRequest(), dbId);
                respBuilder
                    .setReadJobResultResponse(readJobResultResp);
                if (readJobResultResp.getResponseCode() == TuGraphDBManagement.ResponseCode.SUCCESS) {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS);
                } else {
                    respBuilder
                        .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED);
                }
            break;
            case UPDATE_JOB_REQUEST:
                TuGraphDBManagement.UpdateJobResponse updateJobResp =
                    handleUpdateJobRequest(request.getUpdateJobRequest(), dbId);
                respBuilder
                    .setUpdateJobResponse(updateJobResp);
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

    public TuGraphDBManagement.CreateJobResponse handleCreateJobRequest(TuGraphDBManagement.CreateJobRequest request, String dbId) {
        log.info("create request db_id = " + dbId);

        TuGraphDBManagement.CreateJobResponse.Builder respBuilder =
            TuGraphDBManagement.CreateJobResponse
                .newBuilder();
        TuGraphDBManagement.CreateJobResponse resp;
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
                    .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS)
                    .build();
        } catch (Exception e) {
            resp = respBuilder
                    .setJobId(-1)
                    .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED)
                    .build();
        }

        return resp;
    }

    public TuGraphDBManagement.ReadJobStatusResponse handleReadJobStatusRequest(TuGraphDBManagement.ReadJobStatusRequest request, String dbId) {
        log.info("read status request db_id = " + dbId);

        TuGraphDBManagement.ReadJobStatusResponse.Builder respBuilder =
            TuGraphDBManagement.ReadJobStatusResponse
                .newBuilder();
        TuGraphDBManagement.ReadJobStatusResponse resp;
        try {
            List<JobStatus> tempJobStatusList = jobService.listStatus();
            for (JobStatus tempJobStatus: tempJobStatusList) {
                TuGraphDBManagement.JobStatus jobStatus =
                    TuGraphDBManagement.JobStatus.newBuilder()
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
                    .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS)
                    .build();
        } catch (Exception e) {
            resp = respBuilder
                    .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED)
                    .build();
        }

        return resp;
    }

    public TuGraphDBManagement.ReadJobResultResponse handleReadJobResultRequest(TuGraphDBManagement.ReadJobResultRequest request, String dbId) {
        log.info("read result request db_id = " + dbId);

        TuGraphDBManagement.ReadJobResultResponse.Builder respBuilder =
            TuGraphDBManagement.ReadJobResultResponse
                .newBuilder();
        TuGraphDBManagement.ReadJobResultResponse resp;
        try {
            JobResult tempResult = jobService.getResultById(request.getJobId());
            TuGraphDBManagement.JobResult jobResult =
                TuGraphDBManagement.JobResult
                    .newBuilder()
                    .setJobId(tempResult.getJobId())
                    .setResult(tempResult.getResult())
                    .build();
            resp = respBuilder
                    .setJobResult(jobResult)
                    .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS)
                    .build();
        } catch (Exception e) {
            TuGraphDBManagement.JobResult emptyJobResult =
                TuGraphDBManagement.JobResult
                    .newBuilder()
                    .setJobId(-1)
                    .setResult("")
                    .build();
            resp = respBuilder
                    .setJobResult(emptyJobResult)
                    .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED)
                    .build();
        }

        return resp;
    }

    public TuGraphDBManagement.UpdateJobResponse handleUpdateJobRequest(TuGraphDBManagement.UpdateJobRequest request, String dbId) {
        log.info("update request db_id = " + dbId);

        TuGraphDBManagement.UpdateJobResponse.Builder respBuilder =
            TuGraphDBManagement.UpdateJobResponse
                .newBuilder();
        TuGraphDBManagement.UpdateJobResponse resp;
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
                    .setResponseCode(TuGraphDBManagement.ResponseCode.SUCCESS)
                    .build();
        } catch (Exception e) {
            resp = respBuilder
                    .setResponseCode(TuGraphDBManagement.ResponseCode.FAILED)
                    .build();
        }

        return resp;
    }

    public TuGraphDBManagement.DeleteJobResponse handleDeleteJobRequest(TuGraphDBManagement.DeleteJobRequest request, String dbId) {
        log.info("delete request db_id = " + dbId);

        TuGraphDBManagement.DeleteJobResponse.Builder respBuilder =
            TuGraphDBManagement.DeleteJobResponse
                .newBuilder();
        TuGraphDBManagement.DeleteJobResponse resp;
        try {
            jobService.delete(request.getJobId());
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
