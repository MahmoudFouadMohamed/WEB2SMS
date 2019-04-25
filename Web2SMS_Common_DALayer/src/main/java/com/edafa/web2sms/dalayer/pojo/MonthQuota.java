package com.edafa.web2sms.dalayer.pojo;

public class MonthQuota {
	
	int month;
	int count;
	
	public MonthQuota(String month, Long count) {
		this.month = Integer.parseInt(month);
		this.count = count.intValue();
	}
	
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "MonthQuota [month=" + month + ", count=" + count + "]";
	}
	
	
	

}
