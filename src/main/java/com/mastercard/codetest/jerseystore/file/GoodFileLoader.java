package com.mastercard.codetest.jerseystore.file;

import com.mastercard.codetest.jerseystore.common.Utils;
import com.mastercard.codetest.jerseystore.model.*;
import com.mastercard.codetest.jerseystore.service.JerseyStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Read file (using cache)
 */
@Component
public class GoodFileLoader extends FileLoader {

    // cache size (10M)
    public final static int CACHE_SIZE = 10 * 1024 * 1024;

    // CHARSET
    public final static String CHARSET = "utf-8";

    // JerseyStoreService
    private JerseyStoreService jerseyStoreService;

    @Autowired
    public GoodFileLoader(JerseyStoreService jerseyStoreService) {
        this.jerseyStoreService = jerseyStoreService;
    }

    /**
     * Read the file from the path
     *
     * @param path file path
     */
    @Override
    public void load(String path) {
        File file = new File(path);
        loadFile(file, 0);
    }

    /**
     * Read the file from the File
     *
     * @param pFile file object
     * @param pFlag 0:insert into db; 1:get by list
     */
    public Map<String, Jersey> loadFile(File pFile, int pFlag) {
        Map<String, Jersey> map = new HashMap<>();
        try {
            // Create the BufferedInputStream
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(pFile));
            // Create the BufferedReader -> Cache: 10M
            BufferedReader in = new BufferedReader(new InputStreamReader(bis, CHARSET), CACHE_SIZE);
            while (in.ready()) {
                if (pFlag == 0) {
                    // add the jersey object to service by line
                    addJerseyByLine(in.readLine());
                } else {
                    Jersey jersey = getJerseyByLine(in.readLine());
                    if (jersey != null) {
                        String key = Utils.getKey(jersey);
                        if (map.containsKey(key) == false) {
                            map.put(key, jersey);
                        } else {
                            int amount = Integer.valueOf(map.get(key).getAmount());
                            map.get(key).setAmount(amount + jersey.getAmount());
                            System.out.println("Warning: same key exists: " + key);
                        }
                    }
                }
            }
            // close the stream
            in.close();
            bis.close();
        } catch (Exception ex) {
            // Error log
            System.out.println("Error: reading error: " + pFile.getPath());
            ex.printStackTrace();
            return null;
        }
        return map;
    }

    /**
     * Get a jersey object to service by one line
     *
     * @param pLine one line of file
     * @return OK:Jersey, NG:null
     */
    private Jersey getJerseyByLine(String pLine) {
        Jersey jersey = null;
        if (pLine != null) {
            try {
                // split the line by comma
                Scanner lineScanner = new Scanner(pLine);
                lineScanner.useDelimiter(",");

                String s = lineScanner.next();
                String b = lineScanner.next();
                String cl = lineScanner.next();
                String y = lineScanner.next();
                String t = lineScanner.next();
                String c = lineScanner.next();
                String a = lineScanner.next();

                // make Jersey and add to service
                jersey = new Jersey();
                jersey.setId("");
                jersey.setSize(JerseySize.valueOf(s));
                jersey.setBrand(b);
                jersey.setClub(cl);
                jersey.setYear(y);
                jersey.setType(JerseyType.valueOf(t));
                jersey.setCut(JerseyCut.valueOf(c));
                jersey.setAmount(Integer.valueOf(a));
                jersey.setMaterial(JerseyMaterial.valueOf(1));
            } catch (Exception e) {
                // Error log
                System.out.println("Error: cannot read the line: " + pLine);
                e.printStackTrace();
                return null;
            }
        }
        return jersey;
    }

    /**
     * Add a jersey object to service by one line
     *
     * @param pLine one line of file
     * @return OK:true NG:false
     */
    private boolean addJerseyByLine(String pLine) {
        try {
            Jersey jersey = getJerseyByLine(pLine);
            if (jersey != null) {
                jerseyStoreService.addJersey(jersey);
            } else {
                return false;
            }
        } catch (Exception e) {
            // Error log
            System.out.println("Error: cannot read the line: " + pLine);
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
