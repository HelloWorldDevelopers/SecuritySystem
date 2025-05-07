package com.rnt.SecuritySystem.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rnt.SecuritySystem.accesschecker.CheckApiAccess;
import com.rnt.SecuritySystem.dto.EmployeeLunchDetailDTO;
import com.rnt.SecuritySystem.dto.RescponceData;
import com.rnt.SecuritySystem.dto.TokenResponseBody;
import com.rnt.SecuritySystem.entity.EmpLunchDetail;
import com.rnt.SecuritySystem.entity.TempUserBlock;
import com.rnt.SecuritySystem.repo.EmpLunchDetailRepo;
import com.rnt.SecuritySystem.repo.UserInfoRepo;

@RestController
@RequestMapping("/api/v1/lunch/")
public class EmpLunchDetailController {

	@Autowired
	EmpLunchDetailRepo empLunchDetailRepo;

	@Autowired
	UserInfoRepo userInfoRepo;

	@PostMapping("ipAddress")
	public ResponseEntity<?> parseToken(@RequestBody TempUserBlock token, HttpSession session,
			HttpServletRequest request) {

		return ResponseEntity.ok(TokenResponseBody.builder().message("Login successful. Token generated successfully.")
				.httpStatus(HttpStatus.OK).accessToken(null).refreshToken(null).success(Boolean.TRUE).durationMs(1)
				.build());

	}

	@CheckApiAccess
	@GetMapping("getAllLunchDetails")
	public ResponseEntity<?> getAllUser(HttpSession session, HttpServletRequest request) {

		try {

			Instant start = Instant.now();
			List<EmployeeLunchDetailDTO> findAll = empLunchDetailRepo.findLunchDetailsByStaffId();

			return ResponseEntity.ok(new RescponceData(true, "Data get successfully", HttpStatus.OK,
					Duration.between(start, Instant.now()).toMillis(), findAll));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	@CheckApiAccess
	@PostMapping("saveLunch")
	public ResponseEntity<?> saveLuncg(@RequestBody EmpLunchDetail empLunchDetail, HttpSession session,
			HttpServletRequest request) {
		try {
			Instant start = Instant.now();
			EmpLunchDetail save = empLunchDetailRepo.save(empLunchDetail);
			return ResponseEntity.ok(new RescponceData(true, "Data get successfully", HttpStatus.OK,
					Duration.between(start, Instant.now()).toMillis(), null));

		} catch (Exception e) {
		}
		return null;
	}

}
