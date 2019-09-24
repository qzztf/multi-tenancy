package cn.sexycode.tenancy.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author qinzaizhen
 */
@SpringBootApplication
public class MultiTenancyStarterDubboConsumerApplication {

    public static void main(String[] args) {

        SpringApplication.run(MultiTenancyStarterDubboConsumerApplication.class, args);
        /*ExecutorService executorService = Executors.newScheduledThreadPool(2);

        new Thread(new Runnable(){
            @Override
            public void run() {
                *//*DefaultTenantInfo tenantInfo = new DefaultTenantInfo();
                tenantInfo.setTenantId("1111");
                tenantInfo.setTenantIdColumn("id");
                TenantInfoHolder.setTenantInfo(tenantInfo);*//*
//                accountDao.test();
                DefaultTenantInfo tenantInfo = new DefaultTenantInfo();
                tenantInfo.setTenantId("rrrr2222");
//        tenantInfo.setTenantIdColumn("id");
                TenantInfoHolder.setTenantInfo(tenantInfo);
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            accountDao.test();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                executorService.submit(task);
            }
        }).start();
        new Thread(new Runnable(){
            @Override
            public void run() {
                *//*DefaultTenantInfo tenantInfo = new DefaultTenantInfo();
                tenantInfo.setTenantId("2222");
                tenantInfo.setTenantIdColumn("id");
                TenantInfoHolder.setTenantInfo(tenantInfo);*//*
//                accountDao.test();
                DefaultTenantInfo tenantInfo = new DefaultTenantInfo();
                tenantInfo.setTenantId("rrrr33333");
//        tenantInfo.setTenantIdColumn("id");
                TenantInfoHolder.setTenantInfo(tenantInfo);
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            accountDao.test();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                executorService.submit(task);
            }
        }).start();



     *//*   Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    accountDao.test();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        executorService.submit(task);*//*
// 额外的处理，生成修饰了的对象executorService
//        executorService = TtlExecutors.getTtlExecutorService(executorService);
*/
        /*DefaultTenantInfo tenantInfo = new DefaultTenantInfo();
        tenantInfo.setTenantId("1111");
        tenantInfo.setTenantIdColumn("id");
        TenantInfoHolder.setTenantInfo(tenantInfo);
        accountDao.test();*/
    }

}

