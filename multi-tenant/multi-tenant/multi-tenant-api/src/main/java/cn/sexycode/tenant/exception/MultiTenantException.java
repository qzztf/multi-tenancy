/**
 * <pre>
 *  Copyright (C) , 大道金服
 *  @description: 多租户异常
 *  @author: liuhui
 *  @email: liuhui@ddjf.com.cn
 *  @date: 2019/3/7 15:48
 *  @project: multi-tenant-starter
 *  </pre>
 */
package cn.sexycode.tenant.exception;

/**
 *  <pre>
 *  @description: 多租户异常
 *  @author: liuhui
 *  @email: liuhui@ddjf.com.cn 
 *  @date: 2019/3/7 15:48
 *  @project: multi-tenant-starter
 *  </pre>
 */
public class MultiTenantException extends RuntimeException {
    public MultiTenantException() {
    }

    public MultiTenantException(String message) {
        super(message);
    }

    public MultiTenantException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultiTenantException(Throwable cause) {
        super(cause);
    }

    public MultiTenantException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}