package cn.sexycode.tenant.parser;

public interface SqlParser {

    /**
     * sql语句处理入口
     *
     * @param sql
     * @return
     */
    String process(String sql);
}
