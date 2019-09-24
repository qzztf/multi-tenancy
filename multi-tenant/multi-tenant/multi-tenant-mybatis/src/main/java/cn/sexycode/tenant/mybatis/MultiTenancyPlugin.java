package cn.sexycode.tenant.mybatis;

import cn.sexycode.tenant.TenantInfo;
import cn.sexycode.tenant.core.parser.DefaultSqlParser;
import cn.sexycode.tenant.filter.MultiTenancyFilter;
import cn.sexycode.tenant.parser.SqlParser;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author qinzaizhen
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class MultiTenancyPlugin implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(MultiTenancyPlugin.class);

    /**
     * 数据库中租户ID的列名
     */
    private static final String TENANT_ID_COLUMN = TenantInfo.DEFAULT_TENANT_ID_COLUMN;
    /**
     * 属性参数信息
     */
    private Properties properties = new Properties();
    /**
     * sql处理
     */
    private SqlParser sqlParser;

    private MultiTenancyFilter tenancyFilter;

    public MultiTenancyPlugin(SqlParser sqlParser) {
        this.sqlParser = sqlParser;
        properties.put("tenantIdColumn", "tenant_id");
        properties.put("filterDefault", "false");
        properties.put("dialect", "mysql");
    }

    public MultiTenancyPlugin() {
        this(null);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        mod(invocation);
        return invocation.proceed();
    }

    /**
     * 更改MappedStatement为新的
     *
     * @param invocation
     * @throws Throwable
     */
    public void mod(Invocation invocation) {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        if (!tenancyFilter.doStatementFilter(ms.getId())) {
            logger.debug("跳过解析statement: {}", ms.getId());
            return;
        }
        BoundSql boundSql = ms.getBoundSql(invocation.getArgs()[1]);
        String sql = boundSql.getSql();
        if (!tenancyFilter.doStatementFilter(sql)) {
            logger.debug("跳过解析sql: {}", sql);
            return;
        }
        /**
         * 根据已有BoundSql构造新的BoundSql
         *
         */
        //更改后的sql
        BoundSql newBoundSql = new BoundSql(
                ms.getConfiguration(),
                sqlParser.process(sql),
                boundSql.getParameterMappings(),
                boundSql.getParameterObject());

        MappedStatement newMs = buildMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));

        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        /**
         * 替换MappedStatement
         */
        invocation.getArgs()[0] = newMs;
    }

    /**
     * 根据已有MappedStatement构造新的MappedStatement
     */
    private MappedStatement buildMappedStatement(MappedStatement ms, SqlSource sqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), sqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }


    /**
     * 获取配置信息
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        if (properties == null) {
            return;
        }
        this.properties.putAll(properties);
        /*if (tenantInfo == null) {

            Object tenantInfo = this.properties.get("tenantInfo");
            if (tenantInfo != null) {
                setTenantInfo((TenantInfo) tenantInfo);
            } else {
                //租户字段
                this.tenantInfo = new DefaultTenantInfo();
                this.tenantInfo.setTenantIdColumn(PropertiesUtil.getProperty(properties, "tenantIdColumn", TENANT_ID_COLUMN));
            }
        }*/
        if (sqlParser == null) {
            //sql处理
            SqlParser defaultSqlParser = new DefaultSqlParser();
            setSqlParser(defaultSqlParser);
        }

    }

    public void setSqlParser(SqlParser sqlParser) {
        this.sqlParser = sqlParser;
    }

    public MultiTenancyFilter getTenancyFilter() {
        return tenancyFilter;
    }

    public void setTenancyFilter(MultiTenancyFilter tenancyFilter) {
        this.tenancyFilter = tenancyFilter;
    }

    /**
     * 用于构造新MappedStatement
     */
    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

}
