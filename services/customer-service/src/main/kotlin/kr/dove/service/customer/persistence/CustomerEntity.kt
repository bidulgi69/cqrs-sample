package kr.dove.service.customer.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table(value = "CUSTOMERS")
data class CustomerEntity(
    @Id val id: String,
    var firstname: String,
    var lastname: String,
    var fullname: String? = null,
    var addressAsJson: String,
    var cardAsJson: String,
    @Version val version: Int = 0,
)