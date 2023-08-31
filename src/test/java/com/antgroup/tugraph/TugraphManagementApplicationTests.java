package com.antgroup.tugraph;

import org.junit.jupiter.api.Test;
import org.junit.Assert;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class TugraphManagementApplicationTests {

	@Test
	void contextLoads() {
		log.info("开始单元测试1");
		Assert.assertEquals(0, 1);
	}

	@Test
	void contextLoads2() {
		log.info("开始单元测试2");
		Assert.assertEquals(0, 1);
	}

}
