package com.antgroup.tugraph;

import org.junit.jupiter.api.Test;
// import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TugraphManagementApplicationTests {

	// @Test
	// void contextLoads() {
	// 	log.info("开始单元测试1");
	// 	Assert.assertEquals(0, 1);
	// }

	@Test
	@Order(0)
	void testCreateJob() {
		log.info("test create job.");
	}

	@Test
	@Order(1)
	void testReadJob() {
		log.info("test read job.");
	}

	@Test
	@Order(2)
	void testUpdateJob() {
		log.info("test update job.");
	}

	@Test
	@Order(3)
	void testDeleteJob() {
		log.info("test delete job.");
	}

}
