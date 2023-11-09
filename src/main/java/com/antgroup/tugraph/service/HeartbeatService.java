package com.antgroup.tugraph.service;

import lgraph.management.TuGraphDBManagement;

public interface HeartbeatService {
    TuGraphDBManagement.HeartbeatResponse detectHeartbeat(TuGraphDBManagement.HeartbeatRequest request);
}
