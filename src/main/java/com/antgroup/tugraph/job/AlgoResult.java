package com.antgroup.tugraph.job;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AlgoResult {
    private Integer JobId; // unique integer id.

    private String result;
}