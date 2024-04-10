package com.beikei.pro.easyexcel.comment;

import java.util.*;

/**
 * 支持三元map
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

    public Dict getDict(String key) {
        boolean contained = this.containsKey(key);
        if (contained) {
            Object val = this.get(key);
            if (val instanceof Dict) {
                return (Dict) val;
            } else {
                throw new RuntimeException("类型错误");
            }
        }
        return new Dict();
    }

    public Long getNum(String key) {
        return Long.valueOf(getStr(key));
    }

    public void put(String preKey,String subKey,Object value) {
        checkSign(subKey);
        this.put(preKey + "#" + subKey, value);
    }

    public void putDict(String key,String cKey,Object cValue) {
        Dict dict = this.getDict(key);
        dict.put(cKey,cValue);
        this.put(key,dict);
    }

    /**
     * 针对符号位特殊定制
     * @param key
     * @return
     */
    public String sign(String key) {
        boolean contains = key.contains("#");
        if (!contains) {
            return "";
        }
        String symbol = key.split("#")[1];
        return SIGN_MAP.get(symbol);
    }
    /**
     * 针对符号位特殊定制
     * @param key
     * @return
     */
    public String column(String key) {
        return key.split("#")[0];
    }

    private void checkSign(String sign) {
        boolean containSign = SIGN_MAP.containsKey(sign);
        if (!containSign) {
            throw new RuntimeException("符号位不符合要求");
        }
    }
}
