package com.antgroup.tugraph;

public interface HeartbeatService {
    TuGraphDBManagement.HeartbeatResponse detectHeartbeat(TuGraphDBManagement.HeartbeatRequest request);
}
