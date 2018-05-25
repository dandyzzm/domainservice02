package com.lonntec.sufservice.repository;

import com.lonntec.sufservice.entity.License;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenseRepository extends PagingAndSortingRepository<License,String>{
    @Query("select l from License l where " +
            "(l.domain.domainNumber like :keyword or l.domain.domainName like :keyword or l.domain.domainShortName like :keyword) " +
            "and l.domain.user.rowId = :userId" +
            " order by l.createTime desc")
    List<License> findAllByMyQuery(
            @Param("keyword") String keyword,
            @Param("userId") String ownerId,
            Pageable pageable);

    @Query("select l from License l where l.domain.domainNumber like :keyword or l.domain.domainName like :keyword or l.domain.domainShortName like :keyword order by l.createTime desc")
    List<License> findAllByMyQueryIsAdmin(
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("select count(l) from License l where " +
            "(l.domain.domainNumber like :keyword or l.domain.domainName like :keyword or l.domain.domainShortName like :keyword) " +
            "and l.domain.user.rowId = :userId")
    Integer countByMyQuery(
            @Param("keyword") String keyword,
            @Param("userId") String ownerId);

    @Query("select count(l) from License l where l.domain.domainNumber like :keyword or l.domain.domainName like :keyword or l.domain.domainShortName like :keyword")
    Integer countByMyQueryIsAdmin(@Param("keyword") String keyword);
}
