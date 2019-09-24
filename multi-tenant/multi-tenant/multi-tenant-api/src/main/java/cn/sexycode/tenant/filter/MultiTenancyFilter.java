package cn.sexycode.tenant.filter;

import java.util.Properties;

/**
 * @author qinzaizhen
 */
public interface MultiTenancyFilter {
    void setConfig(Properties properties);

    /**
     * 按照表名进行过滤
     */
    boolean doTableFilter(String table);

    /**
     * 按照statementId进行过滤
     */
    boolean doStatementFilter(String statementId);
}