package yjr.wechat.mp.utils;

import lombok.Data;

@Data
public class ResData {
	
	private Integer code;
	private String message;
	private boolean success;
	private Object data;
	
	public static ResData success(Object data) {
		ResData res = new ResData();
		res.code = 0;
		res.success = true;
		res.data = data;
		return res;
	}
	
	public static ResData error(int code,String message) {
		ResData res = new ResData();
		res.code = code;
		res.data = null;
		res.success = false;
		res.message = message;
		return res;
	}
	public static ResData error(String message) {
		ResData res = new ResData();
		res.code = -1;
		res.data = null;
		res.success = false;
		res.message = message;
		return res;
	}
	public static ResData error() {
		ResData res = new ResData();
		res.code = -1;
		res.success = false;
		return res;
	}

}
