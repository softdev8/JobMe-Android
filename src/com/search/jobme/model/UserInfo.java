package com.search.jobme.model;

import java.util.ArrayList;

public class UserInfo {
	
	String user_id;
	String first_name;
	String last_name;
	String email;
	String company;
	String company_avatar;
	String company_description;
	String avatar;
	String headline;
	String location;
	String matched;
	String latitude;
	String longitude;
	String main_cat_id;
	String sub_cat_id;
	String experience_year;
	ArrayList<Experience> experience;
	ArrayList<Education> education;
	String skill_ids;
	String online;
	String status;
	String social_id;
	String created;
	String salary_min;
	String salary_max;
	String message_count;
	String videochat_id;
	
	public String getVideochat_id() {
		return videochat_id;
	}
	public void setVideochat_id(String videochat_id) {
		this.videochat_id = videochat_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getMatched() {
		return matched;
	}
	public void setMatched(String matched) {
		this.matched = matched;
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
	public String getMain_cat_id() {
		return main_cat_id;
	}
	public void setMain_cat_id(String main_cat_id) {
		this.main_cat_id = main_cat_id;
	}
	public String getSub_cat_id() {
		return sub_cat_id;
	}
	public void setSub_cat_id(String sub_cat_id) {
		this.sub_cat_id = sub_cat_id;
	}
	public String getExperience_year() {
		return experience_year;
	}
	public void setExperience_year(String experience_year) {
		this.experience_year = experience_year;
	}
	
	public String getSkill_ids() {
		return skill_ids;
	}
	public void setSkill_ids(String skill_ids) {
		this.skill_ids = skill_ids;
	}
	public String getOnline() {
		return online;
	}
	public void setOnline(String online) {
		this.online = online;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSocial_id() {
		return social_id;
	}
	public void setSocial_id(String social_id) {
		this.social_id = social_id;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getMessage_count() {
		return message_count;
	}
	public void setMessage_count(String message_count) {
		this.message_count = message_count;
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
	public ArrayList<Experience> getExperience() {
		return experience;
	}
	public void setExperience(ArrayList<Experience> experience) {
		this.experience = experience;
	}
	public ArrayList<Education> getEducation() {
		return education;
	}
	public void setEducation(ArrayList<Education> education) {
		this.education = education;
	}
	public String getCompany_avatar() {
		return company_avatar;
	}
	public void setCompany_avatar(String company_avatar) {
		this.company_avatar = company_avatar;
	}
	public String getCompany_description() {
		return company_description;
	}
	public void setCompany_description(String company_description) {
		this.company_description = company_description;
	}
	
}
