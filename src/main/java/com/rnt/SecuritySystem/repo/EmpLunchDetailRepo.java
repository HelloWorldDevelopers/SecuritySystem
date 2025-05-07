package com.rnt.SecuritySystem.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rnt.SecuritySystem.dto.EmployeeLunchDetailDTO;
import com.rnt.SecuritySystem.entity.EmpLunchDetail;

public interface EmpLunchDetailRepo extends JpaRepository<EmpLunchDetail, Integer> {
	
	@Query("SELECT new com.rnt.SecuritySystem.dto.EmployeeLunchDetailDTO(" +
	        "e.staffId, e.fName, e.lName, e.email, " +  
	        "l.lunchEnrollStatus, l.startDate, l.endDate) " +
	        "FROM EmployeeMaster e " +
	        "JOIN EmpLunchDetail l ON e.staffId = l.staffId ")
	List<EmployeeLunchDetailDTO> findLunchDetailsByStaffId();


}