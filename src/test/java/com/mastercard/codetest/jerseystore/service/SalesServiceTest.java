package com.mastercard.codetest.jerseystore.service;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SalesServiceTest {

	private final ExecutorService executorService = Executors.newFixedThreadPool(20);

	private final SalesService salesService = new SalesService();

	@Test
	public void testAddSales() throws Exception {
//		final List<Callable<Integer>> salesCallables = new ArrayList<>();
//		for (int i = 0; i < 1000; i++) {
//			salesCallables.add(() -> salesService.addSale(1));
//		}
//		final List<Future<Integer>> sales = executorService.invokeAll(salesCallables);
//
//		Assert.assertEquals(1000, sales.size());
//		Assert.assertEquals(1000, salesService.getTotalSales());
	}

}
