package com.search.jobme.model;

import java.util.ArrayList;

public class JobInfo {
	
	String job_id;
	String user_id;
	String job_title;
	String job_function;
	String location;
	String latitude;
	String longitude;
	String radius;
	String relocate;
	String remote;
	String experience_year;
	String salary_min;
	String salary_max;
	String skill_ids;
	String perks;
	String description;
	String qualification;
	String created;
	UserInfo userInfo;
	
	public String getJob_id() {
		return job_id;
	}
	public void setJob_id(String job_id) {
		this.job_id = job_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getJob_title() {
		return job_title;
	}
	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}
	public String getJob_function() {
		return job_function;
	}
	public void setJob_function(String job_function) {
		this.job_function = job_function;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getRadius() {
		return radius;
	}
	public void setRadius(String radius) {
		this.radius = radius;
	}
	public String getRelocate() {
		return relocate;
	}
	public void setRelocate(String relocate) {
		this.relocate = relocate;
	}
	public String getRemote() {
		return remote;
	}
	public void setRemote(String remote) {
		this.remote = remote;
	}
	public String getExperience_year() {
		return experience_year;
	}
	public void setExperience_year(String experience_year) {
		this.experience_year = experience_year;
	}
	public String getSalary_min() {
		return salary_min;
	}
	public void setSalary_min(String salary_min) {
		this.salary_min = salary_min;
	}
	public String getSalary_max() {
		return salary_max;
	}
	public void setSalary_max(String salary_max) {
		this.salary_max = salary_max;
	}
	public String getSkill_ids() {
		return skill_ids;
	}
	public void setSkill_ids(String skill_ids) {
		this.skill_ids = skill_ids;
	}
	public String getPerks() {
		return perks;
	}
	public void setPerks(String perks) {
		this.perks = perks;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
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
