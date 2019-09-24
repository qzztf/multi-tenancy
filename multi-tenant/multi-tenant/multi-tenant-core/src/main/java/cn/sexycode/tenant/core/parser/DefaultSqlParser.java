package cn.sexycode.tenant.core.parser;

import cn.sexycode.tenant.TenantInfo;
import cn.sexycode.tenant.core.TenantInfoHolder;
import cn.sexycode.tenant.exception.MultiTenantException;
import cn.sexycode.tenant.parser.SqlParser;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinzaizhen
 */
public class DefaultSqlParser implements SqlParser {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSqlParser.class);
    private static final String TABLE_DUAL = "dual";

    TenantInfo getTenantInfo() {
        return TenantInfoHolder.getTenantInfo();
    }

    @Override
    public String process(String sql) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql is not null");
        }
        logger.debug("old sql:{}", sql);
        Statement stmt = null;
        try {
            stmt = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
//            logger.debug("解析", e);
            logger.error("解析sql[{}]失败, 原因:", sql, e);
            //如果解析失败不进行任何处理防止业务中断
            return sql;
        }

        processStatement(stmt);

        verifyStmt(stmt);
        logger.debug("new sql:{}", stmt);
        return stmt.toString();
    }

    private void verifyStmt(Statement stmt) {
        //todo 简单的校验,防止漏拼接租户ID
        if (!stmt.toString().contains(getTenantInfo().getTenantIdColumn())) {
            throw new MultiTenantException("SQL拼接租户ID失败:" + stmt);
        }
    }

    private void processStatement(Statement statement) {
        statement.accept(new StatementVisitorAdapter() {
            @Override
            public void visit(Delete delete) {
                super.visit(delete);
                delete.setWhere(buildExpression(delete.getWhere(), delete.getTable()));
            }


            @Override
            public void visit(Update update) {
                super.visit(update);
                for (Table table : update.getTables()) {
                    update.setWhere(buildExpression(update.getWhere(), table));
                }
            }

            @Override
            public void visit(Select select) {
                super.visit(select);
                processSelectBody(select.getSelectBody());
            }

            @Override
            public void visit(Replace replace) {
                super.visit(replace);
                processReplace(replace);
            }

            @Override
            public void visit(Insert insert) {
                super.visit(insert);
                processInsert(insert);
            }
        });

    }

    private void processDelete(Delete delete) {
        processStatement(delete);
    }

    public void processInsert(Insert insert) {
        {
            TenantInfo tenantInfo = getTenantInfo();
            Column tenantColumn = new Column(tenantInfo.getTenantIdColumn());
            Column existsTenantColumn = null;
            for (Column column : insert.getColumns()) {
                if (tenantInfo.getTenantIdColumn().equalsIgnoreCase(column.getColumnName())) {
                    existsTenantColumn = column;
                    break;
                }
            }
            boolean notExistTenantColumn = existsTenantColumn == null;
            if (notExistTenantColumn) {
                insert.getColumns().add(tenantColumn);
            }
            if (insert.getSelect() != null) {
                processPlainSelect((PlainSelect) insert.getSelect().getSelectBody(), notExistTenantColumn);
            } else if (insert.getItemsList() != null) {
                ItemsList itemsList = insert.getItemsList();
                if (itemsList instanceof MultiExpressionList) {
                    ((MultiExpressionList) itemsList).getExprList().forEach(el -> {
                        if (notExistTenantColumn) {
                            el.getExpressions().add(getTenantColumnValue(tenantInfo.getTenantId()));
                        }else {
                            logger.debug("自己负责维护 tenantId");
                        }
                    });
                } else {
                    if (notExistTenantColumn) {
                        ((ExpressionList) insert.getItemsList()).getExpressions().add(getTenantColumnValue(tenantInfo.getTenantId()));
                    }
                }
            } else {
                //
                throw new RuntimeException("无法处理的 sql: " + insert.toString());
            }
        }
    }

    public void processReplace(Replace insert) {
        {
            insert.getColumns().add(new Column(getTenantInfo().getTenantIdColumn()));
            if (insert.getItemsList() != null) {
                if (insert.getItemsList() instanceof SubSelect) {
                    processPlainSelect((PlainSelect) ((SubSelect) insert.getItemsList()).getSelectBody(), true);
                }
            } else {
                //
                throw new RuntimeException("无法处理的 sql: " + insert.toString());
            }
        }
    }

    /**
     * update语句处理
     * TODO 因为线上系统不允许更改数据租户,所以并未实现它
     *
     * @param update
     */
    public void processUpdate(Update update) {
        processStatement(update);
    }

    /**
     * 处理联接语句
     *
     * @param join
     */
    public void processJoin(Join join) {
        if (join.getRightItem() instanceof Table) {
            Table fromTable = (Table) join.getRightItem();
            join.setOnExpression(buildExpression(join.getOnExpression(), fromTable));
        }
    }

    /**
     * 处理条件
     * TODO 未解决sql注入问题(考虑替换StringValue为LongValue),因为线上数据库租户字段为int暂时不存在注入问题
     *
     * @param expression
     * @param table
     * @return
     */
    public Expression buildExpression(Expression expression, Table table) {
        Expression tenantExpression = null;
        String[] tenantIds = getTenantInfo().getTenantId().split(",");
        //当传入table时,字段前加上别名或者table名
        //别名优先使用
        StringBuilder tenantIdColumnName = new StringBuilder();
        if (table != null) {
            tenantIdColumnName.append(table.getAlias() != null ? table.getAlias().getName() : table.getName());
            tenantIdColumnName.append(".");
        }
        tenantIdColumnName.append(getTenantInfo().getTenantIdColumn());
        //生成字段名
        Column tenantColumn = new Column(tenantIdColumnName.toString());

        if (tenantIds.length == 1) {
            EqualsTo equalsTo = new EqualsTo();
            tenantExpression = equalsTo;
            equalsTo.setLeftExpression(tenantColumn);
            equalsTo.setRightExpression(getTenantColumnValue(tenantIds[0]));
        } else {
            //多租户身份
            InExpression inExpression = new InExpression();
            tenantExpression = inExpression;
            inExpression.setLeftExpression(tenantColumn);
            List<Expression> valueList = new ArrayList<>();
            for (String tid : tenantIds) {
                valueList.add(getTenantColumnValue(tid));
            }
            inExpression.setRightItemsList(new ExpressionList(valueList));
        }

        //加入判断防止条件为空时生成 "and null" 导致查询结果为空
        if (expression == null) {
            return tenantExpression;
        } else {
            expression.accept(new ExpressionVisitorAdapter() {
                @Override
                public void visit(SubSelect subSelect) {
                    super.visit(subSelect);
                    processFromItem(subSelect);
                }
            });
            /*if (expression instanceof BinaryExpression) {
                BinaryExpression binaryExpression = (BinaryExpression) expression;
                if (binaryExpression.getLeftExpression() instanceof FromItem) {
                    processFromItem((FromItem) binaryExpression.getLeftExpression());
                }
                if (binaryExpression.getRightExpression() instanceof FromItem) {
                    processFromItem((FromItem) binaryExpression.getRightExpression());
                }
            } else if (expression instanceof InExpression) {
                ((InExpression) expression).getLeftExpression().accept(new ExpressionVisitorAdapter() {
                    @Override
                    public void visit(SubSelect subSelect) {
                        super.visit(subSelect);
                        processFromItem(subSelect);
                    }
                });
                ((InExpression) expression).getRightItemsList().accept(new ItemsListVisitorAdapter() {
                    @Override
                    public void visit(SubSelect subSelect) {
                        super.visit(subSelect);
                        processFromItem(subSelect);
                    }
                });
            }else if (expression instanceof AndExpression){
//                ((AndExpression) expression).
            }*/
              if(table != null && TABLE_DUAL.equalsIgnoreCase(table.getName())){
                return  expression;
            }
            return new AndExpression(tenantExpression, expression);
        }

    }

    private Expression getTenantColumnValue(String tid) {
        TenantInfo tenantInfo = getTenantInfo();
        if (tenantInfo.getTenantIdColumnType().equals(Long.class)){
            return new LongValue(tid);
        }
        return new StringValue("'" + tid + "'");
    }

    /**
     * 处理SelectBody
     */
    public void processSelectBody(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (withItem.getSelectBody() != null) {
                processSelectBody(withItem.getSelectBody());
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
                List<SelectBody> plainSelects = operationList.getSelects();
                for (SelectBody plainSelect : plainSelects) {
                    processSelectBody(plainSelect);
                }
            }
        }
    }

    /**
     * 处理PlainSelect
     */

    public void processPlainSelect(PlainSelect plainSelect) {
        processPlainSelect(plainSelect, false);
    }

    /**
     * 处理PlainSelect
     *
     * @param plainSelect
     * @param addColumn   是否添加租户列,insert into select语句中需要
     */

    public void processPlainSelect(final PlainSelect plainSelect, final boolean addColumn) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;

            plainSelect.setWhere(buildExpression(plainSelect.getWhere(), fromTable));
            if (addColumn) {
                plainSelect.getSelectItems().add(new SelectExpressionItem(getTenantColumnValue(getTenantInfo().getTenantId())));
            }
        } else {
            processFromItem(fromItem);
        }
        List<Join> joins = plainSelect.getJoins();
        if (joins != null && joins.size() > 0) {
            for (Join join : joins) {
                processJoin(join);
                processFromItem(join.getRightItem());
            }
        }
    }

    /**
     * 处理子查询等
     *
     * @param fromItem
     */
    public void processFromItem(FromItem fromItem) {

        fromItem.accept(new FromItemVisitorAdapter() {
            @Override
            public void visit(SubJoin subjoin) {
                super.visit(subjoin);
                if (subjoin.getJoinList() != null) {
                    for (Join join : subjoin.getJoinList()) {
                        processJoin(join);
                    }
                }
                if (subjoin.getLeft() != null) {
                    processFromItem(subjoin.getLeft());
                }
            }

            @Override
            public void visit(SubSelect subSelect) {
                super.visit(subSelect);
                if (subSelect.getSelectBody() != null) {
                    processSelectBody(subSelect.getSelectBody());
                }
            }

            @Override
            public void visit(LateralSubSelect lateralSubSelect) {
                super.visit(lateralSubSelect);
                if (lateralSubSelect.getSubSelect() != null) {
                    SubSelect subSelect = lateralSubSelect.getSubSelect();
                    if (subSelect.getSelectBody() != null) {
                        subSelect.accept(this);
//                        processSelectBody(subSelect.getSelectBody());
                    }
                }
            }
        });


        /*if (fromItem instanceof SubJoin) {
            SubJoin subJoin = (SubJoin) fromItem;
            if (subJoin.getJoinList() != null) {
                for (Join join : subJoin.getJoinList()) {
                    processJoin(join);
                }
            }
            if (subJoin.getLeft() != null) {
                processFromItem(subJoin.getLeft());
            }
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            if (subSelect.getSelectBody() != null) {
                processSelectBody(subSelect.getSelectBody());
            }
        } else if (fromItem instanceof ValuesList) {

        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
                SubSelect subSelect = lateralSubSelect.getSubSelect();
                if (subSelect.getSelectBody() != null) {
                    processSelectBody(subSelect.getSelectBody());
                }
            }
        }*/
    }

    public DefaultSqlParser() {
    }

}
