package cn.sexycode.tenancy.demo;

import cn.sexycode.tenant.TenantInfoGetter;
import cn.sexycode.tenant.core.DefaultTenantInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qinzaizhen
 */
@Configuration
public class JavaConfig {
    @Bean
    public TenantInfoGetter tenantInfoGetter() {
        return (param) -> {
            DefaultTenantInfo tenantInfo = new DefaultTenantInfo();
            tenantInfo.setTenantId("rrrr2222");
            return tenantInfo;
        };
    }
}
