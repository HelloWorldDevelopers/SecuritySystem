package com.rnt.SecuritySystem.accesschecker;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;

public class ApiEndpoints {

	public static Map<String, ApiEndpointsDetails> getAllApiEndpoints() {
		Map<String, ApiEndpointsDetails> map = new HashMap();
		map.put("lunch/getAllLunchDetails",
				new ApiEndpointsDetails("/lunch/getAllLunchDetails", HttpMethod.GET, "tms lunch"));
		map.put("lunch/saveLunch",
				new ApiEndpointsDetails("/lunch/saveLunch", HttpMethod.POST, "tms lunch"));

		return map;
	}
	

}
