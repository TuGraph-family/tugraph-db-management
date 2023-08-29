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
        log.info("create request db_id = " + dbId);

        JobStatus jobStatus =
            new JobStatus()
                .setDbId(dbId)
                .setStartTime(request.getCreateJobRequest().getStartTime())
                .setPeriod(request.getCreateJobRequest().getPeriod())
                .setProcedureName(request.getCreateJobRequest().getProcedureName())
                .setProcedureType(request.getCreateJobRequest().getProcedureType())
                .setCreator(request.getCreateJobRequest().getCreator())
                .setCreateTime(request.getCreateJobRequest().getCreateTime());
        int jobId = jobService.create(jobStatus);
        TugraphManagement.CreateJobResponse createJobResponse =
            TugraphManagement.CreateJobResponse.newBuilder()
                .setJobId(jobId)
                .build();

        return TugraphManagement.JobManagementResponse
                .newBuilder()
                .setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.FAILED)
                .setCreateJobResponse(createJobResponse)
                .build();
    }

    public TugraphManagement.JobManagementResponse handleReadJobRequest(TugraphManagement.JobManagementRequest request) {
        String db_id = request.getDbHost() + ":" + request.getDbPort();
        log.info("read request db_id = " + db_id);

        TugraphManagement.ReadJobResponse.Builder readJobResponseBuilder = TugraphManagement.ReadJobResponse.newBuilder();
        TugraphManagement.ReadJobRequest.ReadType readType = request.getReadJobRequest().getReadType();
        if (readType == TugraphManagement.ReadJobRequest.ReadType.READ_ALL){
            log.info("read all job status");
            List<JobStatus> tempJobStatusList = jobService.listStatus();
            for(JobStatus tempJobStatus: tempJobStatusList){
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
                readJobResponseBuilder.addJobStatus(jobStatus);
            }
        } else if(readType == TugraphManagement.ReadJobRequest.ReadType.READ_RESULT){
            JobResult tempResult = jobService.getResultById(request.getReadJobRequest().getJobId());
            TugraphManagement.JobResult jobResult = TugraphManagement.JobResult.newBuilder()
                                                    .setJobId(tempResult.getJobId())
                                                    .setResult(tempResult.getResult())
                                                    .build();
            readJobResponseBuilder.setJobResult(jobResult);
        }
        TugraphManagement.ReadJobResponse readJobResponse = readJobResponseBuilder.build();

        return TugraphManagement.JobManagementResponse
                .newBuilder()
                .setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.SUCCESS)
                .setReadJobResponse(readJobResponse)
                .build();
    }

    public TugraphManagement.JobManagementResponse handleUpdateJobRequest(TugraphManagement.JobManagementRequest request) {
        String db_id = request.getDbHost() + ":" + request.getDbPort();
        log.info("update request db_id = " + db_id);

        JobStatus jobStatus =
            new JobStatus()
                .setJobId(request.getUpdateJobRequest().getJobId())
                .setStatus(request.getUpdateJobRequest().getStatus())
                .setRuntime(request.getUpdateJobRequest().getRuntime());
        JobResult jobResult =
            new JobResult()
                .setJobId(request.getUpdateJobRequest().getJobId())
                .setResult(request.getUpdateJobRequest().getResult());
        jobService.update(jobStatus, jobResult);

        return TugraphManagement.JobManagementResponse
                .newBuilder()
                .setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.SUCCESS)
                .build();
    }

    public TugraphManagement.JobManagementResponse handleDeleteJobRequest(TugraphManagement.JobManagementRequest request) {
        String db_id = request.getDbHost() + ":" + request.getDbPort();
        log.info("delete request db_id = " + db_id);

        jobService.delete(request.getDeleteJobRequest().getJobId());

        return TugraphManagement.JobManagementResponse
                .newBuilder()
                .setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.SUCCESS)
                .build();
    }
}