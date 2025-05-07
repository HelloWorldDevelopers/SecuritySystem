package com.rnt.SecuritySystem.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rnt.SecuritySystem.dto.AccessDTO;
import com.rnt.SecuritySystem.repo.UserInfoRepo;
import com.rnt.SecuritySystem.service.AccessService;

@Service
public class AccessServicImple implements AccessService {

	@Autowired
	UserInfoRepo userInfoRepo;

	public List<AccessDTO> getAccessDataByUserId(Long userId) {
		List<Object[]> results = userInfoRepo.getAccessDataByUserId(userId);
		return results.stream()
				.map(row -> new AccessDTO((String) row[0], (char) row[1], (char) row[2], (char) row[3], (char) row[4]))
				.toList();
	}

}