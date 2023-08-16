package com.antgroup.tugraph;

import com.baidu.cloud.starlight.springcloud.server.annotation.RpcService;

@RpcService
public class JobManagementServiceImpl implements JobManagementService {
    @Override
    public TugraphManagement.JobManagementResponse handleRequest(TugraphManagement.JobManagementRequest request) {
        TugraphManagement.JobManagementResponse resp = TugraphManagement.JobManagementResponse.newBuilder().build();
        return resp;
    }
}