package com.search.jobme.model;

import java.util.ArrayList;

public class NotificationModel {
	
	String employer_id;
	String noti_id;
	String created;
	UserInfo userInfo;
	
	public String getEmployer_id() {
		return employer_id;
	}
	public void setEmployer_id(String employer_id) {
		this.employer_id = employer_id;
	}
	public String getNoti_id() {
		return noti_id;
	}
	public void setNoti_id(String noti_id) {
		this.noti_id = noti_id;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
}
