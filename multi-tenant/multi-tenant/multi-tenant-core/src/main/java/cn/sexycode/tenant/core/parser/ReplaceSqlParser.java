package cn.sexycode.tenant.core.parser;

import cn.sexycode.tenant.TenantInfo;
import cn.sexycode.tenant.parser.SqlParser;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qinzaizhen
 */
public class ReplaceSqlParser implements SqlParser {

    /**
     * from sst ss,ttt t where
     */
    private final Pattern wherePattern = Pattern.compile("from\\s+((\\w+\\s*(\\w+)?),?)+\\s+where");

    /**
     * ss s join s s on
     */
    private final Pattern joinPattern = Pattern.compile("\\w+\\s*(\\w+)? join \\w+\\s*(\\w+)? on");

    private TenantInfo tenantInfo;


    /**
     * 在where 条件中加上租户字段条件
     * <p>
     * where tenancy_id = ‘’ 前面有别名的加上别名
     * on 条件中加上租户字段
     *
     * @param sql
     * @return
     */
    @Override
    public String process(String sql) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql must not null");
        }
        StringBuilder sqlSB = new StringBuilder(sql.replaceAll("\\n", "").replaceAll("\\t", ""));
        StringBuilder tenantSql = new StringBuilder(tenantInfo.getTenantIdColumn());
        tenantSql.append("=").append(tenantInfo.getTenantId()).append(" and ");
        Matcher whereMatcher = wherePattern.matcher(sql);
        while (whereMatcher.find()) {
            handleMatcher(sqlSB, tenantSql, whereMatcher);
        }

        //处理join的情况
        Matcher joinMatcher = joinPattern.matcher(sqlSB);
        while (joinMatcher.find()) {
            handleMatcher(sqlSB, tenantSql, joinMatcher);
        }
        return sqlSB.toString();
    }

    private void handleMatcher(StringBuilder sqlSB, StringBuilder tenantSql, Matcher whereMatcher) {
        String s = whereMatcher.group();
        //得到 from sst ss,ttt t where
        String[] split = s.split(" ");
        //去掉头尾
        split[0] = split[split.length - 1] = "";
        String tableStr = StringUtils.join(split, " ");
        // 得到 sst ss,ttt t
        String[] tables = tableStr.split(",");

        for (String table : tables) {
            String[] a = table.trim().split(" ");

            StringBuilder sb = new StringBuilder(tenantSql);
            if (a.length == 2) {
                //有别名
                String alias = split[1];
                sb.insert(0, alias + ".");
            }

            int end = whereMatcher.end();
            if (sqlSB.charAt(end  + 1) != ' '){
                sqlSB.insert(end, " ");
            }
            sqlSB.insert(end + 1, sb);
        }
    }
}
