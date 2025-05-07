package com.rnt.SecuritySystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeLunchDetailDTO {

	private int staffId;
	private String fName;
	private String lName;
	private String email;
	private String lunchEnrollStatus;
	private String startDate;
	private String endDate;

	 
}
