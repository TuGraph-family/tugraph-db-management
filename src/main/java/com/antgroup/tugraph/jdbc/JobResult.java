package com.antgroup.tugraph;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobStatus{
    private Integer jobId;

    private String result;
}