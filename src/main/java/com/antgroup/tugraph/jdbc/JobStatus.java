package com.antgroup.tugraph;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobStatus{
    private String dbId;

    private Integer jobId;

    private String startTime;

    private String period;

    private String procedureName;

    private String procedureType;

    private String status;

    private String runtime;

    private String creator;

    private String createTime;
}