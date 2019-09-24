package cn.sexycode.tenancy.demo.dubbo.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import cn.sexycode.tenancy.demo.dubbo.DemoConsumer;
import cn.sexycode.tenancy.demo.dubbo.DemoService;
import org.springframework.stereotype.Service;

@Service
public class DemoConsumerImpl implements DemoConsumer {
    @Reference(url = "dubbo://10.96.2.236:20881")
    DemoService demoService;

    @Override
    public void consumer() {
        demoService.test();
    }
}
