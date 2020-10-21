package com.music.SalesService.ui.controllers;

import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.music.SalesService.Exceptions.ServiceException;
import com.music.SalesService.service.SalesService;
import com.music.SalesService.service.data.DownloadData;
import com.music.SalesService.service.data.InvoiceData;
import com.music.SalesService.ui.controllers.beans.AdminBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@SessionAttributes("admin")
@RequestMapping("/adminController")
public class AdminController {

    public static final String Admin_jsp_dir = "/admin/";

    private SalesService salesService;
    
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public void setSalesService(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping("/admin/validate/{username}/{password}")
    public boolean validateAdmin(@Valid @PathVariable(value = "username") String username,
                                 @Valid @PathVariable(value = "password") String password) throws ServletException{
        try {
        	System.out.println(username + "\t" + password);
            return salesService.checkAdminLogin(username, password);
        } catch (ServiceException e) {
        	e.printStackTrace();
            throw new ServletException(e);
        }
    }
    
    @PutMapping("/update/invoice/{id}")
    public void processInvoice(@PathVariable(value="id") long id) throws ServletException{
    	try {
			salesService.processInvoice(id);
		} catch (ServiceException e) {
			throw new ServletException(e);
		}
    }
    
    @GetMapping("/invoice/unprocessed")
    public Set<InvoiceData> getUnProcessedInvoices() throws ServletException{
    	try {
			return salesService.getListofUnprocessedInvoices();
		} catch (ServiceException e) {
			throw new ServletException(e);
		}
    }

//    @Autowired
//    public void setCatalogService(CatalogService catalogService) {
//        this.catalogService = catalogService;
//    }

//    @Value("${spring.datasource.url}")
//    private String dbUrl;

//    @GetMapping("/adminPortalLogin.html")
//    public String welcomeadmin(HttpServletRequest request) throws ServletException {
//        String url = Admin_jsp_dir + "adminPortalLogin";
//        if (request.getSession().getAttribute("admin") != null) {
//            return Admin_jsp_dir + "AdminPage";
//        }
//        return url;
//    }

//    @PostMapping("/AdminCredentials")
//    public String displayWelcome(Model model, @RequestParam(value = "firstName", required = false) String firstname,
//                                 @RequestParam(value = "password"
//                                         , required = false) String password, HttpServletRequest request) throws ServletException {
//
//        System.out.println("Starting admin detail checkup:" + firstname + "/t" + password);
//
//        AdminBean admin = (AdminBean) request.getSession().getAttribute("admin");
//        System.out.println(admin);
//        if (admin == null)
//            admin = new AdminBean();
//        if (admin != null)
//            admin.setFirstname(firstname);
//        if (admin.getfirstname() != null)
//            request.getSession().setAttribute("admin", admin);
//        try {
//            System.out.println("Entering the condition:");
//
//            if (salesService.checkAdminLogin(firstname, password)) {
//                System.out.println("Successfully entered:");
//                System.out.println("Successful Login:-");
//                model.addAttribute("firstName", firstname);
//                return Admin_jsp_dir + "AdminPage";
//            } else {
//                System.out.print("Wrong Admin Details:");
//                return Admin_jsp_dir + "adminPortalLogin";
//            }
//        } catch (Exception e) {
//            throw new ServletException("Problem with direction");
//        }
//    }

//    @GetMapping("/ShowReport.html")
//    public String DisplayReport(Model model, HttpServletRequest request) {
//        if (request.getSession().getAttribute("admin") == null) {
//            return Admin_jsp_dir + "adminPortalLogin";
//        }
//        return Admin_jsp_dir + "ShowReport";
//    }
//
//    @GetMapping("/listVariables.html")
//    public String listVariables(Model model, HttpServletRequest request) {
//        model.addAttribute("dbUrl", dbUrl);
//        System.out.println("dbUrl from application.properties: " + dbUrl);
//        String url = Admin_jsp_dir + "listVariables";
//        return url;
//    }
//
//    //Removed Functionality
//    @RequestMapping("/initializeDB.html")
//    public String adminInitDB(Model model, HttpServletRequest request) {
//        if (request.getSession().getAttribute("admin") == null) {
//            return Admin_jsp_dir + "adminPortalLogin";
//        }
//        String info;
//        try {
//            salesService.initializeDB();
////            catalogService.initializeDB();
////            SystemTest test = new SystemTest(catalogService, salesService);
////            test.runSystemTest();
//            info = "Initialize db: success";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String url = Admin_jsp_dir + "initializeDB";
//        return url;
//    }

//    @GetMapping("/logout.html")
//    public String logout(Model model, HttpServletRequest request) {
//        model.addAttribute("admin", null);
//        request.getSession().invalidate();
//        return "/logout";
//    }
//
//    @GetMapping("/AdminPage.html")
//    public String PageTesting(HttpServletRequest request) {
//        if (request.getSession().getAttribute("admin") == null) {
//            return Admin_jsp_dir + "adminPortalLogin";
//        }
//        String url = Admin_jsp_dir + "AdminPage";
//        return url;
//    }
//
//    @GetMapping("/ListOfDownloads.html")
//    public String showAllDownloads(Model model, @RequestParam(value = "command", required = false) String command
//            , HttpServletRequest request) {
//        if (request.getSession().getAttribute("admin") == null) {
//            return Admin_jsp_dir + "adminPortalLogin";
//        }
//        Set<DownloadData> allDownload = null;
//        String url = null;
//        try {
//        	String theUrl = "";
//        	
//        	ResponseEntity<Set<DownloadData>> response = restTemplate.exchange(theUrl, HttpMethod.GET, null, new ParameterizedTypeReference<Set<DownloadData>>() {
//            });
//            
//            allDownload = response.getBody();
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//        if (allDownload != null) {
//            model.addAttribute("allDown", allDownload);
//            url = Admin_jsp_dir + "ListOfDownloads";
//        } else {
//            String nothing = "There are no downloads to show";
//            model.addAttribute("nothing", nothing);
//            url = Admin_jsp_dir + "ListOfDownloads";
//        }
//        return url;
//    }
//
//    @GetMapping("/process")
//    public String Process(Model model, @RequestParam(value = "id", required = false) String id, HttpServletRequest request) {
//        if (request.getSession().getAttribute("admin") == null) {
//            return Admin_jsp_dir + "adminPortalLogin";
//        }
//        String url = null;
//        System.out.println("Processing invoice with id :" + id);
//        long id1 = Long.parseLong(id);
//        try {
//            if (id != null) {
//                salesService.processInvoice(id1);
//                url = Admin_jsp_dir + "ProcessInvoice";
//            } else {
//                url = "forward:processInvoice";
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//            url = "forward:processInvoice";
//        }
//        return url;
//    }
//
//    @GetMapping("/processInvoice")
//    public String ProcessInvoice(Model model, @RequestParam(value = "command", required = false) String command, HttpServletRequest request) {
//        if (request.getSession().getAttribute("admin") == null) {
//            return Admin_jsp_dir + "adminPortalLogin";
//        }
//        Set<InvoiceData> invoices = null;
//        String ForwardUrl = null;
//
//
//        try {
//            invoices = salesService.getListofUnprocessedInvoices();
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//        if (invoices != null && command == null) {
//            model.addAttribute("Invoices", invoices);
//            ForwardUrl = Admin_jsp_dir + "ProcessInvoice";
//        } else {
//            String nothing = "There are no downloads to show";
//            model.addAttribute("nothing", nothing);
//            ForwardUrl = Admin_jsp_dir + "ProcessInvoice";
//        }
//
//
//        return ForwardUrl;
//    }
//
//
//    @GetMapping("/forinvoiceprocess.html")
//    public String ToProcessInvoice(Model model, @RequestParam(value = "command", required = false) String command, HttpServletRequest request) {
//        if (request.getSession().getAttribute("admin") == null) {
//            return Admin_jsp_dir + "adminPortalLogin";
//        }
//        Set<InvoiceData> invoices = null;
//        String url = null;
//        try {
//            invoices = salesService.getListofUnprocessedInvoices();
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//        if (invoices != null && command == null) {
//            model.addAttribute("Invoices", invoices);
//            url = Admin_jsp_dir + "forinvoiceprocess";
//
//        } else {
//            String nothing = "There are no downloads to show";
//            model.addAttribute("nothing", nothing);
//            url = Admin_jsp_dir + "forinvoiceprocess";
//        }
//
//
//        return url;
//    }

}
