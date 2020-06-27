package com.mastercard.codetest.jerseystore;

import com.mastercard.codetest.jerseystore.file.BadFileLoader;
import com.mastercard.codetest.jerseystore.file.GoodFileLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@SpringBootApplication
public class JerseyStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(JerseyStoreApplication.class, args);
    }

    @Component
    class StartupApplicationListener {

        @Autowired
        GoodFileLoader fileLoader;
        //BadFileLoader fileLoader;

        @EventListener
        public void onApplicationEvent(ContextRefreshedEvent event) {
            //fileLoader.load(JerseyStoreApplication.class.getClassLoader().getResource("warehouseStock.csv").getFile());
        }
    }

}
