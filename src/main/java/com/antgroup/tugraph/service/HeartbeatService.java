package com.antgroup.tugraph.service;

import lgraph.TuGraphDBManagement;

public interface HeartbeatService {
    TuGraphDBManagement.HeartbeatResponse detectHeartbeat(TuGraphDBManagement.HeartbeatRequest request);
}
