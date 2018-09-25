package io.choerodon.issue.infra.utils;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/9/7
 * 更新字段的处理工具，把值设置到对应的字段中
 */
@Component
public class VerifyUpdateUtil {
    public List<String> verifyUpdateField(JSONObject jsonObject,Object objectUpdate) {
        List<String> fieldList = new ArrayList<>();
        Class objectClass = objectUpdate.getClass();
        jsonObject.forEach((String k,Object v)->{
            try {
                Field field = objectClass.getDeclaredField(k);
                field.setAccessible(true);
                //把值设置到属性中
                Boolean flag = handleFieldType(field,objectUpdate,v);
                if(flag){
                    fieldList.add(k);
                }
            }catch (Exception e){
                throw new CommonException("error.verifyUpdateField.value");
            }
        });
        fieldList.remove("objectVersionNumber");
        return fieldList;
    }

    private Boolean handleFieldType(Field field, Object objectUpdate, Object v) throws
            IllegalAccessException, ParseException, ClassNotFoundException {
        if (field.getType() == String.class) {
            field.set(objectUpdate, v);
        } else if (field.getType() == Long.class) {
            field.set(objectUpdate, v == null ? null : Long.valueOf(v.toString()));
        } else if (field.getType() == Date.class) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            field.set(objectUpdate, v != null ? sdf.parse(v.toString()) : null);
        } else if (field.getType() == Integer.class) {
            field.set(objectUpdate, v == null ? null : Integer.valueOf(v.toString()));
        } else if (field.getType() == BigDecimal.class) {
            field.set(objectUpdate, v == null ? null : new BigDecimal(v.toString()));
        } else if (field.getType() == List.class) {
            //对象包含子对象是list的值设置
            return false;
        }
        return true;
    }
}
