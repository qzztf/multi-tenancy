package cn.sexycode.tenancy.demo;

import cn.sexycode.tenancy.demo.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @Autowired
    AccountDao accountDao;

    @GetMapping
    public void test() {
        accountDao.test();
    }
}
