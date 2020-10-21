package com.music.SalesService.service;

import com.music.SalesService.Exceptions.ServiceException;
import com.music.SalesService.domain.Cart;
import com.music.SalesService.service.data.InvoiceData;
import com.music.SalesService.service.data.UserData;

import java.util.Set;

public interface SalesService {

    void registerUser(String firstname, String lastname, String email) throws ServiceException;

    void addUserAddress(long userId, String address) throws ServiceException;

    UserData getUserInfo(long userId) throws ServiceException;

    UserData getUserInfoByEmail(String email) throws ServiceException;

    boolean userIsCustomer(String email) throws ServiceException;

    InvoiceData checkout(Cart cart, long userId) throws ServiceException;

    Boolean checkAdminLogin(String username,String password) throws ServiceException;

    void initializeDB() throws ServiceException;

    void processInvoice(long invoice_id) throws ServiceException;

    Set<InvoiceData> getListofInvoices() throws ServiceException;

    Set<InvoiceData> getListofUnprocessedInvoices() throws ServiceException;
}
