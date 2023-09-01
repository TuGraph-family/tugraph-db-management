package com.antgroup.tugraph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.baidu.cloud.starlight.springcloud.server.annotation.StarlightScan;

@SpringBootApplication
@StarlightScan
public class TugraphManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(TugraphManagementApplication.class, args);
	}

}
