package com.elvira.customer;

import com.elvira.exeption.DuplicateResourceException;
import com.elvira.exeption.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("customer with id [%s] not found".formatted(id))
                );
    }

    public void saveCustomer(CustomerRegistrationRequest customerRegistrationRequest){
        String email = customerRegistrationRequest.email();
        if(customerDao.existPersonWithEmail(email)){
            throw new DuplicateResourceException("customer with email [%s] already exist".formatted(email));
        }
        customerDao.insertCustomer(
                new Customer(
                        customerRegistrationRequest.name(),
                        customerRegistrationRequest.email(),
                        customerRegistrationRequest.age()
                )
        );
    }

    public void deleteCustomerById(Integer customerId){
        if(!customerDao.existPersonWithId(customerId)){
            throw new ResourceNotFoundException("customer with id [%s] does not exist".formatted(customerId));
        }
        customerDao.deleteCustomerById(customerId);
    }
}
