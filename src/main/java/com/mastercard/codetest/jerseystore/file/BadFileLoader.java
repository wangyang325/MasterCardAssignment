package com.mastercard.codetest.jerseystore.file;

import com.mastercard.codetest.jerseystore.model.Jersey;
import com.mastercard.codetest.jerseystore.model.JerseyCut;
import com.mastercard.codetest.jerseystore.model.JerseySize;
import com.mastercard.codetest.jerseystore.model.JerseyType;
import com.mastercard.codetest.jerseystore.service.JerseyStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;

/**
 * What's wrong with this implementation ?
 * Add comments around the issues you identify and suggest ways to improve the code
 */
@Component
public class BadFileLoader extends FileLoader {

    private JerseyStoreService jerseyStoreService;

    @Autowired
    public BadFileLoader(JerseyStoreService jerseyStoreService){
        this.jerseyStoreService = jerseyStoreService;
    }

    public void load(String filename) {
        try {
            // **************   comment By Yang Wang    **************
            // 1. need to assign the charset to avoid the garbled text
            // 2. do not read all data into memory, if it is a huge file, the error of "outOfMemory" will happen.
            //    solution: read by cache.
            // 3. divide the error into different types. For example:
            //    1) the file does not exist.
            //    2) the file format is not right.
            //    3) the other unexpect errors.
            // 4. fault tolerance:
            //    1) if the file does not exist or blank, the process can be past. (warning)
            //    2) some lines are illegal format, the process can be past and output the wrong lines.
            // 5. Transaction and BatchUpdate
            //    1) solution 1: (recommend for big data)
            //       split the file into parts and batchUpdate the data into db, output the fault lines for re-running.
            //    2) solution 2: (only for small data)
            //       first, read all data into memory, if failed, finish all.
            //       second, use the transaction, batchUpdate all, if failed, rollback all.
            // 6. Thread block
            //    use multiple threads to avoid blocking and optimize calculation ability (CPUs) (master-workers(thread pool))
            File f = new File(filename);
            FileReader r = new FileReader(f);
            int i;
            String fileContent = "";
            while ((i = r.read()) != -1) {
                System.out.print((char) i);
                fileContent += (char) i;
            }
            String[] split = fileContent.split(",");
            for(int j = 0; j < split.length; j += 7) {
                String s = split[j].trim();
                String b = split[j + 1];
                String cl = split[j + 2];
                String y = split[j + 3];
                String t = split[j + 4];
                String c = split[j + 5];
                String a = split[j + 6];
                for(int k = 0; k < Integer.parseInt(a); k++) {
                    try {
                        Jersey jersey = new Jersey();
                        jersey.setSize(JerseySize.valueOf(s));
                        jersey.setBrand(b);
                        jersey.setClub(cl);
                        jersey.setYear(y);
                        jersey.setType(JerseyType.valueOf(t));
                        jersey.setCut(JerseyCut.valueOf(c));
                        jerseyStoreService.addJersey(jersey);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
