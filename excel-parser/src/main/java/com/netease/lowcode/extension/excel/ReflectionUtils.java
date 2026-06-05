package com.netease.lowcode.extension.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射工具类
 */
@Slf4j
public class ReflectionUtils {
    private static Map<String, ReflectionHolder> reflectionHolderMap = new ConcurrentHashMap<>();
    private static Method labelAnnotationValueMethod;
    private static Class labelAnnotationClass;
    private static final DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final String ENUM_PREFIX = "FIELD_";

    public static void register(Class clazz) {
        long start = System.currentTimeMillis();
        if (null == clazz) {
            log.warn("注册的class类为空");
            return;
        }
        ReflectionHolder reflectionHolder = reflectionHolderMap.get(clazz.getCanonicalName());
        if (null != reflectionHolder && reflectionHolder.hashCode() == clazz.hashCode()) {
            log.warn("class:{}已经注册，无需重复注册", clazz.getCanonicalName());
            return;
        } else if (null != reflectionHolder && reflectionHolder.hashCode() != clazz.hashCode()) {
            log.info("class hashcode不一样，可能热部署更新过，重新注册class");
            labelAnnotationValueMethod = null;
            labelAnnotationClass = null;
            reflectionHolderMap.remove(clazz.getCanonicalName());
        }

        try {
            Field[] fields = clazz.getDeclaredFields();
            reflectionHolder = new ReflectionHolder();
            reflectionHolder.classHashCode = clazz.hashCode();
            for (Field field : fields) {
                String name = field.getName();
                String label = getPropertyLabel(field);
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(name, clazz);
                reflectionHolder.add(name, label, propertyDescriptor);

                if (isLcapDomainClass(field.getType()) && !field.getType().isEnum()) {
                    // 如果是实体或structure把这些class也注册上
                    register(field.getType());
                    reflectionHolder.addLcapDomainProperty(field.getName());
                }
            }
            reflectionHolderMap.put(clazz.getCanonicalName(), reflectionHolder);
        } catch (IntrospectionException e) {
            throw new ExcelParseException("注册class出错", e);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new ExcelParseException("注册class出错", e);
        } finally {
            log.info("注册class{}花费{}ms", clazz.getCanonicalName(), System.currentTimeMillis() - start);
        }
    }

    public static <T> T newInstance(Class<T> tClass) {
        if (Map.class.isAssignableFrom(tClass)) {
            return (T) new HashMap<>();
        } else if (List.class.isAssignableFrom(tClass)) {
            return (T) new ArrayList<>();
        } else if (BigDecimal.class.isAssignableFrom(tClass)) {
            return (T) new BigDecimal(0);
        } else {
            try {
                return tClass.getDeclaredConstructor().newInstance();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                log.info("无法初始化目标类型:{}", tClass.getCanonicalName());
                throw new ExcelParseException(String.format("无法初始化目标类型:%s", tClass.getCanonicalName()), e);
            }
        }
    }

    public static boolean isCollectionType(Class tClass) {
        return null != tClass && (List.class.isAssignableFrom(tClass) || Map.class.isAssignableFrom(tClass));
    }

    private static PropertyDescriptorChain getPropertyDescriptorChain(Class clazz, String propertyName) {
        if (null == clazz || StringUtils.isBlank(propertyName)) {
            return null;
        }

        ReflectionHolder reflectionHolder = reflectionHolderMap.get(clazz.getCanonicalName());
        if (null == reflectionHolder) {
            return null;
        }

        PropertyDescriptor propertyDescriptor = reflectionHolder.propertyNameDescriptorMap.get(propertyName);
        propertyDescriptor = ObjectUtils.defaultIfNull(propertyDescriptor, reflectionHolder.propertyLabelDescriptorMap.get(propertyName));

        if (null != propertyDescriptor) {
            return new PropertyDescriptorChain(propertyDescriptor);
        }

        if (CollectionUtils.isEmpty(reflectionHolder.lcapDomainClassProperties)) {
            return null;
        }

        // 如果存在平台entity或structure属性则需要再继续递归
        for (String lcapDomainPropName : reflectionHolder.lcapDomainClassProperties) {
            PropertyDescriptor lcapDomainPropDescriptor =
                    reflectionHolder.propertyNameDescriptorMap.get(lcapDomainPropName);
            if (null == lcapDomainPropDescriptor) {
                continue;
            }

            PropertyDescriptorChain propertyDescriptorChain = getPropertyDescriptorChain(
                    lcapDomainPropDescriptor.getPropertyType(), propertyName);

            if (null != propertyDescriptorChain) {
                return new PropertyDescriptorChain(lcapDomainPropDescriptor, propertyDescriptorChain);
            }
        }
        return null;
    }

    /**
     * 类型转换
     * @param value
     * @param tClass
     * @return
     * @param <T>
     */
    public static <T> T convert(String value, Class<T> tClass) {
        if (null == value || null == tClass) {
            return null;
        }

        try {
            if (tClass.isEnum() && isLcapDomainClass(tClass)) {
                //平台枚举类型
                try {
                    return (T) Enum.valueOf((Class) tClass, ENUM_PREFIX + value);
                } catch (IllegalArgumentException e) {
                    Method method = tClass.getMethod("getDesc");

                    for (Object obj : tClass.getEnumConstants()) {
                        if (value.equals(method.invoke(obj))) {
                            return (T) obj;
                        }
                    }

                    return null;
                }
            } else if (tClass.isEnum()) {
                //枚举类型
                return (T) Enum.valueOf((Class) tClass, value);
            } else if (String.class.isAssignableFrom(tClass)) {
                // String类型
                return (T) value;
            } else if (Integer.class.isAssignableFrom(tClass)) {
                // Integer类型
                return (T) Integer.valueOf(value);
            } else if (Long.class.isAssignableFrom(tClass)) {
                // Integer类型
                return (T) Long.valueOf(value);
            } else if (Double.class.isAssignableFrom(tClass)) {
                // Double类型
                return (T) Double.valueOf(value);
            } else if (Float.class.isAssignableFrom(tClass)) {
                // Float类型
                return (T) Float.valueOf(value);
            } else if (Boolean.class.isAssignableFrom(tClass)) {
                // Boolean类型
                Boolean result = "true".equalsIgnoreCase(value) || "1".equals(value);
                return (T) result;
            } else if (BigDecimal.class.isAssignableFrom(tClass)) {
                // BigDecimal类型
                return (T) new BigDecimal(value);
            } else if (LocalDate.class.isAssignableFrom(tClass)) {

                // 将日期转换为标准格式 yyyy/MM/dd 不足位补0
                value = FormatDateStringUtils.format(value);

                // LocalDate类型
                if (value.indexOf("/") >= 0) {
                    return (T) LocalDate.parse(value, dateFormatter2);
                } else {
                    return (T) LocalDate.parse(value, dateFormatter1);
                }
            } else if (LocalTime.class.isAssignableFrom(tClass)) {
                // LocalTime类型
                return (T) LocalTime.parse(value);
            } else if (ZonedDateTime.class.isAssignableFrom(tClass)) {
                // ZonedDateTime类型
                if (value.indexOf("/") >= 0) {
                    return (T) ZonedDateTime.parse(value, dateTimeFormatter2);
                } else {
                    return (T) ZonedDateTime.parse(value, dateTimeFormatter1);
                }
            } else {
                throw new ExcelParseException(String.format("未知类型%s无法转换", tClass.getCanonicalName()));
            }
        } catch (Exception e) {
            throw new ExcelParseException(String.format("类型转换失败，值:%s, 目标类型:%s", value, toErrorMsgType(tClass)), e);
        }
    }

    private static String toErrorMsgType(Class tClass) {
        if (null == tClass) {
            return "未知类型";
        } else if (tClass.isEnum() && isLcapDomainClass(tClass)) {
            //平台枚举类型
            String enumName = tClass.getName().endsWith("Enum") ?
                    tClass.getName().substring(0, tClass.getName().length() - 4) : tClass.getName();
            return enumName + "枚举";
        } else if (tClass.isEnum()) {
            //枚举类型
            return tClass.getName() + "枚举";
        } else if (String.class.isAssignableFrom(tClass)) {
            // String类型
            return "字符串";
        } else if (Integer.class.isAssignableFrom(tClass)) {
            // Integer类型
            return "整数";
        } else if (Long.class.isAssignableFrom(tClass)) {
            // Integer类型
            return "长整数";
        } else if (Double.class.isAssignableFrom(tClass)) {
            // Double类型
            return "小数";
        } else if (Float.class.isAssignableFrom(tClass)) {
            // Float类型
            return "小数";
        } else if (Boolean.class.isAssignableFrom(tClass)) {
            // Boolean类型
            return "布尔值";
        } else if (BigDecimal.class.isAssignableFrom(tClass)) {
            // BigDecimal类型
            return "精确小数";
        } else if (LocalDate.class.isAssignableFrom(tClass)) {
            return "日期";
        } else if (LocalTime.class.isAssignableFrom(tClass)) {
            // LocalTime类型
            return "小时";
        } else if (ZonedDateTime.class.isAssignableFrom(tClass)) {
            // ZonedDateTime类型
            return "日期时间";
        } else {
            return "未知类型";
        }
    }

    private static boolean isLcapDomainClass(Class tClass) {
        // 低代码实体和structure在固定的package下，4.4版本包路径变更，没有domain
        return tClass.getPackage().getName().indexOf("entities") > 0 ||
                tClass.getPackage().getName().indexOf("structure") > 0 ||
                tClass.getPackage().getName().indexOf("enumeration") > 0;
    }

    /**
     *
     * @param obj
     * @param propertyNameOrLabel
     * @param value
     * @return
     */
    public static boolean setValue(Object obj, String propertyNameOrLabel, String value) {
        if (obj instanceof List) {
            ((List) obj).add(value);
            return true;
        } else if (obj instanceof Map) {
            ((Map) obj).put(propertyNameOrLabel, value);
            return true;
        } else {
            Class clazz = obj.getClass();
            PropertyDescriptorChain propertyDescriptorChain = getPropertyDescriptorChain(clazz, propertyNameOrLabel);
            try {
                if (null == propertyDescriptorChain || null == propertyDescriptorChain.target) {
                    return false;
                }

                Object curObj = obj;
                PropertyDescriptorChain curPropertyDescriptorChain = propertyDescriptorChain;
                while (null != curPropertyDescriptorChain.next) {
                    Object curValue = curPropertyDescriptorChain.target.getReadMethod().invoke(curObj);
                    if (null == curValue) {
                        curValue = newInstance(curPropertyDescriptorChain.target.getPropertyType());
                        curPropertyDescriptorChain.target.getWriteMethod().invoke(curObj, curValue);
                    }

                    curObj = curValue;
                    curPropertyDescriptorChain = curPropertyDescriptorChain.next;
                }
                curPropertyDescriptorChain.target.getWriteMethod().invoke(curObj,
                        convert(value, curPropertyDescriptorChain.target.getPropertyType()));
                return true;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ExcelParseException(String.format("类型转换失败，目标类型:%s",
                        propertyDescriptorChain.target.getPropertyType().getCanonicalName()));
            }
        }
    }

    /**
     * @param source
     * @param sourcePropertyNameOrLabel
     * @param dest
     * @param destPropertyNameOrLabel
     */
    public static void copyProperty(Object source, String sourcePropertyNameOrLabel,
                                    Object dest, String destPropertyNameOrLabel) {
        if (source == dest && sourcePropertyNameOrLabel.equals(destPropertyNameOrLabel)) {
            return;
        }

        if (null == source) {
            return;
        }

        PropertyDescriptorChain sourcePropertyDescriptor = getPropertyDescriptorChain(source.getClass(), sourcePropertyNameOrLabel);
        PropertyDescriptorChain destPropertyDescriptor = getPropertyDescriptorChain(dest.getClass(), destPropertyNameOrLabel);

        if (null == sourcePropertyDescriptor || null == destPropertyDescriptor) {
            return;
        }

        try {
            // 获取待复制原始值
            Object curSourceObj = source;
            PropertyDescriptorChain curSourcePropertyDescriptorChain = sourcePropertyDescriptor;
            while (null != curSourcePropertyDescriptorChain && null != curSourceObj) {
                curSourceObj = curSourcePropertyDescriptorChain.target.getReadMethod().invoke(curSourceObj);
                curSourcePropertyDescriptorChain = curSourcePropertyDescriptorChain.next;
            }
            // 初始化待复制目标值的上一级属性，比如a.b.c则先把a.b先初始化出来再复制c
            Object curDesteObj = dest;
            PropertyDescriptorChain curDestPropertyDescriptorChain = destPropertyDescriptor;
            while (null != curDestPropertyDescriptorChain.next && null != curSourceObj) {
                Object value = curDestPropertyDescriptorChain.target.getReadMethod().invoke(curDesteObj);
                if (null == value) {
                    value = ObjectUtils.defaultIfNull(value, newInstance(curDestPropertyDescriptorChain.target.getPropertyType()));
                    curDestPropertyDescriptorChain.target.getWriteMethod().invoke(curDesteObj, value);
                }

                curDesteObj = value;
                curDestPropertyDescriptorChain = curDestPropertyDescriptorChain.next;
            }

            if (null != curSourceObj) {
                // 属性拷贝,null值就不用拷贝了
                // 这里这几个时间类型需要做深拷贝，BigDecimal因为是不可变类型所以可以不用做深拷贝
                if (curSourceObj instanceof LocalTime) {
                    curSourceObj = LocalTime.from((LocalTime) curSourceObj);
                } else if (curSourceObj instanceof LocalDate) {
                    curSourceObj = LocalDate.from((LocalDate) curSourceObj);
                } else if (curSourceObj instanceof ZonedDateTime) {
                    curSourceObj = ZonedDateTime.from((ZonedDateTime) curSourceObj);
                }
                curDestPropertyDescriptorChain.target.getWriteMethod().invoke(curDesteObj, curSourceObj);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ExcelParseException("复制属性失败: " + e.getMessage(), e);
        }
    }

    private static String getPropertyLabel(Field field) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Annotation[] annotations = field.getAnnotations();
        if (null != labelAnnotationClass && null != labelAnnotationValueMethod &&
                field.getAnnotation(labelAnnotationClass) != null) {
            Annotation labelAnnotation = field.getAnnotation(labelAnnotationClass);
            Object labelValue = labelAnnotationValueMethod.invoke(labelAnnotation);
            return null == labelValue ? null : labelValue.toString();
        }

        for (Annotation annotation : annotations) {
            Class clazz = annotation.annotationType();
            if ("Label".equals(clazz.getSimpleName())) {
                if (null == labelAnnotationValueMethod || !clazz.equals(labelAnnotationClass)) {
                    synchronized (ReflectionUtils.class) {
                        labelAnnotationClass = clazz;
                        labelAnnotationValueMethod = clazz.getMethod("value");
                    }
                }

                Object labelValue = labelAnnotationValueMethod.invoke(annotation);
                return null == labelValue ? null : labelValue.toString();
            }
        }

        return null;
    }

    static class ReflectionHolder {
        private Map<String, PropertyDescriptor> propertyNameDescriptorMap = new HashMap<>();
        private Map<String, PropertyDescriptor> propertyLabelDescriptorMap = new HashMap<>();
        // 记录哪些属性是实体或structure，这样可以做递归反射
        private Set<String> lcapDomainClassProperties = new HashSet<>();

        private int classHashCode;

        public void add(String propertyName, String propertyLabel, PropertyDescriptor propertyDescriptor) {
            propertyNameDescriptorMap.put(propertyName, propertyDescriptor);
            if (StringUtils.isNotBlank(propertyLabel)) {
                propertyLabelDescriptorMap.put(propertyLabel, propertyDescriptor);
            }
        }

        public void addLcapDomainProperty(String propertyName) {
            lcapDomainClassProperties.add(propertyName);
        }
    }

    static class PropertyDescriptorChain {
        private PropertyDescriptor target;
        private PropertyDescriptorChain next;

        public PropertyDescriptorChain(PropertyDescriptor target) {
            this.target = target;
        }

        public PropertyDescriptorChain(PropertyDescriptor target, PropertyDescriptorChain next) {
            this.target = target;
            this.next = next;
        }
    }
}
