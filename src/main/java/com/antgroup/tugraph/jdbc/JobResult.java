package com.antgroup.tugraph;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class JobResult {
    private Integer JobId; // unique integer id.

    private String result;
}
