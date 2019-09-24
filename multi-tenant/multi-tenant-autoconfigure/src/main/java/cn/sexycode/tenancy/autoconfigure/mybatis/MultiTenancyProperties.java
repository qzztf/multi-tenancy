package cn.sexycode.tenancy.autoconfigure.mybatis;

import cn.sexycode.tenant.TenantInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author qinzaizhen
 */
@ConfigurationProperties(prefix = "multi-tenant")
public class MultiTenancyProperties {
    private String tenantIdColumn = TenantInfo.DEFAULT_TENANT_ID_COLUMN;

    private String exclusionsUrl;

    /**
     * 忽略的sql语句和mybatis statement id
     */
    private List<String> ignoreSql;

    public String getExclusionsUrl() {
        return exclusionsUrl;
    }

    public void setExclusionsUrl(String exclusionsUrl) {
        this.exclusionsUrl = exclusionsUrl;
    }

    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    public void setTenantIdColumn(String tenantIdColumn) {
        this.tenantIdColumn = tenantIdColumn;
    }

    public List<String> getIgnoreSql() {
        return ignoreSql;
    }

    public void setIgnoreSql(List<String> ignoreSql) {
        this.ignoreSql = ignoreSql;
    }

    @Override
    public String toString() {
        return "MultiTenancyProperties{" +
                "tenantIdColumn='" + tenantIdColumn + '\'' +
                ", exclusionsUrl='" + exclusionsUrl + '\'' +
                ", ignoreSql=" + ignoreSql +
                '}';
    }
}
