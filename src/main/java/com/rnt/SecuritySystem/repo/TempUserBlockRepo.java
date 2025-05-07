package com.rnt.SecuritySystem.repo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rnt.SecuritySystem.entity.TempUserBlock;

public interface TempUserBlockRepo extends JpaRepository<TempUserBlock, Long	> {

	TempUserBlock findByIpAddress(String remoteAddr);

    @Transactional
	@Modifying
	@Query("DELETE FROM TempUserBlock t WHERE t.ipAddress = :ipAddress")
	int deleteByIpAddress(@Param("ipAddress") String ipAddress);


}
