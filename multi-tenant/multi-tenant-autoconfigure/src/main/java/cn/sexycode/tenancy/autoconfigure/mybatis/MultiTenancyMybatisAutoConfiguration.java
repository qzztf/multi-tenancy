package cn.sexycode.tenancy.autoconfigure.mybatis;

import cn.sexycode.tenant.filter.MultiTenancyFilter;
import cn.sexycode.tenant.mybatis.MultiTenancyPlugin;
import cn.sexycode.tenant.parser.SqlParser;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import javax.annotation.PostConstruct;

/**
 * @author qinzaizhen
 */

@ConditionalOnClass(name = "org.apache.ibatis.plugin.Plugin")
@AutoConfigureAfter(name = {"com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration", "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration"})
public class MultiTenancyMybatisAutoConfiguration {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private SqlParser sqlParser;

    @Autowired
    private MultiTenancyFilter multiTenancyFilter;



    @PostConstruct
    public void addMultiTenancyInterceptor() {
        MultiTenancyPlugin plugin = new MultiTenancyPlugin();
        plugin.setSqlParser(sqlParser);
        plugin.setTenancyFilter(multiTenancyFilter);
        sqlSessionFactory.getConfiguration().addInterceptor(plugin);
    }

}
