package cn.sexycode.tenant;

import java.util.Map;

/**
 * TenantInfo设置器
 * @author qinzaizhen
 */
public interface TenantInfoGetter {
    /**
     * 获取 tenantInfo
     * @return TenantInfo
     */
    TenantInfo getTenantInfo(Map<String, Object> payload);
}
