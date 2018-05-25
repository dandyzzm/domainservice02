package com.lonntec.sufservice.repository;

import com.lonntec.sufservice.entity.DomainUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainUserRepository extends JpaRepository<DomainUser, String> {

    DomainUser findByMobile(String mobile);
}
