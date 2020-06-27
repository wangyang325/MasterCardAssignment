package com.mastercard.codetest.jerseystore.controller;

import com.mastercard.codetest.jerseystore.common.Utils;
import com.mastercard.codetest.jerseystore.service.SalesService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Sales Controller
 */
@RestController
public class SalesController {

    // Sales service
    private final SalesService salesService;

    /**
     * Constructor
     *
     * @param salesService : SalesService;
     */
    @Autowired
    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    /**
     * Get all Jersey data
     *
     * @param request : HttpServletRequest;
     * @return result;
     */
    @RequiresPermissions(Utils.PERMISSION_BUY)
    @GetMapping(value = "/rest/api/v1/sale")
    public String makeSale(HttpServletRequest request) {
        // get id
        String id = request.getParameter("id");
        // get amount
        String amount = request.getParameter("amount");
        int am = Integer.valueOf(amount);
        return salesService.addSale(id, am);
    }
}
