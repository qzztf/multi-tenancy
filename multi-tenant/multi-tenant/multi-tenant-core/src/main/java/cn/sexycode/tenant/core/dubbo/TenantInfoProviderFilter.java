package cn.sexycode.tenant.core.dubbo;

import cn.sexycode.tenant.TenantInfo;
import cn.sexycode.tenant.core.DefaultTenantInfo;
import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import cn.sexycode.tenant.core.TenantInfoHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * dubbo 过滤器
 *
 * @author qinzaizhen
 */
@Activate(group = Constants.PROVIDER)
public class TenantInfoProviderFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantInfoProviderFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) {
        try {
            String tenantId = RpcContext.getContext().getAttachment("tenantId");
            String tenantIdColumn = RpcContext.getContext().getAttachment("tenantIdColumn");
            DefaultTenantInfo defaultTenantInfo = new DefaultTenantInfo();
            defaultTenantInfo.setTenantIdColumn(tenantIdColumn);
            defaultTenantInfo.setTenantId(tenantId);
            setTenantInfo(defaultTenantInfo);
            return invoker.invoke(invocation);
        } catch (Exception e) {
            LOGGER.error("多租户过滤器出现异常", e);
            throw e;
        } finally {
            TenantInfoHolder.clear();
        }
    }

    private void setTenantInfo(TenantInfo tenantInfo) {
        TenantInfoHolder.setTenantInfo(tenantInfo);

    }

}
