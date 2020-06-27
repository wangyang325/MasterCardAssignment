package com.mastercard.codetest.jerseystore.controller;

import com.mastercard.codetest.jerseystore.model.Jersey;
import com.mastercard.codetest.jerseystore.service.JerseyStoreService;
import com.mastercard.codetest.jerseystore.service.SalesService;
import com.mastercard.codetest.jerseystore.service.UserInfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * Front web page controller
 */
@Controller
public class IndexController {

    // Jersey store service
    @Autowired
    private JerseyStoreService jerseyStoreService;

    // Sale service
    @Autowired
    private SalesService salesService;

    // User authority service
    @Autowired
    UserInfoService userInfoService;

    /**
     * Initial index
     *
     * @param model   : Model;
     * @param session : HttpSession;
     * @return index;
     */
    @RequestMapping("/jersey/index")
    public String showIndex(Model model, HttpSession session) {
        List<Jersey> list = jerseyStoreService.getAllJerseys();
        model.addAttribute("Jerseys", list);
        model.addAttribute("Total", salesService.getTotalSales());
        model.addAttribute("User", session.getAttribute("UserId"));
        return "index";
    }

    /**
     * Initial login
     *
     * @param model : Model;
     * @return login;
     */
    @RequestMapping("/jersey/login")
    public String showLogin(Model model) {
        model.addAttribute("USERID", "wangyang");
        model.addAttribute("PASS", "wangyang");
        return "login";
    }

    /**
     * login
     *
     * @param request  : HttpServletRequest;
     * @param response : HttpServletResponse;
     * @param session  : HttpSession;
     */
    @RequestMapping("/jersey/doLogin")
    public void login(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        // Get User Id and Password from web page
        String userId = request.getParameter("USERID");
        String password = request.getParameter("PASS");

        // Add user to account realm
        SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();
        simpleAccountRealm.addAccount(userId, password);

        // create SecurityManager
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);

        // set SecurityManager
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        // Login by token
        UsernamePasswordToken token = new UsernamePasswordToken(userId, password);
        subject.login(token);
        try {
            // Login Ok
            if (subject.isAuthenticated()) {
                session.setAttribute("UsernamePasswordToken", token);
                session.setAttribute("UserId", userId);
                response.sendRedirect("/jersey/index");
            }
            // Login NG
            else {
                session.setAttribute("UsernamePasswordToken", null);
                session.setAttribute("UserId", null);
                response.sendRedirect("/jersey/403");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * logout
     *
     * @param response : HttpServletResponse;
     * @param session  : HttpSession;
     */
    @RequestMapping(value = "/jersey/doLogout", method = RequestMethod.GET)
    public void logout(HttpServletResponse response, HttpSession session) {
        // Logout
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        // Clear session and go to login page
        try {
            session.setAttribute("UsernamePasswordToken", null);
            session.setAttribute("UserId", null);
            response.sendRedirect("/jersey/login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * UnauthorizedRole
     *
     * @return error message;
     */
    @RequestMapping("/jersey/403")
    @ResponseBody
    public String unauthorizedRole() {
        return "Error:403: no permission";
    }

    /**
     * NoFound
     *
     * @return error message;
     */
    @RequestMapping("/jersey/404")
    @ResponseBody
    public String noFound() {
        return "Error:404: cannot find the page";
    }

    /**
     * downloadFile
     *
     * @param response : HttpServletResponse;
     * @return error message;
     */
    @RequestMapping("/jersey/sql")
    public String downloadFile(HttpServletResponse response) throws UnsupportedEncodingException {
        File file = new File(ClassUtils.getDefaultClassLoader().getResource("").getPath(), "/userRolePermission.sql");
        String fileName = file.getName();
        if (file.exists()) {
            // Set response
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // download
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                System.out.println("Download the song successfully!");
            } catch (Exception e) {
                System.out.println("Download the song failed!");
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}
