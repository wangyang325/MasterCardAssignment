package com.mastercard.codetest.jerseystore.service;

import com.mastercard.codetest.jerseystore.JerseyStoreApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JerseyStoreApplication.class)
public class SalesServiceTest {

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    @Autowired
    private SalesService salesService;

    @Test
    public void testAddSales() throws Exception {
        final List<Callable<String>> salesCallables = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            salesCallables.add(() -> salesService.addSale("0c2c8aa0-f496-4279-b644-724478e37d90", 1));
        }
        final List<Future<String>> sales = executorService.invokeAll(salesCallables);

        Assert.assertEquals(1000, sales.size());
        System.out.println("Call: " + sales.size());
        Assert.assertEquals(1000, salesService.getTotalSales());
    }

}
