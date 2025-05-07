package com.rnt.SecuritySystem.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Getter
@Entity
@Table(name = "temp_user_block")
public class TempUserBlock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "is_blocked", nullable = false)
	private boolean isBlocked;

	@Column(name = "blocked_at")
	private LocalDateTime blockedAt;

	@Column(name = "reason", length = 255)
	private String reason;

	@Column(name = "ip_address", length = 45)
	private String ipAddress;

	@Column(name = "attempt")
	private int attempt;

	@PrePersist
	protected void onCreate() {
		this.blockedAt = LocalDateTime.now();
	}
}
