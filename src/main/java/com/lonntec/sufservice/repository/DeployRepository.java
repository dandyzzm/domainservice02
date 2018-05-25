package com.lonntec.sufservice.repository;

import com.lonntec.sufservice.entity.ApplyForm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeployRepository extends PagingAndSortingRepository<ApplyForm,String>{
    @Query("select f from ApplyForm f where " +
            "(f.domain.domainNumber like :keyword or f.domain.domainName like :keyword or f.domain.domainShortName like :keyword) " +
            "and f.domain.user.rowId = :userId" +
            " order by f.createTime desc")
    List<ApplyForm> findAllByMyQuery(
            @Param("keyword") String keyword,
            @Param("userId") String ownerId,
            Pageable pageable);

    @Query("select f from ApplyForm f where f.domain.domainNumber like :keyword or f.domain.domainName like :keyword or f.domain.domainShortName like :keyword order by f.createTime desc")
    List<ApplyForm> findAllByMyQueryIsAdmin(
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("select count(f) from ApplyForm f where " +
            "(f.domain.domainNumber like :keyword or f.domain.domainName like :keyword or f.domain.domainShortName like :keyword) " +
            "and f.domain.user.rowId = :userId")
    Integer countByMyQuery(
            @Param("keyword") String keyword,
            @Param("userId") String ownerId);

    @Query("select count(f) from ApplyForm f where f.domain.domainNumber like :keyword or f.domain.domainName like :keyword or f.domain.domainShortName like :keyword")
    Integer countByMyQueryIsAdmin(@Param("keyword") String keyword);
}
