package com.antgroup.tugraph;

import com.baidu.cloud.starlight.springcloud.server.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

@RpcService
@Slf4j
public class HeartbeatServiceImpl implements HeartbeatService {

    @Override
    public TuGraphDBManagement.HeartbeatResponse detectHeartbeat(TuGraphDBManagement.HeartbeatRequest request) {
        log.info("get heartbeat request");
        String reqMsg = request.getRequestMsg();
        log.info(reqMsg);
        String respMsg = "This is a heartbeat response message.";
        int heartbeatCount = request.getHeartbeatCount();
        TuGraphDBManagement.HeartbeatResponse.Builder respBuilder =
            TuGraphDBManagement.HeartbeatResponse
                .newBuilder();
        respBuilder
            .setResponseMsg(respMsg)
            .setHeartbeatCount(heartbeatCount + 1);
        return respBuilder.build();
    }

}
