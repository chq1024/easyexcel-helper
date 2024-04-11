package com.beikei.pro.easyexcel.handler;

import com.beikei.pro.easyexcel.comment.Const;
import com.beikei.pro.easyexcel.comment.DbHelper;
import com.beikei.pro.easyexcel.comment.Dict;
import com.beikei.pro.easyexcel.handler.properties.HelperProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * jdbcTemplate 实现DbHelper
 * @author bk
 */
@Configuration
@Getter
public class JdbcTemplateDbHelper extends DbHelper {


    @Autowired
    private HelperProperties helperProperties;
    @Autowired
    private JdbcTemplate template;

    @PostConstruct
    public void load() {
        Dict schema = loadSchema(helperProperties.getDbName());
        String datumNon = helperProperties.getIgnorePrefix();
        super.setSchema(schema);
        super.setDatumNon(datumNon);
    }

    @Override
    public void insert2db(List<Dict> data) {
        Map<String, String> tbSqlMap = new HashMap<>();
        String datumNon = getDatumNon();
        for (Dict datum : data) {
            String tbName = (String) datum.getOrDefault(datumNon + Const.TB, "");
            if (!StringUtils.hasText(tbName)) {
                throw new RuntimeException("数据中必须包含tb字段");
            }
            String sql = "";
            List<Object> params = new ArrayList<>();
            if (tbSqlMap.containsKey(tbName)) {
                sql = tbSqlMap.get(tbName);
                datum.forEach((k, v) -> {
                    if (!k.startsWith(datumNon)) {
                        params.add(v);
                    }
                });
            } else {
                StringBuilder keyBuilder = new StringBuilder("INSERT INTO " + tbName + "(");
                StringBuilder valueBuilder = new StringBuilder("VALUES(");
                Dict schemaDict = getSchema().getDict(tbName);
                for (Map.Entry<String, Object> entry : datum.entrySet()) {
                    String key = entry.getKey();
                    if (key.startsWith(datumNon)) {
                        continue;
                    }
                    if (helperProperties.getEnableCheck() && !schemaDict.containsKey(key)) {
                        throw new RuntimeException("表结构异常，需执行刷新接口同步表结构");
                    }
                    if (schemaDict.containsKey(key)) {
                        params.add(entry.getValue());
                        keyBuilder.append(key).append(",");
                        valueBuilder.append("?").append(",");
                    }
                }
                // 删除多余的','
                keyBuilder.deleteCharAt(keyBuilder.length() - 1);
                valueBuilder.deleteCharAt(valueBuilder.length() - 1);
                sql = keyBuilder.append(")").append(" ").append(valueBuilder).append(")").toString();
                tbSqlMap.put(tbName, sql);
            }
            template.update(sql, params.toArray());
        }
    }

    @Override
    public long count(Dict queryWrapper) {
        String datumNon = getDatumNon();
        boolean containsTb = queryWrapper.containsKey(datumNon + "tb");
        if (!containsTb) {
            throw new RuntimeException("数据中必须包含tb字段");
        }
        String tb = queryWrapper.getStr(datumNon + "tb");
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM " + tb + " WHERE 1=1");
        List<Object> params = new ArrayList<>();
        wrapper2builder(tb,sqlBuilder, params, queryWrapper);
        String sql = sqlBuilder.toString();
        return Optional.ofNullable(template.queryForObject(sql, Long.class, params.toArray())).orElse(0L);
    }

    @Override
    public List<Dict> batchQuery(long page, int size, Dict queryWrapper, Dict orderItems) {
        String datumNon = getDatumNon();
        boolean containsTb = queryWrapper.containsKey(datumNon + "tb");
        if (!containsTb) {
            throw new RuntimeException("数据中必须包含tb字段");
        }
        String tb = queryWrapper.getStr(datumNon + "tb");
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM " + tb + " WHERE 1=1");
        List<Object> params = new ArrayList<>();
        wrapper2builder(tb,sqlBuilder, params, queryWrapper);
        // 需要检测k,v安全性
        if (orderItems != null && !orderItems.isEmpty()) {
            sqlBuilder.append(" ORDER BY ");
            orderItems.forEach((k, v) -> {
                sqlBuilder.append(k).append(" ").append(v);
            });
        }
        sqlBuilder.append(" LIMIT ").append(page * size).append(",").append(size);
        String sql = sqlBuilder.toString();
        List<Map<String, Object>> maps = template.queryForList(sql, params);
        return maps2dicts(maps);
    }

    private void wrapper2builder(String tb,StringBuilder builder, List<Object> params, Dict queryWrapper) {
        String datumNon = getDatumNon();
        Dict schemaDict = getSchema().getDict(tb);
        for (Map.Entry<String, Object> entry : queryWrapper.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.startsWith(datumNon)) {
                continue;
            }
            Dict dict = new Dict(key, value);
            String column = dict.column(key);
            String symbol = dict.sign(key);
            if (helperProperties.getEnableCheck() && !schemaDict.containsKey(dict.column(key))) {
                throw new RuntimeException("表结构异常，需执行刷新接口同步表结构");
            }
            if (schemaDict.containsKey(column)) {
                builder.append(" AND ").append(column).append(symbol).append("?");
                params.add(dict.getStr(key));
            }
        }
    }

    private List<Dict> maps2dicts(List<Map<String, Object>> maps) {
        ObjectMapper mapper = new ObjectMapper();
        List<Dict> dicts = new ArrayList<>();
        try {
            for (Map<String, Object> map : maps) {
                String json = mapper.writeValueAsString(map);
                Dict dict = mapper.readValue(json, Dict.class);
                dicts.add(dict);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return dicts;
    }

    public Dict loadSchema(String dbName) {
        String schemaSql = "SELECT TABLE_NAME,COLUMN_NAME,ORDINAL_POSITION FROM information_schema.columns WHERE TABLE_SCHEMA = ?";
        Dict dict = new Dict();
        Dict tempDict = dict;
        template.query(schemaSql, (rs, rowNum) -> {
            String tableName = rs.getString("TABLE_NAME");
            String columnName = rs.getString("COLUMN_NAME");
            String dataType = rs.getString("ORDINAL_POSITION");
            tempDict.putDict(tableName, columnName, dataType);
            return tempDict;
        }, dbName);
        return dict;
    }
}
