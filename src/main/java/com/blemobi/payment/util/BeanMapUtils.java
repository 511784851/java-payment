/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.util
 *
 *    Filename:    BeanMapUtils.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月1日 下午9:05:55
 *
 *    Revision:
 *
 *    2017年3月1日 下午9:05:55
 *
 *****************************************************************/
package com.blemobi.payment.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @ClassName BeanMapUtils
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月1日 下午9:05:55
 * @version 1.0.0
 */
public final class BeanMapUtils {

    public static Map<String, String> bean2Map(Object obj) {
        Map<String, String> ret = new HashMap<>();
        Field[] fields = getAllField(obj.getClass());
        for (Field f : fields) {
            f.setAccessible(true);
            String name = f.getName();
            try {
                Object value = f.get(obj);
                if (value == null) {
                    continue;
                }
                if (StringUtils.isEmpty(value.toString())) {
                    continue;
                }
                ret.put(name, value.toString());
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return ret;
    }

    private static Field[] getAllField(Class<?> clazz) {
        ArrayList<Field> fieldList = new ArrayList<Field>();
        Field[] dFields = clazz.getDeclaredFields();
        if (null != dFields && dFields.length > 0) {
            fieldList.addAll(Arrays.asList(dFields));
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            Field[] superFields = getAllField(superClass);
            if (null != superFields && superFields.length > 0) {
                for (Field field : superFields) {
                    if (!isContain(fieldList, field)) {
                        fieldList.add(field);
                    }
                }
            }
        }
        Field[] result = new Field[fieldList.size()];
        fieldList.toArray(result);
        return result;
    }

    private static boolean isContain(ArrayList<Field> fieldList, Field field) {
        for (Field temp : fieldList) {
            if (temp.getName().equals(field.getName())) {
                return true;
            }
        }
        return false;
    }
}
