package com.antgroup.tugraph;

import com.baidu.cloud.starlight.springcloud.server.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

@RpcService
@Slf4j
public class JobManagementServiceImpl implements JobManagementService {
    private final JobService jobService = new JobServiceImpl(new JobDaoImpl());

    @Override
    public TugraphManagement.JobManagementResponse handleRequest(TugraphManagement.JobManagementRequest request) {
        TugraphManagement.JobManagementResponse resp;

        switch(request.getReqCase()){
            case CREAT_JOB_REQUEST:
                resp = handleCreatJobRequest(request);
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

    public TugraphManagement.JobManagementResponse handleCreatJobRequest(TugraphManagement.JobManagementRequest request) {
        String dbId = request.getDbHost() + ":" + request.getDbPort();
        log.info("creat request db_id = " + dbId);
        JobStatus jobStatus = new JobStatus();
        jobStatus.setDbId(dbId);
        jobStatus.setStartTime(request.getCreatJobRequest().getStartTime());
        jobStatus.setPeriod(request.getCreatJobRequest().getPeriod());
        jobStatus.setProcedureName(request.getCreatJobRequest().getProcedureName());
        jobStatus.setProcedureType(request.getCreatJobRequest().getProcedureType());
        jobStatus.setCreator(request.getCreatJobRequest().getCreator());
        jobStatus.setCreateTime(request.getCreatJobRequest().getCreateTime());
        return TugraphManagement.JobManagementResponse.newBuilder().setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.FAILED).build();
    }

    public TugraphManagement.JobManagementResponse handleReadJobRequest(TugraphManagement.JobManagementRequest request) {
        String db_id = request.getDbHost() + ":" + request.getDbPort();
        log.info("read request db_id = " + db_id);
        return TugraphManagement.JobManagementResponse.newBuilder().setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.FAILED).build();
    }

    public TugraphManagement.JobManagementResponse handleUpdateJobRequest(TugraphManagement.JobManagementRequest request) {
        String db_id = request.getDbHost() + ":" + request.getDbPort();
        log.info("update request db_id = " + db_id);
        return TugraphManagement.JobManagementResponse.newBuilder().setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.FAILED).build();
    }

    public TugraphManagement.JobManagementResponse handleDeleteJobRequest(TugraphManagement.JobManagementRequest request) {
        String db_id = request.getDbHost() + ":" + request.getDbPort();
        log.info("delete request db_id = " + db_id);
        return TugraphManagement.JobManagementResponse.newBuilder().setErrorCode(TugraphManagement.JobManagementResponse.ErrorCode.FAILED).build();
    }
}