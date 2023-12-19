package com.antgroup.tugraph.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Job {
    private Integer jobId; // unique self increment integer id.

    private String dbId; // IP:port

    private Long startTime; // epoch time, ms

    private String period; // PERIODIC, IMMEDIATE, DELAYED

    private String procedureName;

    private String procedureType;

    private String status; // pending, success, failed

    private Long runtime; // ms

    private String user;

    private Long createTime; // epoch time, ms
}
