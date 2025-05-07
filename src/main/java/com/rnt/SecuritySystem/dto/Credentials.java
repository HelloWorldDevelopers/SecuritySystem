package com.rnt.SecuritySystem.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credentials {

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Please enter a valid email address")
	@Size(max = 100, message = "Email must be less than 100 characters")
	
	private String userName;
	
	@NotBlank(message = "Password cannot be blank")
	@Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
	private String password;

}
