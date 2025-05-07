package com.rnt.SecuritySystem.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class TokenBody {

	private String accessToken;
	private String refreshToken;
	
}
