package cn.sexycode.tenant;

/**
 * @author qinzaizhen
 */
public interface TenantInfo {
    String DEFAULT_TENANT_ID_COLUMN = "tenant_id";

    Class DEFAULT_TENANT_ID_COLUMN_TYPE = Long.class;

    /**
     * 租户id
     * @return
     */
    String getTenantId();

    void setTenantId(String tenantId);

    /**
     * 租户列
     * @return
     */
    String getTenantIdColumn();

    TenantInfo setTenantIdColumn(String tenantIdColumn);

    /**
     * 租户列类型
     * @return
     */
    default Class getTenantIdColumnType() {
        return DEFAULT_TENANT_ID_COLUMN_TYPE;
    }

    default void setTenantIdColumnType(Class clazz) {
        setTenantIdColumnType(DEFAULT_TENANT_ID_COLUMN_TYPE);
    }
}