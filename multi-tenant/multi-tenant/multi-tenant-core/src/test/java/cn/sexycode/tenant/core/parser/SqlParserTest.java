package cn.sexycode.tenant.core.parser;

import cn.sexycode.tenant.core.DefaultTenantInfo;
import cn.sexycode.tenant.core.TenantInfoHolder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SqlParserTest {
    private DefaultSqlParser defaultSqlParser;

    @Before
    public void init() {
        DefaultTenantInfo tenantInfo = new DefaultTenantInfo();
        tenantInfo.setTenantIdColumn("tenant_id");
        tenantInfo.setTenantId("2");
        defaultSqlParser = new DefaultSqlParser();
        TenantInfoHolder.setTenantInfo(tenantInfo);
    }

    /**
     * 查询语句
     */
    @Test
    public void selectTest() {
        String sql = "SELECT bid, book_name FROM book";
        String newSql = defaultSqlParser.process(sql);
        Assert.assertEquals("SELECT bid, book_name FROM book WHERE book.tid = '2'",
                newSql);
    } 
    
    /**
     * 查询语句
     */
    @Test
    public void selectTest2() {
        String sql = "SELECT tab2.* FROM ( SELECT tab.ID_, tab.PROC_INST_ID_, b.apply_no, b.product_name, b.seller_name, b.buyer_name, b.partner_insurance_name, b.partner_bank_name, b.sales_user_name, b.apply_status, b.relate_type, b.delete_flag, b.group_apply_no, b.product_id, b.house_no, b.version, b.rob_user_name, b.project_tag, b.green_channel, bt.NODE_ID_, bt.OWNER_ID_, bt.NAME_, bt.CREATE_TIME_, bt.task_status taskStatus, inst.status_ instStatus, tab.is_priority, tab.materials_upload_status, tab.tail_release_node, tab.to_use_amount_flag, tab.credit_unfinish, tab.rule_level FROM ( select tab3.*, isr.is_priority, isr.materials_upload_status, isr.tail_release_node, isr.to_use_amount_flag, isr.credit_unfinish, isr.rule_level from ( SELECT task.ID_, task.proc_inst_id_, task.PRIORITY_, task.CREATE_TIME_, b.apply_no, b.green_channel, b.matter_sort FROM BPM_TASK task INNER JOIN BPM_TASK_CANDIDATE ca ON task.id_ = ca.task_id_ LEFT JOIN BPM_PRO_INST inst ON task.proc_inst_id_ = inst.id_ INNER JOIN biz_apply_order b ON task.proc_inst_id_ = b.flow_instance_id WHERE 1 = 1 AND task.assignee_id_ = '0' AND ( ca.executor_ = '' AND ca.type_ = 'user' or (ca.executor_ in ('10000000050760') and ca.type_ ='role') or ( ca.executor_ in ('10000000050612') and ca.type_ ='org') ) AND task.status_ != 'TRANSFORMING' AND ( b.city_no IS NULL OR b.city_no = '' ) UNION ALL SELECT task.ID_, task.proc_inst_id_, task.PRIORITY_, task.CREATE_TIME_, b.apply_no, b.green_channel, b.matter_sort FROM BPM_TASK task INNER JOIN BPM_TASK_CANDIDATE ca ON task.id_ = ca.task_id_ LEFT JOIN BPM_PRO_INST inst ON task.proc_inst_id_ = inst.id_ INNER JOIN biz_apply_order b ON inst.parent_inst_id_ = b.flow_instance_id WHERE 1 = 1 AND task.assignee_id_ = '0' AND ( ca.executor_ = '' AND ca.type_ = 'user' or (ca.executor_ in ('10000000050760') and ca.type_ ='role') or ( ca.executor_ in ('10000000050612') and ca.type_ ='org') ) AND task.status_ != 'TRANSFORMING' AND ( b.city_no IS NULL OR b.city_no = '' ) UNION ALL SELECT task.ID_, task.proc_inst_id_, task.PRIORITY_, task.CREATE_TIME_, b.apply_no, b.green_channel, b.matter_sort FROM BPM_TASK task LEFT JOIN BPM_PRO_INST inst ON task.proc_inst_id_ = inst.id_ INNER JOIN biz_apply_order b ON task.proc_inst_id_ = b.flow_instance_id WHERE 1 = 1 AND task.assignee_id_ = '' AND task.status_ != 'TRANSFORMING' AND ( b.city_no IS NULL OR b.city_no = '' ) UNION ALL SELECT task.ID_, task.proc_inst_id_, task.PRIORITY_, task.CREATE_TIME_, b.apply_no, b.green_channel, b.matter_sort FROM BPM_TASK task LEFT JOIN BPM_PRO_INST inst ON task.proc_inst_id_ = inst.id_ INNER JOIN biz_apply_order b ON inst.parent_inst_id_ = b.flow_instance_id WHERE 1 = 1 AND task.assignee_id_ = '' AND task.status_ != 'TRANSFORMING' AND ( b.city_no IS NULL OR b.city_no = '' ) ) tab3 LEFT JOIN biz_isr_mixed isr ON tab3.apply_no = isr.apply_no ORDER BY tab3.CREATE_TIME_ DESC LIMIT ?, 10 ) tab LEFT JOIN bpm_task bt ON bt.id_ = tab.id_ LEFT JOIN BPM_PRO_INST inst ON tab.proc_inst_id_ = inst.id_ INNER JOIN biz_apply_order b ON ( tab.proc_inst_id_ = b.flow_instance_id OR inst.parent_inst_id_ = b.flow_instance_id ) ) tab2 ";
        String newSql = defaultSqlParser.process(sql);
        System.out.println(newSql);
        /*Assert.assertEquals("SELECT bid, book_name FROM book WHERE book.tid = '2'",
                newSql);*/
    }

    /**
     * 查询语句
     */
    @Test
    public void selectTest1() {
        String sql = "select count ( 0 ) from ( select bdm.id as id , bdm.msg_content as msgcontent , bdm.send_time as sendtime , bdm.`mode` as `mode` , bdm.apply_no as applyno , bdm.delete_flag as deleteflag , bdm.has_read as hasread , bdm.tpl_key as tplkey from biz_ding_msg bdm inner join biz_apply_order bao on bao.apply_no = bdm.apply_no where  bao.branch_id = '' and bdm.`mode` in ( '') and bdm.delete_flag = '' order by sendtime desc , ( case hasread when '0' then 2 when '1' then 1 else 0 end ) desc ) tmp_count" ;
        String newSql = defaultSqlParser.process(sql);
        System.out.println(newSql);
//        Assert.assertEquals("SELECT bid, book_name FROM book WHERE book.tid = '2'",
//                newSql);
    }

    /**
     * INSERT INTO SELECT语句测试
     */
    @Test
    public void insertSelectTest() {
        String sql = "INSERT INTO book(bid, book_name) SELECT '',username FROM `user`LIMIT 1";
        String newSql = defaultSqlParser.process(sql);
        Assert.assertEquals("INSERT INTO book (bid, book_name, tid) SELECT '', username, '2' FROM `user` WHERE `user`.tid = '2' LIMIT 1",
                newSql);
    }

    /**
     * INSERT INTO SELECT语句测试
     */
    @Test
    public void replaceSelectTest() {
        String sql = "replace INTO book(bid, book_name) SELECT '',username FROM `user`LIMIT 1";
        String newSql = defaultSqlParser.process(sql);
        Assert.assertEquals("INSERT INTO book (bid, book_name, tid) SELECT '', username, '2' FROM `user` WHERE `user`.tid = '2' LIMIT 1",
                newSql);
    }

    /**
     * delete INTO SELECT语句测试
     */
    @Test
    public void updateTest() {
        String sql = "update stu t set t.NAME = 'mike' where t.ID = '1'";
        String newSql = defaultSqlParser.process(sql);
        System.out.println(newSql);
        sql = "update stu t ,stu1 t1 set t.NAME = t1.NAME where t1.ID = t.ID and t.id in (select id from stu2 t2 where id=1)";
        newSql = defaultSqlParser.process(sql);
        System.out.println(newSql);
        /*Assert.assertEquals("INSERT INTO book (bid, book_name, tid) SELECT '', username, '2' FROM `user` WHERE `user`.tid = '2' LIMIT 1",
                newSql);*/
    }

    /**
     * delete INTO SELECT语句测试
     */
    @Test
    public void deleteTest() {
        String sql = "delete from book where id =1 and id in (select 1 from test )";
        String newSql = defaultSqlParser.process(sql);
        System.out.println(newSql);
        /*Assert.assertEquals("INSERT INTO book (bid, book_name, tid) SELECT '', username, '2' FROM `user` WHERE `user`.tid = '2' LIMIT 1",
                newSql);*/
    }

    /**
     * delete INTO SELECT语句测试
     */
    @Test
    public void deleteTest1() {
        String sql = "delete from book where id in(select 1 from t1)";
        String newSql = defaultSqlParser.process(sql);
        System.out.println(newSql);
        /*Assert.assertEquals("INSERT INTO book (bid, book_name, tid) SELECT '', username, '2' FROM `user` WHERE `user`.tid = '2' LIMIT 1",
                newSql);*/
    }

    /**
     * 左联接以及别名测试
     */
    @Test
    public void leftJoinTest() {
        String sql = "SELECT a1.id ,a2.name as a2Name,a3.name as a3Name  FROM t1 a1   LEFT JOIN t2 a2 ON a1.id = a2.id LEFT JOIN t2 a3 ON a1.gid = a3.id";
        String newSql = defaultSqlParser.process(sql);
        Assert.assertEquals("SELECT a1.id, a2.name AS a2Name, a3.name AS a3Name FROM t1 a1 LEFT JOIN t2 a2 ON a2.tid = '2' AND a1.id = a2.id LEFT JOIN t2 a3 ON a3.tid = '2' AND a1.gid = a3.id WHERE a1.tid = '2'",
                newSql);
    }


    /**
     * 左联接子查询
     */
    @Test
    public void leftJoinSelectTest() {
        String sql = "SELECT a1.id ,a2.name as a2Name,a3.name as a3Name  FROM t1 a1   LEFT JOIN (select 1 from test )a2 ON a1.id = a2.id LEFT JOIN t2 a3 ON a1.gid = a3.id";
        String newSql = defaultSqlParser.process(sql);
        /*Assert.assertEquals("SELECT a1.id, a2.name AS a2Name, a3.name AS a3Name FROM t1 a1 LEFT JOIN t2 a2 ON a2.tid = '2' AND a1.id = a2.id LEFT JOIN t2 a3 ON a3.tid = '2' AND a1.gid = a3.id WHERE a1.tid = '2'",
                newSql);*/
    }

    /**
     * 多表查询
     */
    @Test
    public void multiSelectTest() {
        String sql = "SELECT a1.id ,a2.name as a2Name,a3.name as a3Name  FROM t1 a1 , t3 a3  where a1.id= a3.id";
        String newSql = defaultSqlParser.process(sql);
        /*Assert.assertEquals("SELECT a1.id, a2.name AS a2Name, a3.name AS a3Name FROM t1 a1 LEFT JOIN t2 a2 ON a2.tid = '2' AND a1.id = a2.id LEFT JOIN t2 a3 ON a3.tid = '2' AND a1.gid = a3.id WHERE a1.tid = '2'",
                newSql);*/
        System.out.println(newSql);
    }

    /**
     * 子查询测试
     */
    @Test
    public void selectSelectTest() {
        String sql = "select a from table1 where  column1 IN (select column1 from table2 where a=1 ) ";
        String newSql = defaultSqlParser.process(sql);
        Assert.assertEquals("SELECT a FROM table1 WHERE table1.tid = '2' AND column1 IN (SELECT column1 FROM table2 WHERE table2.tid = '2' AND a = 1)",
                newSql);
    }

    /**
     * 子查询测试
     */
    @Test
    public void insertSelectTest1() {
        String sql = "INSERT INTO sys_org (id,  org_code,  org_name, status)  SELECT  82419, 'SSGLB8668', '宿舍管理部', 1 FROM DUAL WHERE NOT EXISTS(SELECT 1 FROM sys_org so1 WHERE so1.org_code = 1 )";
        String newSql = defaultSqlParser.process(sql);
     /*   Assert.assertEquals("SELECT a FROM table1 WHERE table1.tid = '2' AND column1 IN (SELECT column1 FROM table2 WHERE table2.tid = '2' AND a = 1)",
                newSql);*/
        System.out.println(newSql);
    }
    /**
     * 插入测试
     */
    @Test
    public void insertTest1() {
        String sql = "INSERT INTO sys_org (id,  org_code,  org_name, status)  values(  82419, 'SSGLB8668', '宿舍管理部', 1 )";
        String newSql = defaultSqlParser.process(sql);
     /*   Assert.assertEquals("SELECT a FROM table1 WHERE table1.tid = '2' AND column1 IN (SELECT column1 FROM table2 WHERE table2.tid = '2' AND a = 1)",
                newSql);*/
        System.out.println(newSql);
    }
}