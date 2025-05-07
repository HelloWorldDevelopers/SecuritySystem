package com.rnt.SecuritySystem.securityconfig;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.rnt.SecuritySystem.filter.JWTAuthenticationEntryPoint;
import com.rnt.SecuritySystem.filter.JwtRequestFilter;
import com.rnt.SecuritySystem.service.CustomeUserDetailsService;
import com.rnt.SecuritySystem.util.AuthenticationUtil;

 

/**
 * @author Vishal Dabade
 *
 */
@Configuration
@EnableWebSecurity
@EnableWebMvc
@SuppressWarnings("deprecation")
public class MyConfig implements WebMvcConfigurer {

	@Autowired
	CustomeUserDetailsService customUserDetails;
	@Autowired
	JwtRequestFilter authenticationFilter;
	@Autowired
	JWTAuthenticationEntryPoint authenticationEntryPoint;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors(cors->cors.configurationSource(corsConfigurationSource()));
		CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
		repo.setCookiePath("/");

 		http.csrf(csrf -> csrf.csrfTokenRepository(repo)
				.ignoringAntMatchers("/api/v1/auth/**")
				);
		http.authorizeHttpRequests(auth -> auth.antMatchers(AuthenticationUtil.PUBLIC_URLS).permitAll()
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll().requestMatchers(CorsUtils::isPreFlightRequest)
				.permitAll().anyRequest().authenticated());

		http.exceptionHandling(handling -> handling.authenticationEntryPoint(authenticationEntryPoint));

		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
		http.authenticationProvider(daoAuthenticationProvider());

		return http.build();

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new MessageDigestPasswordEncoder("SHA-1");
	}


	@Bean
	public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {

		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.customUserDetails);
		provider.setPasswordEncoder(passwordEncoder());

		return provider;
	}

	/**
	 * This method will configure cross origin access to api's.
	 * 
	 * @since version 1.0
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/" + "**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*").exposedHeaders("*");
	}

	private CorsConfigurationSource corsConfigurationSource() {
		return new CorsConfigurationSource() {
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration cfg = new CorsConfiguration();
				cfg.setAllowedOrigins(Arrays.asList(
	                    "http://localhost:3000",
	                    "http://http://172.20.1.217:8080"
	                    
	                ));
				cfg.setAllowedMethods(Collections.singletonList("*"));
				cfg.setAllowCredentials(true);
				cfg.setAllowedHeaders(Collections.singletonList("*"));
 				cfg.setAllowedHeaders(Arrays.asList("X-XSRF-TOKEN", "Content-Type", "Authorization"));

				cfg.setMaxAge(3600L);
				return cfg;
			}
		};
	}

}
