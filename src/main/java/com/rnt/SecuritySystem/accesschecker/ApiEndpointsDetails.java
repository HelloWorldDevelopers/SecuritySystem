package com.rnt.SecuritySystem.accesschecker;

import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiEndpointsDetails {

 private String apiEndpoint;
 public HttpMethod httpMethod;
 public String userCase;
 	
}
