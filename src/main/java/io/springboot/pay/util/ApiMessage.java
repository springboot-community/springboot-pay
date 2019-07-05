package io.springboot.pay.util;

public class ApiMessage {
	public static final ApiMessage SUCCESS = new ApiMessage(ApiMessage.Status.SUCCESS.getCode(),ApiMessage.Status.SUCCESS);
	public static final ApiMessage BAD_REQUEST = new ApiMessage(ApiMessage.Status.BAD_REQUEST.getCode(),ApiMessage.Status.BAD_REQUEST);
	public static final ApiMessage SERVER_ERROR = new ApiMessage(ApiMessage.Status.SERVER_ERROR.getCode(),ApiMessage.Status.SERVER_ERROR);
	public static final ApiMessage PRODUCT_LACK = new ApiMessage(ApiMessage.Status.PRODUCT_LACK.getCode(),ApiMessage.Status.PRODUCT_LACK);

	public enum Status {

		SUCCESS(200, "OK"),
		BAD_REQUEST(400, "无效请求"),
		SERVER_ERROR(500, "服务器异常"),
		PRODUCT_LACK(2001,"库存不足"),
		;

		private int code;
		private String desc;

		Status(String desc) {
			this.desc = desc;
		}

		Status(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
	
	private String id;
	
	private Integer code;
	// 提示消息
	private String message;
	// 数据
	private Object data;
	// 状态
	private Status status;

	public ApiMessage() {
	}

	public ApiMessage(Integer code, Status status) {
		this.code = code;
		this.status = status;
	}

	public ApiMessage(String id, Integer code, Status status, Object data) {
		this.id = id;
		this.code = code;
		this.status = status;
		this.data = data;
	}

	public ApiMessage(String id, Integer code, String message, Status status, Object data) {
		this.id = id;
		this.code = code;
		this.message = message;
		this.status = status;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		if (this.message == null && this.status != null) {
			return this.status.getDesc();
		}
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
