package com.udacity.pricing;

import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.exceptions.PriceException;
import com.udacity.pricing.service.PricingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class PricingServiceApplicationTests {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private JacksonTester<Price> json;

	@MockBean
	private PricingService pricingService;

	@Test
	public void contextLoads() {
	}

	/**
	 * Creates pre-requisites for testing, such as an example car.
	 */
	@Before
	public void setup() throws PriceException {
		Price price = getPrice();
		given(pricingService.getPrice(1L)).willReturn(price);
		given(pricingService.getPrice(100L)).willThrow(PriceException.class);
	}

	/**
	 * Tests for finding price for a given vehicleId
	 *
	 * @throws Exception when car creation fails in the system
	 */
	@Test
	public void findPrice() throws Exception {
		Price price = getPrice();
		mvc.perform(get("/services/price").param("vehicleId", "1"))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.vehicleId").value(1));
		verify(pricingService, times(1)).getPrice(1L);
	}


	/**
	 * Tests for finding price for a given vehicleId
	 *
	 * @throws Exception when car creation fails in the system
	 */
	@Test
	public void testPriceNotFoundException() throws Exception {
		Price price = getPrice();
		mvc.perform(get("/services/price").param("vehicleId", "100"))
				.andExpect(status().isNotFound())
				.andDo(print())
				.andExpect(content().string("Price Not Found"));
		verify(pricingService, times(1)).getPrice(100L);
	}

	/**
	 * Creates an example Price object for use in testing.
	 *
	 * @return an example Price object
	 */
	private Price getPrice() {
		return new Price(1L, "USD", new BigDecimal(20000));
	}
}
