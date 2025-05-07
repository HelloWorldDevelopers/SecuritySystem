package com.rnt.SecuritySystem.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rnt.SecuritySystem.entity.EmployeeMaster;
 
public interface UserInfoRepo extends JpaRepository<EmployeeMaster, Long> {

	Optional<EmployeeMaster> findByEmail(String email);

	@Query(value = "SELECT " +
            "CONCAT(am.project_alias, ' ', u.use_case_name) AS useCase, " +
            "r.read_access, r.write_access, r.edit_access, r.delete_access " +
            "FROM rbac_master r, user_role ur, use_cases u, project am " +
            "WHERE ur.role_id = r.role_id " +
            "AND ur.user_id = :userId " +
            "AND u.project_id = am.Project_ID " +
            "AND r.use_case_id = u.use_case_id " +
            "AND ur.start_Date <= CURRENT_TIMESTAMP() " +
            "AND ur.end_date >= CURRENT_TIMESTAMP() " +
            "AND r.deleted_by IS NULL " +
            "AND u.deleted_by IS NULL",
            nativeQuery = true)
    List<Object[]> getAccessDataByUserId(@Param("userId") Long userId);
}
