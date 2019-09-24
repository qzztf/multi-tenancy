package cn.sexycode.tenant.core.web;

import cn.sexycode.tenant.TenantInfo;
import cn.sexycode.tenant.TenantInfoGetter;
import cn.sexycode.tenant.constant.Constant;
import cn.sexycode.tenant.core.SpringContextUtil;
import cn.sexycode.tenant.core.TenantInfoHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author qinzaizhen
 */
public class TenantInfoFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantInfoFilter.class);

    public static final String PARAM_NAME_EXCLUSIONS = "exclusions";
    private Set<String> excludesPattern;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("init tenantInfoFilter");
        String param = filterConfig.getInitParameter(PARAM_NAME_EXCLUSIONS);
        if (param != null && param.trim().length() != 0) {
            this.excludesPattern = new HashSet(Arrays.asList(param.split("\\s*,\\s*")));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (isExclusion(req.getRequestURI())) {
            chain.doFilter(request, response);
        } else {

            try {
                Map<String, Object> param = new HashMap<>();
                String accessToken = ((HttpServletRequest) request).getHeader(Constant.ACCESS_TOKEN);
                if (StringUtils.isBlank(accessToken)) {
                    accessToken = request.getParameter(Constant.ACCESS_TOKEN);
                }
                param.put("accessToken", Optional.ofNullable(accessToken).orElse(""));

                String tenantId = "";
                if (StringUtils.isBlank(accessToken)) {
                    tenantId = ((HttpServletRequest) request).getHeader(Constant.TENANT_ID);
                }
                if (StringUtils.isBlank(tenantId)) {
                    tenantId = request.getParameter(Constant.TENANT_ID);
                }
                param.put(Constant.TENANT_ID, Optional.ofNullable(tenantId).orElse(""));
                setTenantInfo(getTenantInfo(param));
                chain.doFilter(request, response);
            } catch (Exception e) {
                LOGGER.error("多租户过滤器出现异常", e);
                throw e;
            } finally {
                TenantInfoHolder.clear();
            }

        }

    }

    private void setTenantInfo(TenantInfo tenantInfo) {
        TenantInfoHolder.setTenantInfo(tenantInfo);
    }

    /**
     * @return TenantInfo
     */
    protected TenantInfo getTenantInfo(Map<String, Object> param) {
        return SpringContextUtil.getBean(TenantInfoGetter.class).getTenantInfo(param);
    }

    @Override
    public void destroy() {
        TenantInfoHolder.clear();
    }

    public boolean isExclusion(String requestURI) {
        if (this.excludesPattern == null) {
            return false;
        } else {
            Iterator iterator = this.excludesPattern.iterator();
            String pattern;
            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                pattern = (String) iterator.next();
            } while (!PatternMatchUtils.simpleMatch(pattern, requestURI));

            return true;
        }
    }
}
