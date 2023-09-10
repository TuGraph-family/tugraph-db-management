package com.antgroup.tugraph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.baidu.cloud.starlight.springcloud.server.annotation.StarlightScan;

@SpringBootApplication
@StarlightScan
public class TuGraphDBManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(TuGraphDBManagementApplication.class, args);
	}

}
