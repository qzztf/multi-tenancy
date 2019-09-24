package cn.sexycode.tenant.core.dubbo;

import cn.sexycode.tenant.TenantInfo;
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

import java.util.HashMap;
import java.util.Map;


/**
 * dubbo 过滤器
 *
 * @author qinzaizhen
 */
@Activate(group = Constants.CONSUMER)
public class TenantInfoConsumerFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantInfoConsumerFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) {
        try {
            TenantInfo info = getTenantInfo();
            if (info != null) {
                Map<String, String> tenantInfo = new HashMap<>(8);
                tenantInfo.put("tenantId", info.getTenantId());
                tenantInfo.put("tenantIdColumn", info.getTenantIdColumn());
                RpcContext.getContext().setAttachments(tenantInfo);
            }
            return invoker.invoke(invocation);
        } catch (Exception e) {
            LOGGER.error("多租户过滤器出现异常", e);
            throw e;
        }
    }

    /**
     * @return TenantInfo
     */
    protected TenantInfo getTenantInfo() {
        //从web层传递过来的tenant info
        return TenantInfoHolder.getTenantInfo();
    }

}
