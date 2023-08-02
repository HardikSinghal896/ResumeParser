package com.PDFExtraction.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DemoApplicationTests {
	private Calculator c= new Calculator();
	@Test
	void contextLoads() {
	}
	@Test
	void doSumTest(){
		int expectedResult = 17;
		int actualResult = c.doSum(8,9);
		assertThat(actualResult).isEqualTo(expectedResult);
	}
}
