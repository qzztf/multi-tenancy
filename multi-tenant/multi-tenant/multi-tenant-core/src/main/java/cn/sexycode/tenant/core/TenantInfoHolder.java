package cn.sexycode.tenant.core;

import cn.sexycode.tenant.TenantInfo;
import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author qinzaizhen
 */
public class TenantInfoHolder {
    private static ThreadLocal<TenantInfo> tenantInfoThreadLocal = new TransmittableThreadLocal<>();

    private TenantInfoHolder(){}

    public static void setTenantInfo(TenantInfo tenantInfo) {
        tenantInfoThreadLocal.set(tenantInfo);
    }

    public static TenantInfo getTenantInfo() {
        return tenantInfoThreadLocal.get();
    }

    public static TenantInfo clearTenantInfo() {
        TenantInfo tenantInfo = getTenantInfo();
        tenantInfoThreadLocal.remove();
        return tenantInfo;
    }

    public static void clear(){
        tenantInfoThreadLocal.remove();
    }
}
