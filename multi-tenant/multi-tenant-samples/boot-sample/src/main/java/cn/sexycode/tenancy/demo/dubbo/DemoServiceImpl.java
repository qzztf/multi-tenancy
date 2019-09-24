package cn.sexycode.tenancy.demo.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import cn.sexycode.tenancy.demo.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = DemoService.class)
public class DemoServiceImpl implements DemoService {
    @Autowired
    AccountDao accountDao;

    @Override
    public void test() {
        System.out.println("demo service");
        accountDao.test();
    }
}
