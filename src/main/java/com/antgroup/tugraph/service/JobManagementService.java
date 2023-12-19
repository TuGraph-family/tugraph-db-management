package com.antgroup.tugraph.service;

import lgraph.management.TuGraphDBManagement;

public interface JobManagementService {
    TuGraphDBManagement.JobManagementResponse handleRequest(TuGraphDBManagement.JobManagementRequest request);
    TuGraphDBManagement.HeartbeatResponse detectHeartbeat(TuGraphDBManagement.HeartbeatRequest request);
}
