package com.iteams.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * 数据脱敏工具类
 * <p>
 * 提供敏感数据脱敏功能
 * </p>
 */
@Component
public class DataMaskingUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // 敏感字段名称正则表达式
    private static final Pattern SENSITIVE_FIELD_PATTERN = Pattern.compile(
            "(?i)(password|pwd|secret|key|token|creditCard|idCard|idNumber|phone|mobile|email|bankAccount)"
    );
    
    /**
     * 对敏感数据进行脱敏处理
     *
     * @param jsonStr JSON字符串
     * @return 脱敏后的JSON字符串
     */
    public static String maskSensitiveData(String jsonStr) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            maskSensitiveFields(rootNode);
            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            // 如果解析失败，返回原始字符串
            return jsonStr;
        }
    }
    
    /**
     * 递归遍历JSON节点，对敏感字段进行脱敏
     *
     * @param jsonNode JSON节点
     */
    private static void maskSensitiveFields(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<String> fieldNames = objectNode.fieldNames();
            
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode fieldValue = objectNode.get(fieldName);
                
                if (SENSITIVE_FIELD_PATTERN.matcher(fieldName).matches() && fieldValue.isTextual()) {
                    // 根据字段类型选择不同的脱敏策略
                    String value = fieldValue.asText();
                    String maskedValue;
                    
                    if (fieldName.toLowerCase().contains("password") || 
                        fieldName.toLowerCase().contains("pwd") || 
                        fieldName.toLowerCase().contains("secret") || 
                        fieldName.toLowerCase().contains("key") || 
                        fieldName.toLowerCase().contains("token")) {
                        maskedValue = "******"; // 密码类完全脱敏
                    } else if (fieldName.toLowerCase().contains("phone") || 
                               fieldName.toLowerCase().contains("mobile")) {
                        maskedValue = maskPhone(value);
                    } else if (fieldName.toLowerCase().contains("email")) {
                        maskedValue = maskEmail(value);
                    } else if (fieldName.toLowerCase().contains("idcard") || 
                               fieldName.toLowerCase().contains("idnumber")) {
                        maskedValue = maskIdCard(value);
                    } else if (fieldName.toLowerCase().contains("creditcard") || 
                               fieldName.toLowerCase().contains("bankaccount")) {
                        maskedValue = maskBankCard(value);
                    } else {
                        // 默认脱敏策略
                        maskedValue = maskDefault(value);
                    }
                    
                    objectNode.put(fieldName, maskedValue);
                } else if (fieldValue.isObject() || fieldValue.isArray()) {
                    // 递归处理嵌套的对象或数组
                    maskSensitiveFields(fieldValue);
                }
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (JsonNode node : arrayNode) {
                maskSensitiveFields(node);
            }
        }
    }
    
    /**
     * 手机号脱敏
     * 保留前3位和后4位，中间用****代替
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
    
    /**
     * 邮箱脱敏
     * 邮箱前缀仅显示前3个字符，后面用***代替，@及后面的地址显示完整
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf('@');
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);
        
        if (prefix.length() <= 3) {
            return prefix + "***" + suffix;
        } else {
            return prefix.substring(0, 3) + "***" + suffix;
        }
    }
    
    /**
     * 身份证号脱敏
     * 保留前6位和后4位，中间用8个*代替
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 10) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
    }
    
    /**
     * 银行卡号脱敏
     * 保留前4位和后4位，中间用*代替
     */
    public static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 8) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + "****" + bankCard.substring(bankCard.length() - 4);
    }
    
    /**
     * 默认脱敏规则
     * 如果长度大于等于8，则保留前3位和后2位，中间用***代替
     * 如果长度小于8，则显示前1位，其他用*代替
     */
    public static String maskDefault(String value) {
        if (value == null) {
            return null;
        }
        
        int length = value.length();
        if (length >= 8) {
            return value.substring(0, 3) + "***" + value.substring(length - 2);
        } else if (length > 1) {
            return value.substring(0, 1) + "*".repeat(length - 1);
        } else {
            return value;
        }
    }
} 