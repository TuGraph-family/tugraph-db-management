package com.antgroup.tugraph.service;

import lgraph.TuGraphDBManagement;

public interface JobManagementService {
    TuGraphDBManagement.JobManagementResponse handleRequest(TuGraphDBManagement.JobManagementRequest request);
}
