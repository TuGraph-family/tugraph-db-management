package com.antgroup.tugraph;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Job {
    private Integer jobId;

    private String dbId;

    private Long startTime;

    private String period;

    private String procedureName;

    private String procedureType;

    private String status;

    private Long runtime;

    private String user;

    private Long createTime;
}
