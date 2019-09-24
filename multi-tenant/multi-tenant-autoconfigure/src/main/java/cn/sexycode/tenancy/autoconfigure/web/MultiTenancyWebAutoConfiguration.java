package cn.sexycode.tenancy.autoconfigure.web;

import cn.sexycode.tenancy.autoconfigure.mybatis.MultiTenancyProperties;
import cn.sexycode.tenant.TenantInfoGetter;
import cn.sexycode.tenant.core.filter.DefaultMultiTenancyFilter;
import cn.sexycode.tenant.core.jwt.JWTTenantInfoGetter;
import cn.sexycode.tenant.core.web.TenantInfoFilter;
import cn.sexycode.tenant.filter.MultiTenancyFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

/**
 * @author qinzaizhen
 */
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class MultiTenancyWebAutoConfiguration {

    @Autowired
    private MultiTenancyProperties multiTenancyProperties;

    @Bean
    public FilterRegistrationBean<TenantInfoFilter> configureTenantFilter() {
        FilterRegistrationBean<TenantInfoFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantInfoFilter());
        registrationBean.setUrlPatterns(Collections.singletonList("/*"));
        if (StringUtils.isNotBlank(multiTenancyProperties.getExclusionsUrl())) {
            registrationBean.addInitParameter("exclusions", multiTenancyProperties.getExclusionsUrl());
        }
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantInfoGetter tenantInfoGetter() {
        return new JWTTenantInfoGetter();
    }

    @Bean
    @ConditionalOnMissingBean
    public MultiTenancyFilter multiTenancyFilter() {
        return new DefaultMultiTenancyFilter();
    }

}