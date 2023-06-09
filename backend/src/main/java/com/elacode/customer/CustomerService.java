package com.elacode.customer;

import com.elacode.exeption.DuplicateResourceException;
import com.elacode.exeption.RequestValidationException;
import com.elacode.exeption.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final CustomerDTOMapper customerDTOMapper;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao, CustomerDTOMapper customerDTOMapper, PasswordEncoder passwordEncoder) {
        this.customerDao = customerDao;
        this.customerDTOMapper = customerDTOMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerDao.selectAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(long id) {
        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(() ->
                        new ResourceNotFoundException("customer with id [%s] not found".formatted(id))
                );
    }

    public void saveCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        if (customerDao.existsCustomerWithEmail(email)) {
            throw new DuplicateResourceException("customer with email [%s] already exist".formatted(email));
        }
        customerDao.insertCustomer(
                new Customer(
                        customerRegistrationRequest.name(),
                        customerRegistrationRequest.email(),
                        passwordEncoder.encode(customerRegistrationRequest.password()),
                        customerRegistrationRequest.age(),
                        customerRegistrationRequest.gender())
        );
    }

    public void deleteCustomerById(Long customerId) {
        if (!customerDao.existsCustomerWithId(customerId)) {
            throw new ResourceNotFoundException("customer with id [%s] does not exist".formatted(customerId));
        }
        customerDao.deleteCustomerById(customerId);
    }

    public void updateCustomer(Long customerId, CustomerUpdateRequest updateRequest) {
        Customer customer = customerDao.selectCustomerById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("customer with id [%s] not found".formatted(customerId))
                );
        boolean changes = false;

        if (updateRequest.name() != null && !updateRequest.name().equals(customer.getName())) {
            customer.setName(updateRequest.name());
            changes = true;
        }

        if (updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())) {
            customer.setAge(updateRequest.age());
            changes = true;
        }

        if (updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
            if (customerDao.existsCustomerWithEmail(updateRequest.email())) {
                throw new DuplicateResourceException(
                        "email already taken"
                );
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }

        if (updateRequest.gender() != null && !updateRequest.gender().equals(customer.getGender())) {
            customer.setGender(updateRequest.gender());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);
    }

}
