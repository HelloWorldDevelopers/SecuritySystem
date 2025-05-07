package com.rnt.SecuritySystem.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessDTO {
	private String useCase;
	private char readAccess;
	private char writeAccess;
	private char editAccess;
	private char deleteAccess;

	@JsonCreator
	public AccessDTO(@JsonProperty("useCase") String useCase, @JsonProperty("readAccess") char readAccess,
			@JsonProperty("writeAccess") char writeAccess, @JsonProperty("editAccess") char editAccess,
			@JsonProperty("deleteAccess") char deleteAccess) {
		this.useCase = useCase;
		this.readAccess = readAccess;
		this.writeAccess = writeAccess;
		this.editAccess = editAccess;
		this.deleteAccess = deleteAccess;
	}

	public String getUseCase() {
		return useCase;
	}

	public void setUseCase(String useCase) {
		this.useCase = useCase;
	}

	public char getReadAccess() {
		return readAccess;
	}

	public void setReadAccess(char readAccess) {
		this.readAccess = readAccess;
	}

	public char getWriteAccess() {
		return writeAccess;
	}

	public void setWriteAccess(char writeAccess) {
		this.writeAccess = writeAccess;
	}

	public char getEditAccess() {
		return editAccess;
	}

	public void setEditAccess(char editAccess) {
		this.editAccess = editAccess;
	}

	public char getDeleteAccess() {
		return deleteAccess;
	}

	public void setDeleteAccess(char deleteAccess) {
		this.deleteAccess = deleteAccess;
	}
}
