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
                    .setCreateJobResponse(createJobResp)
                    .setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.SUCCESS);
            break;
            case READ_JOB_REQUEST:
                TugraphManagement.ReadJobResponse readJobResp =
                    handleReadJobRequest(request.getReadJobRequest(), dbId);
                respBuilder
                    .setReadJobResponse(readJobResp)
                    .setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.SUCCESS);
            break;
            case UPDATE_JOB_REQUEST:
                TugraphManagement.UpdateJobResponse updateJobResp =
                    handleUpdateJobRequest(request.getUpdateJobRequest(), dbId);
                respBuilder
                    .setUpdateJobResponse(updateJobResp)
                    .setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.SUCCESS);
            break;
            case DELETE_JOB_REQUEST:
                TugraphManagement.DeleteJobResponse deleteJobResp =
                    handleDeleteJobRequest(request.getDeleteJobRequest(), dbId);
                respBuilder
                    .setDeleteJobResponse(deleteJobResp)
                    .setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.SUCCESS);
            break;
            default:
                respBuilder
                    .setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.FAILED);
        }

        return respBuilder.build();
    }

    public TugraphManagement.CreateJobResponse handleCreateJobRequest(TugraphManagement.CreateJobRequest request, String dbId) {
        log.info("create request db_id = " + dbId);

        JobStatus jobStatus =
            new JobStatus()
                .setDbId(dbId)
                .setStartTime(request.getStartTime())
                .setPeriod(request.getPeriod())
                .setProcedureName(request.getProcedureName())
                .setProcedureType(request.getProcedureType())
                .setCreator(request.getCreator())
                .setCreateTime(request.getCreateTime());
        int jobId = jobService.create(jobStatus);
        TugraphManagement.CreateJobResponse resp =
            TugraphManagement.CreateJobResponse.newBuilder()
                .setJobId(jobId)
                .build();

        return resp;
    }

    public TugraphManagement.ReadJobResponse handleReadJobRequest(TugraphManagement.ReadJobRequest request, String dbId) {
        log.info("read request db_id = " + dbId);

        TugraphManagement.ReadJobResponse.Builder respBuilder = TugraphManagement.ReadJobResponse.newBuilder();
        TugraphManagement.ReadJobRequest.ReadType readType = request.getReadType();
        if (readType == TugraphManagement.ReadJobRequest.ReadType.READ_ALL) {
            log.info("read all job status");
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
                        .setCreator(tempJobStatus.getCreator())
                        .setCreateTime(tempJobStatus.getCreateTime())
                        .build();
                respBuilder.addJobStatus(jobStatus);
            }
        } else if (readType == TugraphManagement.ReadJobRequest.ReadType.READ_RESULT) {
            JobResult tempResult = jobService.getResultById(request.getJobId());
            TugraphManagement.JobResult jobResult = TugraphManagement.JobResult.newBuilder()
                                                    .setJobId(tempResult.getJobId())
                                                    .setResult(tempResult.getResult())
                                                    .build();
            respBuilder.setJobResult(jobResult);
        }
        TugraphManagement.ReadJobResponse resp = respBuilder.build();

        return resp;
    }

    public TugraphManagement.UpdateJobResponse handleUpdateJobRequest(TugraphManagement.UpdateJobRequest request, String dbId) {
        log.info("update request db_id = " + dbId);

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
        TugraphManagement.UpdateJobResponse resp =
            TugraphManagement.UpdateJobResponse
                .newBuilder()
                .build();

        return resp;
    }

    public TugraphManagement.DeleteJobResponse handleDeleteJobRequest(TugraphManagement.DeleteJobRequest request, String dbId) {
        log.info("delete request db_id = " + dbId);

        jobService.delete(request.getJobId());
        TugraphManagement.DeleteJobResponse resp =
            TugraphManagement.DeleteJobResponse
                .newBuilder()
                .build();

        return resp;
    }
}
