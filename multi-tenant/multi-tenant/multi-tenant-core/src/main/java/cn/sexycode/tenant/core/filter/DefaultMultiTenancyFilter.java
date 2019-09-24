package cn.sexycode.tenant.core.filter;

import cn.sexycode.tenant.filter.MultiTenancyFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author qinzaizhen
 */
public class DefaultMultiTenancyFilter implements MultiTenancyFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMultiTenancyFilter.class);
    public static final String IGNORE_SQL_PROP_KEY = "ignoreSqls";
    private static List<String> ignoreSqls = new ArrayList<String>() {
        {
            add("SELECT LAST_INSERT_ID()");
        }
    };

    @Override
    public void setConfig(Properties properties) {
        Object o = properties.get(IGNORE_SQL_PROP_KEY);
        if (o instanceof List){
            List<String> list = (List<String>) o;
            list.forEach(oo -> {
                if (oo != null) {
                    addIgnoreSql(( oo));
                }
            });

        }
    }

    /**
     * 添加忽略的sql
     * @param ignoreSql
     */
    private void addIgnoreSql(String ignoreSql) {
        LOGGER.debug("添加忽略sql: {}", ignoreSql);
        ignoreSqls.add(ignoreSql);
    }

    @Override
    public boolean doTableFilter(String table) {
        return true;
    }

    @Override
    public boolean doStatementFilter(String statementId) {
        for (String ignoreSql : ignoreSqls) {
            if (ignoreSql.trim().equalsIgnoreCase(statementId)) {
                return false;
            }
        }
        return true;
    }
}
