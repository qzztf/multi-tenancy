package cn.sexycode.tenancy.demo;

import cn.sexycode.tenancy.demo.dubbo.DemoConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired
    DemoConsumer demoConsumer;


    @GetMapping("dubbo")
    public void dubboTest(){
        demoConsumer.consumer();
    }
}
