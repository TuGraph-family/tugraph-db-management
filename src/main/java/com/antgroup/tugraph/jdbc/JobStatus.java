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

    private String startTime;

    private String period;

    private String procedureName;

    private String procedureType;

    private String status;

    private String runtime;

    private String creator;

    private String createTime;
}
