package com.gogo.Gogox;

import com.gogo.Gogox.controllers.LotteryController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GogoxApplicationTests {

	@Autowired
	private LotteryController lotteryController;

	@Test
	void contextLoads() throws Exception {
		assertThat(lotteryController).isNotNull();
	}

}
