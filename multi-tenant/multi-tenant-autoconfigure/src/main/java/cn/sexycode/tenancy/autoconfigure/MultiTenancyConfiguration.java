package cn.sexycode.tenancy.autoconfigure;

import cn.sexycode.tenancy.autoconfigure.mybatis.MultiTenancyProperties;
import cn.sexycode.tenant.TenantInfoGetter;
import cn.sexycode.tenant.core.DefaultTenantInfo;
import cn.sexycode.tenant.core.SpringContextUtil;
import cn.sexycode.tenant.core.filter.DefaultMultiTenancyFilter;
import cn.sexycode.tenant.core.jwt.JWTTenantInfoGetter;
import cn.sexycode.tenant.core.parser.DefaultSqlParser;
import cn.sexycode.tenant.filter.MultiTenancyFilter;
import cn.sexycode.tenant.parser.SqlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Properties;

/**
 * @author qinzaizhen
 */
@Configuration
@EnableConfigurationProperties(MultiTenancyProperties.class)
public class MultiTenancyConfiguration implements ApplicationContextAware {

    @Autowired
    private MultiTenancyProperties multiTenancyProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (!StringUtils.isEmpty(multiTenancyProperties.getTenantIdColumn())) {
            DefaultTenantInfo.setDefaultTenantIdColumn(multiTenancyProperties.getTenantIdColumn());
        }
        SpringContextUtil.setContext(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlParser sqlParser() {
        System.out.println("init sqlparser.");
        return new DefaultSqlParser();
    }

    @Bean
    @ConditionalOnMissingBean
    public MultiTenancyFilter multiTenancyFilter() {
        DefaultMultiTenancyFilter defaultMultiTenancyFilter = new DefaultMultiTenancyFilter();
        Properties properties = new Properties();
        List<String> ignoreSql = multiTenancyProperties.getIgnoreSql();
        if (ignoreSql != null) {
            properties.put(DefaultMultiTenancyFilter.IGNORE_SQL_PROP_KEY, ignoreSql);
        }
        defaultMultiTenancyFilter.setConfig(properties);
        return defaultMultiTenancyFilter;
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantInfoGetter tenantInfoGetter() {
        return new JWTTenantInfoGetter();
    }

}
