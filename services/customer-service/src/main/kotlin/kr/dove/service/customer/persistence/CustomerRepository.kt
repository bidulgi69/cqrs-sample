package kr.dove.service.customer.persistence

import org.springframework.data.r2dbc.repository.R2dbcRepository

interface CustomerRepository : R2dbcRepository<CustomerEntity, String>