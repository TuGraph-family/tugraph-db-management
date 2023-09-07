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
                    .setResponseCode(TugraphManagement.ResponseCode.SUCCESS);
            break;
            case READ_JOB_STATUS_REQUEST:
                TugraphManagement.ReadJobStatusResponse readJobStatusResp =
                    handleReadJobStatusRequest(request.getReadJobStatusRequest(), dbId);
                respBuilder
                    .setReadJobStatusResponse(readJobStatusResp)
                    .setResponseCode(TugraphManagement.ResponseCode.SUCCESS);
            break;
            case READ_JOB_RESULT_REQUEST:
                TugraphManagement.ReadJobResultResponse readJobResultResp =
                    handleReadJobResultRequest(request.getReadJobResultRequest(), dbId);
                respBuilder
                    .setReadJobResultResponse(readJobResultResp)
                    .setResponseCode(TugraphManagement.ResponseCode.SUCCESS);
            break;
            case UPDATE_JOB_REQUEST:
                TugraphManagement.UpdateJobResponse updateJobResp =
                    handleUpdateJobRequest(request.getUpdateJobRequest(), dbId);
                respBuilder
                    .setUpdateJobResponse(updateJobResp)
                    .setResponseCode(TugraphManagement.ResponseCode.SUCCESS);
            break;
            case DELETE_JOB_REQUEST:
                TugraphManagement.DeleteJobResponse deleteJobResp =
                    handleDeleteJobRequest(request.getDeleteJobRequest(), dbId);
                respBuilder
                    .setDeleteJobResponse(deleteJobResp)
                    .setResponseCode(TugraphManagement.ResponseCode.SUCCESS);
            break;
            default:
                respBuilder
                    .setResponseCode(TugraphManagement.ResponseCode.FAILED);
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

    public TugraphManagement.ReadJobStatusResponse handleReadJobStatusRequest(TugraphManagement.ReadJobStatusRequest request, String dbId) {
        log.info("read status request db_id = " + dbId);

        TugraphManagement.ReadJobStatusResponse.Builder respBuilder = TugraphManagement.ReadJobStatusResponse.newBuilder();
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
                    .setCreator(tempJobStatus.getCreator())
                    .setCreateTime(tempJobStatus.getCreateTime())
                    .build();
            respBuilder.addJobStatus(jobStatus);
        }
        TugraphManagement.ReadJobStatusResponse resp = respBuilder.build();

        return resp;
    }

    public TugraphManagement.ReadJobResultResponse handleReadJobResultRequest(TugraphManagement.ReadJobResultRequest request, String dbId) {
        log.info("read result request db_id = " + dbId);

        TugraphManagement.ReadJobResultResponse.Builder respBuilder = TugraphManagement.ReadJobResultResponse.newBuilder();
        JobResult tempResult = jobService.getResultById(request.getJobId());
        TugraphManagement.JobResult jobResult = TugraphManagement.JobResult.newBuilder()
                                                .setJobId(tempResult.getJobId())
                                                .setResult(tempResult.getResult())
                                                .build();
        respBuilder.setJobResult(jobResult);
        TugraphManagement.ReadJobResultResponse resp = respBuilder.build();

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
