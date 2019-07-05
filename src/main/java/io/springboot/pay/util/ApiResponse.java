package io.springboot.pay.util;

public class ApiResponse {

    private static ApiMessage getApiMessage(ApiMessage apiMessage) {
        return apiMessage == null ? new ApiMessage() : apiMessage;
    }

    /**
     * 拼凑返回
     *
     * @param id
     * @param apiMessage
     * @return
     */
    public static ApiMessage message(String id, ApiMessage apiMessage) {
        ApiMessage message = getApiMessage(apiMessage);
        message.setId(id);
        return message;
    }

    /**
     * 有msg提示
     *
     * @param id
     * @param apiMessage
     * @param msg        错误参数提示
     * @return
     */
    public static ApiMessage message(String id, ApiMessage apiMessage, String msg) {
        ApiMessage message = message(id, null, apiMessage, msg);
        return message;
    }

    /**
     * 无msg提示直接取status中文
     *
     * @param id
     * @param data
     * @param apiMessage
     * @return
     */
    public static ApiMessage message(String id, Object data, ApiMessage apiMessage) {
        ApiMessage message = message(id, apiMessage);
        message.setData(data);
        return message;
    }

    /**
     * 详细返回
     *
     * @param id
     * @param data
     * @param apiMessage
     * @param msg
     * @return
     */
    public static ApiMessage message(String id, Object data, ApiMessage apiMessage, String msg) {
        ApiMessage message = message(id, data, apiMessage);
        message.setMessage(msg);
        return message;
    }

    /**
     * 简易返回
     *
     * @return
     */
    public static ApiMessage success() {
        return success(null);
    }

    /**
     * 简易返回
     *
     * @param id
     * @return
     */
    public static ApiMessage success(String id) {
        return success(id, null);
    }

    /**
     * 简易返回
     *
     * @param id
     * @param data
     * @return
     */
    public static ApiMessage success(String id, Object data) {
        ApiMessage message = new ApiMessage();
        message.setId(id);
        message.setData(data);
        message.setCode(ApiMessage.Status.SUCCESS.getCode());
        message.setStatus(ApiMessage.Status.SUCCESS);
        return message;
    }
}
