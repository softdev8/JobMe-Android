package com.search.jobme.model;

public class HistroyChatModel {

	String sender, message, created;

	public HistroyChatModel(String sender, String message, String created) {

		this.sender = sender;
		this.message = message;
		this.created = created;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

}
