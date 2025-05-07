package com.rnt.SecuritySystem.service;

import java.util.List;

import com.rnt.SecuritySystem.dto.AccessDTO;

public interface AccessService {
	public List<AccessDTO> getAccessDataByUserId(Long userId);
}
