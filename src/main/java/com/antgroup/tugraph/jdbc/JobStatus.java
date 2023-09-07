package com.antgroup.tugraph;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class JobStatus {
    private String dbId;

    private Integer jobId;

    private Long startTime;

    private String period;

    private String procedureName;

    private String procedureType;

    private String status;

    private Long runtime;

    private String creator;

    private Long createTime;
}
