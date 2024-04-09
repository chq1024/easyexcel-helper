package com.beikei.pro.easyexcel.handler;

import com.beikei.pro.easyexcel.comment.DbHelper;
import com.beikei.pro.easyexcel.comment.Dict;
import com.beikei.pro.easyexcel.util.SpringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * jdbcTemplate 实现DbHelper
 * @author bk
 */
public class JdbcTemplateDbHelper extends DbHelper {

    private final String DATUM_NON_PREFIX = "non_";

    @Override
    public void insert2db(List<Dict> data) {
        Map<String, String> tbSqlMap = new HashMap<>();
        JdbcTemplate template = SpringUtils.getBean(JdbcTemplate.class);
        for (Dict datum : data) {
            String tbName = (String) datum.getOrDefault(DATUM_NON_PREFIX + "tb", "");
            if (!StringUtils.hasText(tbName)) {
                throw new RuntimeException("数据中必须包含tb字段");
            }
            String sql = "";
            List<String> params = new ArrayList<>();
            if (tbSqlMap.containsKey(tbName)) {
                sql = tbSqlMap.get(tbName);
                datum.forEach((k, v) -> {
                    if (!k.startsWith(DATUM_NON_PREFIX)) {
                        params.add(String.valueOf(v));
                    }
                });
            } else {
                StringBuilder keyBuilder = new StringBuilder("INSERT INTO " + tbName + "(");
                StringBuilder valueBuilder = new StringBuilder("VALUES(");
                for (Map.Entry<String, Object> entry : datum.entrySet()) {
                    String key = entry.getKey();
                    if (key.startsWith(DATUM_NON_PREFIX)) {
                        continue;
                    }
                    params.add(String.valueOf(entry.getValue()));
                    keyBuilder.append(key).append(",");
                    valueBuilder.append("?").append(",");
                }
                // 删除多余的','
                keyBuilder.deleteCharAt(keyBuilder.length() - 1);
                valueBuilder.deleteCharAt(keyBuilder.length() - 1);
                sql = keyBuilder.append(")").append(" ").append(valueBuilder).toString();
                tbSqlMap.put(tbName, sql);
            }
            template.update(sql, params.toArray());
        }
    }

    @Override
    public long count(Dict queryWrapper) {
        JdbcTemplate template = SpringUtils.getBean(JdbcTemplate.class);
        boolean containsTb = queryWrapper.containsKey(DATUM_NON_PREFIX + "tb");
        if (!containsTb) {
            throw new RuntimeException("数据中必须包含tb字段");
        }
        String tb = queryWrapper.getStr(DATUM_NON_PREFIX + "tb");
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM " + tb + " WHERE 1=1");
        List<String> params = new ArrayList<>();
        wrapper2builder(sqlBuilder, params, queryWrapper);
        String sql = sqlBuilder.toString();
        return Optional.ofNullable(template.queryForObject(sql, Long.class, params.toArray())).orElse(0L);
    }

    @Override
    public List<Dict> batchQuery(long page, int size, Dict queryWrapper, Dict orderItems) {
        JdbcTemplate template = SpringUtils.getBean(JdbcTemplate.class);
        boolean containsTb = queryWrapper.containsKey(DATUM_NON_PREFIX + "tb");
        if (!containsTb) {
            throw new RuntimeException("数据中必须包含tb字段");
        }
        String tb = queryWrapper.getStr(DATUM_NON_PREFIX + "tb");
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM " + tb + " WHERE 1=1");
        List<String> params = new ArrayList<>();
        wrapper2builder(sqlBuilder, params, queryWrapper);
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

    private void wrapper2builder(StringBuilder builder, List<String> params, Dict queryWrapper) {
        for (Map.Entry<String, Object> entry : queryWrapper.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.startsWith(DATUM_NON_PREFIX)) {
                continue;
            }
            Dict dict = new Dict(key, value);
            String symbol = dict.sign(key);
            String field = dict.field(key);
            builder.append(" AND ").append(field).append(symbol).append("?");
            params.add(dict.getStr(key));
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


}
