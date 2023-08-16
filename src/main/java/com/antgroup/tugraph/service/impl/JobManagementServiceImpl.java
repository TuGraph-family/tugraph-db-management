package com.antgroup.tugraph;

import com.baidu.cloud.starlight.springcloud.server.annotation.RpcService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

@RpcService
@Slf4j
public class JobManagementServiceImpl implements JobManagementService {
    @Autowired
    private JobService jobService;

    @Override
    public TugraphManagement.JobManagementResponse handleRequest(TugraphManagement.JobManagementRequest request) {
        TugraphManagement.JobManagementResponse resp;

        switch(request.getReqCase()){
            case CREATE_JOB_REQUEST:
                resp = handleCreateJobRequest(request);
            break;
            case READ_JOB_REQUEST:
                resp = handleReadJobRequest(request);
            break;
            case UPDATE_JOB_REQUEST:
                resp = handleUpdateJobRequest(request);
            break;
            case DELETE_JOB_REQUEST:
                resp = handleDeleteJobRequest(request);
            break;
            default:
                resp = TugraphManagement.JobManagementResponse.newBuilder().setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.FAILED).build();
        }

        return resp;
    }

    public TugraphManagement.JobManagementResponse handleCreateJobRequest(TugraphManagement.JobManagementRequest request) {
        String dbId = request.getDbHost() + ":" + request.getDbPort();
        JobStatus jobStatus = new JobStatus();
        jobStatus.setDbId(dbId);
        jobStatus.setStartTime(request.getCreateJobRequest().getStartTime());
        jobStatus.setPeriod(request.getCreateJobRequest().getPeriod());
        jobStatus.setProcedureName(request.getCreateJobRequest().getProcedureName());
        jobStatus.setProcedureType(request.getCreateJobRequest().getProcedureType());
        jobStatus.setCreator(request.getCreateJobRequest().getCreator());
        jobStatus.setCreateTime(request.getCreateJobRequest().getCreateTime());

        int jobId = jobService.create(jobStatus);
        TugraphManagement.CreateJobResponse createJobResponse = TugraphManagement.CreateJobResponse.newBuilder().setJobId(jobId).build();
        return TugraphManagement.JobManagementResponse.newBuilder().setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.FAILED).setCreateJobResponse(createJobResponse).build();
    }

    public TugraphManagement.JobManagementResponse handleReadJobRequest(TugraphManagement.JobManagementRequest request) {
        String db_id = request.getDbHost() + ":" + request.getDbPort();
        log.info("read request db_id = " + db_id);
        return TugraphManagement.JobManagementResponse.newBuilder().setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.SUCCESS).build();
    }

    public TugraphManagement.JobManagementResponse handleUpdateJobRequest(TugraphManagement.JobManagementRequest request) {
        String db_id = request.getDbHost() + ":" + request.getDbPort();
        log.info("update request db_id = " + db_id);
        JobStatus jobStatus = new JobStatus();
        jobStatus.setJobId(request.getUpdateJobRequest().getJobId());
        jobStatus.setStatus(request.getUpdateJobRequest().getStatus());
        jobStatus.setRuntime(request.getUpdateJobRequest().getRuntime());

        JobResult jobResult = new JobResult();
        jobResult.setJobId(request.getUpdateJobRequest().getJobId());
        jobResult.setResult(request.getUpdateJobRequest().getResult());

        jobService.update(jobStatus, jobResult);

        return TugraphManagement.JobManagementResponse.newBuilder().setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.SUCCESS).build();
    }

    public TugraphManagement.JobManagementResponse handleDeleteJobRequest(TugraphManagement.JobManagementRequest request) {
        String db_id = request.getDbHost() + ":" + request.getDbPort();
        log.info("delete request db_id = " + db_id);
        return TugraphManagement.JobManagementResponse.newBuilder().setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.FAILED).build();
    }
}