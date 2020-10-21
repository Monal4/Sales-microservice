package com.music.SalesService.ui.controllers;

import com.music.SalesService.Exceptions.ServiceException;
import com.music.SalesService.service.SalesService;
import com.music.SalesService.service.data.UserData;
import com.music.SalesService.ui.controllers.beans.UserBean;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@RestController
@SessionAttributes({"user", "cart"})
public class SalesController {

    private SalesService salesService;

    @Autowired
    public void setSalesService(SalesService salesService) {
        this.salesService = salesService;
    }

    @PostMapping("/user/register/{firstname}/{lastname}/{email}")
    public void registerUser(@PathVariable(value = "firstname") String firstname,
                             @PathVariable(value = "lastname") String lastname,
                             @Valid @PathVariable(value = "email") String email) throws ServletException{
        try{
            salesService.registerUser(firstname,lastname,email);
//            return salesService.getUserInfoByEmail(email);
        }catch (ServiceException e) {
            throw new ServletException(e);
        }

    }

    @GetMapping("/user/customer/{email}")
    public boolean isCustomer(@PathVariable(value = "email") String email) throws ServletException{
        try {
            return salesService.userIsCustomer(email);
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
    }

    @GetMapping("/user/{email}")
    public UserData getUserByEmail(@PathVariable(value = "email") String email) throws ServletException{
        try {
            return salesService.getUserInfoByEmail(email);
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
    }
    
    @PostMapping("/user/{id}/{address}")
    public void saveUserAddress(@PathVariable(value = "id") long id,
    								@PathVariable(value = "address") String address) throws ServletException{
    	try {
    		salesService.addUserAddress(id, address);
    		//return salesService.getUserInfo(id);
    	}catch(ServiceException e) {
    		throw new ServletException(e);
    	}
    }

//    @GetMapping("/userWelcome.html")
//    public String welcomeuser(HttpServletRequest request) throws ServletException {
//        if (request.getSession().getAttribute("user") != null) {
//            return "catalog";
//        }
//        return "userWelcome";
//    }
//
//    @PostMapping("/Registered.html")
//    public String displayWelcome(Model model, @RequestParam(value = "email", required = false) String email,
//                                 @RequestParam(value = "firstName", required = false) String firstname, @RequestParam(value =
//            "lastName", required = false) String lastname,
//                                 @RequestParam(value = "address", required = false) String address, HttpServletRequest request) throws ServletException {
//
//        System.out.println("Starting User registration:");
//
//        //String forwardURL;
//
//        UserBean user = (UserBean) request.getSession().getAttribute("user");
//        if (user == null) {
//            user = new UserBean();
//            user.setEmail(email);
//        }
//        request.getSession().setAttribute("user", user);
//
//        System.out.print("The inserted values are:-" + email + firstname + lastname);
//        try {
//            salesService.registerUser(firstname, lastname, email);
//        } catch (Exception e) {
//            System.out.println(e);
//            throw new ServletException("Cannot insert user into Database:-");
//        }
//
//        try {
//            if (salesService.userIsCustomer(email)) {
//                System.out.println("address:");
//                salesService.getUserInfoByEmail(email);
//            } else {
//                model.addAttribute("email", email);
//                return "address";
//            }
//        } catch (Exception e) {
//            throw new ServletException("address update problem" + e);
//        }
//
//        return "catalog";
//    }

//    @RequestMapping("addAddress.html")
//    public String addAddress(Model model, @RequestParam(value = "email", required = false)
//            String email, @RequestParam(value = "address", required = false) String address, HttpServletRequest request) throws ServletException {
//
//        UserData user = new UserData();
//
//        try {
//            user = salesService.getUserInfoByEmail(email);
//        } catch (Exception e) {
//            throw new ServletException("Cannot get info from email");
//        }
//        long id = user.getId();
//        try {
//            salesService.addUserAddress(id, address);
//        } catch (Exception e) {
//            throw new ServletException("Cannot update address");
//        }
//
//        return "catalog";
//    }

//    @GetMapping("address.html")
//    public String addressForm() {
//        return "address";
//    }
//
//    @GetMapping("catalog.html")
//    public String showCatalog(HttpServletRequest request) {
//        return "catalog";
//    }

}