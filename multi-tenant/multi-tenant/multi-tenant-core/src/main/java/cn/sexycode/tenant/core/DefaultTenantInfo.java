package cn.sexycode.tenant.core;

import cn.sexycode.tenant.TenantInfo;

import java.util.Properties;

/**
 * @author qinzaizhen
 */
public class DefaultTenantInfo implements TenantInfo {
    private static String defaultTenantIdColumn = DEFAULT_TENANT_ID_COLUMN;
    private String tenantIdColumn = defaultTenantIdColumn;
    private Properties properties;
    private String tenantId;

    @Override
    public String getTenantId() {
        return this.tenantId;
    }

    @Override
    public String getTenantIdColumn() {
        return this.tenantIdColumn;
    }

    @Override
    public TenantInfo setTenantIdColumn(String tenantIdColumn) {
        this.tenantIdColumn = tenantIdColumn;
        return this;
    }

    @Override
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public static void setDefaultTenantIdColumn(String defaultTenantIdColumn) {
        DefaultTenantInfo.defaultTenantIdColumn = defaultTenantIdColumn;
    }
}
