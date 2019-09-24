package cn.sexycode.tenant.core;

import cn.sexycode.tenant.core.parser.DefaultSqlParser;
import cn.sexycode.tenant.parser.SqlParser;

/**
 * @author qinzaizhen
 */
public class SqlParserUtil {


    public static String process(SqlParser parser, String sql) {
        return parser.process(sql);
    }


    public static void main(String[] args) {
        // String sqlParserForInsert =
        // sqlParserForInsert("Insert into Table2 select  *  from Table1",
        // "two");
        DefaultTenantInfo tenantInfo = new DefaultTenantInfo();
        tenantInfo.setTenantId("11111");
        String sqlParserForSelect = SqlParserUtil.process(new DefaultSqlParser(),
                "select 1 from persp where id in (select p.user_id from per p left join test a on(a.id = b.id) inner join test t on(p.id = t.id) where t=1)");
        System.out.println(sqlParserForSelect);
    }

}
