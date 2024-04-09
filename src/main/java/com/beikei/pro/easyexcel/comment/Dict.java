package com.beikei.pro.easyexcel.comment;

import java.util.*;

/**
 * @author bk
 */
public class Dict extends HashMap<String,Object> {

    private final static Map<String,String> SIGN_MAP;

    static {
        SIGN_MAP = new HashMap<>();
        SIGN_MAP.put("eq","=");
        SIGN_MAP.put("lt","<");
        SIGN_MAP.put("le","<=");
        SIGN_MAP.put("gt",">");
        SIGN_MAP.put("ge",">=");
        SIGN_MAP.put("ne","!=");
    }

    public Dict() {}
    public Dict(String key,Object value) {
        put(key,value);
    }

    public String getStr(String key) {
        return String.valueOf(this.get(key));
    }

    public void put(String filed,String sign,Object value) {
        checkSign(sign);
        this.put(filed + "#" + sign, value);
    }

    public String sign(String key) {
        boolean contains = key.contains("#");
        if (!contains) {
            return "";
        }
        String symbol = key.split("#")[1];
        return SIGN_MAP.get(symbol);
    }

    public String field(String key) {
        return key.split("#")[0];
    }

    public Long getNum(String filed,String sign) {
        return Long.valueOf(String.valueOf(this.get(filed + "#" + sign)));
    }

    public String getStr(String filed,String sign) {
        return String.valueOf(this.get(filed + "#" + sign));
    }

    private void checkSign(String sign) {
        boolean containSign = SIGN_MAP.containsKey(sign);
        if (!containSign) {
            throw new RuntimeException("符号位不符合要求");
        }
    }
}
