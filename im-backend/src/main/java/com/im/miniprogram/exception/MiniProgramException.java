package com.im.miniprogram.exception;

/**
 * 小程序异常
 */
public class MiniProgramException extends Exception {
    
    private final String appId;
    private final ErrorCode errorCode;
    private final long timestamp;
    
    public enum ErrorCode {
        // 系统错误
        SYSTEM_ERROR(1000, "系统错误"),
        UNKNOWN_ERROR(1001, "未知错误"),
        
        // 沙箱错误
        SANDBOX_INIT_FAILED(2001, "沙箱初始化失败"),
        SANDBOX_NOT_READY(2002, "沙箱未就绪"),
        SANDBOX_ALREADY_RUNNING(2003, "沙箱已在运行"),
        SANDBOX_TERMINATED(2004, "沙箱已终止"),
        
        // API错误
        API_NOT_FOUND(3001, "API不存在"),
        API_NOT_ALLOWED(3002, "API不允许调用"),
        API_RATE_LIMITED(3003, "API调用过于频繁"),
        API_CALL_FAILED(3004, "API调用失败"),
        
        // 网络错误
        NETWORK_ERROR(4001, "网络错误"),
        REQUEST_TIMEOUT(4002, "请求超时"),
        INVALID_RESPONSE(4003, "响应无效"),
        
        // 安全错误
        SECURITY_VIOLATION(5001, "安全策略违规"),
        PERMISSION_DENIED(5002, "权限被拒绝"),
        RESOURCE_LIMIT_EXCEEDED(5003, "资源限制超出"),
        
        // 文件错误
        FILE_NOT_FOUND(6001, "文件不存在"),
        FILE_READ_ERROR(6002, "文件读取错误"),
        FILE_WRITE_ERROR(6003, "文件写入错误"),
        STORAGE_FULL(6004, "存储空间已满"),
        
        // 生命周期错误
        INVALID_STATE(7001, "无效状态"),
        PAGE_NOT_FOUND(7002, "页面不存在");
        
        private final int code;
        private final String message;
        
        ErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }
        
        public int getCode() { return code; }
        public String getMessage() { return message; }
    }
    
    public MiniProgramException(String message) {
        super(message);
        this.appId = null;
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
        this.timestamp = System.currentTimeMillis();
    }
    
    public MiniProgramException(String message, String appId) {
        super(message);
        this.appId = appId;
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
        this.timestamp = System.currentTimeMillis();
    }
    
    public MiniProgramException(String message, String appId, Throwable cause) {
        super(message, cause);
        this.appId = appId;
        this.errorCode = ErrorCode.UNKNOWN_ERROR;
        this.timestamp = System.currentTimeMillis();
    }
    
    public MiniProgramException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.appId = null;
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }
    
    public MiniProgramException(ErrorCode errorCode, String appId) {
        super(errorCode.getMessage());
        this.appId = appId;
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }
    
    public MiniProgramException(ErrorCode errorCode, String message, String appId) {
        super(message);
        this.appId = appId;
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }
    
    // ============ Getter方法 ============
    
    public String getAppId() { return appId; }
    public ErrorCode getErrorCode() { return errorCode; }
    public int getCode() { return errorCode.getCode(); }
    public long getTimestamp() { return timestamp; }
    
    /**
     * 转换为错误响应对象
     */
    public java.util.Map<String, Object> toErrorResponse() {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("success", false);
        response.put("errorCode", errorCode.getCode());
        response.put("errorMessage", getMessage());
        if (appId != null) {
            response.put("appId", appId);
        }
        return response;
    }
}
