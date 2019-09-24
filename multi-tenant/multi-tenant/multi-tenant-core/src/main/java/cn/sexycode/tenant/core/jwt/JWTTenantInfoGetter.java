package cn.sexycode.tenant.core.jwt;


import cn.sexycode.tenant.TenantInfo;
import cn.sexycode.tenant.TenantInfoGetter;
import cn.sexycode.tenant.constant.Constant;
import cn.sexycode.tenant.core.DefaultTenantInfo;
import cn.sexycode.tenant.exception.MultiTenantException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author qinzaizhen
 * 从Jwt token中解析租户id
 */
public class JWTTenantInfoGetter implements TenantInfoGetter {

    @Override
    public TenantInfo getTenantInfo(Map<String, Object> payload) {

        String tenantId = payload.getOrDefault(Constant.TENANT_ID, "").toString();
        if (!StringUtils.isBlank(tenantId)) {
            TenantInfo tenantInfo = new DefaultTenantInfo();
            tenantInfo.setTenantId(tenantId);
            return tenantInfo;
        }

        String token = payload.getOrDefault("accessToken", "").toString();
        if (!StringUtils.isBlank(token)) {
            /*String userIdByToken = TokenUtils.getUserIdByToken(token);
            //TODO 暂时用分号分割
            String[] split = StringUtils.split(userIdByToken, ";");
            if (split.length < 2) {
                throw new MultiTenantException("没有找到租户id");
            }

            tenantId = split[1];*/
            TenantInfo tenantInfo = new DefaultTenantInfo();
            tenantInfo.setTenantId(tenantId);
            return tenantInfo;

        }

        throw new MultiTenantException("没有找到租户id");
    }
}
