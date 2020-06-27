package com.mastercard.codetest.jerseystore.controller;

import com.mastercard.codetest.jerseystore.model.Jersey;
import com.mastercard.codetest.jerseystore.service.JerseyStoreService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.mastercard.codetest.jerseystore.common.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Jersey Controller
 */
@RestController
public class JerseyController {

    // Jersey Store service
    private JerseyStoreService jerseyStoreService;

    /**
     * Constructor
     *
     * @param jerseyStoreService : JerseyStoreService;
     */
    @Autowired
    public JerseyController(JerseyStoreService jerseyStoreService) {
        this.jerseyStoreService = jerseyStoreService;
    }

    /**
     * Get Jersey data by id
     *
     * @param id : String;
     * @return Jersey;
     */
    @GetMapping("/rest/api/v1/jersey/{id}")
    public Jersey getJersey(@PathVariable String id) {
        return jerseyStoreService.getJersey(id);
    }

    /**
     * Get all Jersey data
     *
     * @return List<Jersey>;
     */
    @GetMapping("/rest/api/v1/jersey")
    public List<Jersey> getJerseys() {
        return jerseyStoreService.getAllJerseys();
    }

    /**
     * Check the difference of  csv and database
     *
     * @param multfile : MultipartFile;
     * @return difference;
     */
    @RequiresPermissions(Utils.PERMISSION_VIEW)
    @RequestMapping(value = "/jersey/upload/check", method = RequestMethod.POST)
    @ResponseBody
    public String uploadCheck(@RequestParam("file") MultipartFile multfile) {
        String rs = "";
        // file name
        String fileName = multfile.getOriginalFilename();
        // extension
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        try {
            // use uuid as file name
            final File excelFile = File.createTempFile(UUID.randomUUID().toString(), prefix);
            // MultipartFile to File
            multfile.transferTo(excelFile);
            // check the difference
            rs = jerseyStoreService.checkJersey(excelFile);
            // delete file
            excelFile.delete();
        } catch (Exception e) {
            rs = "Upload Error:" + e.getMessage();
        }
        return rs;
    }

    /**
     * Update database from cvs
     *
     * @param multfile : MultipartFile;
     * @return result;
     */
    @RequiresPermissions(Utils.PERMISSION_ADD)
    @RequestMapping(value = "/jersey/upload/update", method = RequestMethod.POST)
    @ResponseBody
    public String uploadUpdate(@RequestParam("file") MultipartFile multfile) {
        String rs = "";
        // file name
        String fileName = multfile.getOriginalFilename();
        // extension
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        try {
            // use uuid as file name
            final File excelFile = File.createTempFile(UUID.randomUUID().toString(), prefix);
            // MultipartFile to File
            multfile.transferTo(excelFile);
            // check the difference
            rs = jerseyStoreService.addJersey(excelFile);
            // delete file
            excelFile.delete();
        } catch (Exception e) {
            rs = "Upload Error:" + e.getMessage();
        }
        return rs;
    }
}
