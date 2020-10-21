package com.music.SalesService.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.music.SalesService.Exceptions.ServiceException;
import com.music.SalesService.dao.AdminUserDAO;
import com.music.SalesService.dao.InvoiceDAO;
import com.music.SalesService.dao.SalesDbDAO;
import com.music.SalesService.dao.UserDAO;
import com.music.SalesService.domain.*;
import com.music.SalesService.service.data.InvoiceData;
import com.music.SalesService.service.data.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SalesServiceImpl implements SalesService{

    private SalesDbDAO salesDbDAO;

    private InvoiceDAO invoiceDb;

    private UserDAO userDb;

    private AdminUserDAO adminUserDb;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    public void setSalesDbDAO(SalesDbDAO salesDbDAO) {
        this.salesDbDAO = salesDbDAO;
    }

    @Autowired
    public void setInvoiceDb(InvoiceDAO invoiceDb) {
        this.invoiceDb = invoiceDb;
    }

    @Autowired
    public void setUserDb(UserDAO userDb) {
        this.userDb = userDb;
    }

    @Autowired
    public void setAdminUserDb(AdminUserDAO adminUserDb) {
        this.adminUserDb = adminUserDb;
    }

    public void registerUser(String firstname, String lastname, String email) throws ServiceException {
        User user = null;
        Connection connection = null;
        try {
            connection = salesDbDAO.startTransaction();
            user = userDb.findUserByEmail(connection, email);
            if (user == null) { // this user has not registered yet
                user = new User();
                user.setFirstname(firstname);
                user.setLastname(lastname);
                user.setEmailAddress(email);
                userDb.insertUser(connection, user);
            }
            salesDbDAO.commitTransaction(connection);
        } catch (Exception e) {
            salesDbDAO.rollbackAfterException(connection);
            throw new ServiceException("Error while registering user: ", e);
        }
    }

    public void addUserAddress(long userId, String address) throws ServiceException {
        User user = null;
        Connection connection = null;
        try {
            connection = salesDbDAO.startTransaction();
            user = userDb.findUserByID(connection, userId);
            if (user == null) { // this user has not registered yet
                throw new ServiceException("Can't add address to unregistered user " + userId);
            }
            user.setAddress(address);
            userDb.updateUserAddress(connection, user);
            salesDbDAO.commitTransaction(connection);
        } catch (Exception e) {
            salesDbDAO.rollbackAfterException(connection);
            throw new ServiceException("Error while registering user: ", e);
        }
    }

//	public boolean checkUserAddress(long userId) throws ServiceException {
//
//		User user = null;
//		Connection connection = null;
//
//		try {
//			connection = salesDbDAO.startTransaction();
//			user = userDb.findUserByID(connection, userId);
//
//			if (user == null) { // this user has not registered yet
//				throw new ServiceException("No user with this ID " + userId);
//			}
//			if(user.get)
//		}catch(Exception e) {
//			salesDbDAO.rollbackAfterException(connection);
//			throw new ServiceException("Error while checking user: ", e);
//		}
//	}


    public UserData getUserInfo(long userId) throws ServiceException {
        User user = null;
        UserData user1 = null;
        Connection connection = null;
        try {
            connection = salesDbDAO.startTransaction();
            user = userDb.findUserByID(connection, userId);
            user1 = new UserData(user);
            salesDbDAO.commitTransaction(connection);
        } catch (Exception e) {
            salesDbDAO.rollbackAfterException(connection);
            throw new ServiceException("Error while getting user info: ", e);
        }
        if (user != null)
            user1 = new UserData(user);
        return user1;
    }

    public UserData getUserInfoByEmail(String email) throws ServiceException {
        User user = null;
        UserData user1 = null;
        Connection connection = null;
        try {
            connection = salesDbDAO.startTransaction();
            user = userDb.findUserByEmail(connection, email);
            user1 = new UserData(user);
            salesDbDAO.commitTransaction(connection);
        } catch (Exception e) {
            salesDbDAO.rollbackAfterException(connection);
            throw new ServiceException("Error while getting user info: ", e);
        }
        if (user != null)
            user1 = new UserData(user);
        return user1;
    }

    public boolean userIsCustomer(String email) throws ServiceException {
        User user = null;
        Connection connection = null;

        try {
            connection = salesDbDAO.startTransaction();
            user = userDb.findUserByEmail(connection, email);
            salesDbDAO.commitTransaction(connection);
        } catch (Exception e) {
            salesDbDAO.rollbackAfterException(connection);
            throw new ServiceException("Error while getting user info: ", e);
        }
        if (user != null) {
            System.out.println("customer name = " + user.getFirstname());
            System.out.println("customer addr = " + user.getAddress());
            System.out.println("returning " + (user != null && user.getAddress() != null
                    && user.getAddress().length() > 0 && !user.getAddress().equalsIgnoreCase("null")));
        }
        return user != null && user.getAddress() != null && user.getAddress().length() > 0
                && !user.getAddress().equalsIgnoreCase("null");
    }

    public InvoiceData checkout(Cart cart, long userId) throws ServiceException {
        Connection connection = null;
        Invoice invoice = null;
        try {

            invoice = new Invoice(-1L, null, new Date(), false, null, null);
            Set<LineItem> invItems = new HashSet<LineItem>();
            BigDecimal invoiceTotal = new BigDecimal(0);

            for (CartItem item : cart.getItems()) {
                final String url = "http://CATALOGSERVICE/getProduct/"+item.getProductId();
                Product product = restTemplate.getForObject(url, Product.class);
//                Product product = catalogService.getProduct(item.getProductId());
                LineItem invItem = new LineItem();
                invItem.setProductCode(product.getCode());
                invItem.setInvoice(invoice);
                invItems.add(invItem);
                BigDecimal price = product.getPrice();
                BigDecimal quantity = new BigDecimal(item.getQuantity());
                invoiceTotal = invoiceTotal.add(price.multiply(quantity));
            }
            invoice.setLineItems(invItems);
            invoice.setTotalAmount(invoiceTotal);

            connection = salesDbDAO.startTransaction();
            User user = userDb.findUserByID(connection, userId);
            if (user == null) {
                throw new ServiceException("Checkout failed: can't find user");
            }
            invoice.setUser(user);
            String address = user.getAddress();
            if (address == null || address.isEmpty() || address.equalsIgnoreCase("null")) {
                throw new ServiceException("Checkout failed: no address for user "+user.getEmailAddress());
            }

            invoiceDb.insertInvoice(connection, invoice);
            salesDbDAO.commitTransaction(connection);
            cart.clear();
        } catch (Exception e) {
            salesDbDAO.rollbackAfterException(connection);
            throw new ServiceException("Can't check out: ", e);
        }
        return new InvoiceData(invoice);
    }

    public Boolean checkAdminLogin(String username,String password) throws ServiceException {
        Connection connection = null;
        System.out.println(username + "\t" + password + connection);
        Boolean b = false;
        try {

            System.out.println(connection);
            connection = salesDbDAO.startTransaction();
            b = adminUserDb.findAdminUser(connection, username, password);
            salesDbDAO.commitTransaction(connection);
        } catch (Exception e)
        {
            salesDbDAO.rollbackAfterException(connection);
        	e.printStackTrace();
            throw new ServiceException("Check login error: ", e);
        }
        return b;
    }

    public void initializeDB() throws ServiceException {
        try {
            System.out.println("initDB salesDbDAO ="+salesDbDAO);
            salesDbDAO.initializeDb();
        } catch (Exception e) { // any exception
            throw new ServiceException("Can't initialize DB: (probably need to load DB)", e);
        }
    }

    public void processInvoice(long invoice_id) throws ServiceException {
        Connection connection = null;
        try {
            connection = salesDbDAO.startTransaction();
            Invoice invoice = invoiceDb.findInvoice(connection, invoice_id);
            invoice.setProcessed(true);
            invoiceDb.updateInvoice(connection, invoice);
            salesDbDAO.commitTransaction(connection);
        } catch (Exception e) {
            salesDbDAO.rollbackAfterException(connection);
            throw new ServiceException("Invoice was not processed successfully: ", e);
        }
    }

    public Set<InvoiceData> getListofInvoices() throws ServiceException {
        Connection connection = null;
        Set<Invoice> invoices = null;
        try {
            connection = salesDbDAO.startTransaction();
            invoices = invoiceDb.findAllInvoices(connection);
            salesDbDAO.commitTransaction(connection);
        } catch (Exception e) {
            salesDbDAO.rollbackAfterException(connection);
            throw new ServiceException("Can't find invoice list in DB: ", e);
        }
        Set<InvoiceData> invoices1 = new HashSet<InvoiceData>();
        for (Invoice i : invoices) {
            invoices1.add(new InvoiceData(i));
        }
        return invoices1;
    }

    public Set<InvoiceData> getListofUnprocessedInvoices() throws ServiceException {
        Connection connection = null;
        Set<Invoice> invoices = null;
        try {
            connection = salesDbDAO.startTransaction();
            invoices = invoiceDb.findAllUnprocessedInvoices(connection);
            salesDbDAO.commitTransaction(connection);
        } catch (Exception e) {
            salesDbDAO.rollbackAfterException(connection);
            throw new ServiceException("Can't find unprocessed invoice list: ", e);
        }
        Set<InvoiceData> invoices1 = new HashSet<InvoiceData>();
        for (Invoice i : invoices) {
            invoices1.add(new InvoiceData(i));
        }
        return invoices1;
    }

}