package com.tangyu.component.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;

/**
 * This class depends on 'Fastjson'.
 * Support a simple way to translate between Object and JsonObject.
 * Like this:
 * <pre>
 *     // java bean is follow the rule of FastJson.
 *     class A implements IJsonBeanV2 {
 *         private int paramA;
 *         private String paramB;
 *
 *         // sets/gets ...
 *
 *         public static final EncodeObjectV2<A> CODE = new EncodeObjectV2<A>() { };
 *         public static final EncodeString<A> CODE_STRING = new EncodeString<A>() { };
 *     }
 *
 *     // invoking.
 *     // encode object to jsonObject
 *     A.CODE.encode(...);
 *     // decode jsonObject to object
 *     A.CODE.decode(...);
 *     // encode object to String
 *     A.CODE_STRING.encode(...);
 *     // decode String to object
 *     A.CODE_STRING.decode(...);
 *
 * </pre>
 *
 * @see com.tangyu.component.demo.util.JavaBeanV2
 *
 * @author binliu on 8/24/14.
 */
public interface IJsonBeanV2 {

    public static final EncodeObject CODE = null;

    public static final EncodeArray CODE_ARRAY = null;

    public static final EncodeString CODE_STRING = null;

    public static final EncodeStrings CODE_STRING_ARRAY = new EncodeStrings();

    public static interface IEncode<T, E> {
        public E encode(T instance);
        public T decode(E jsonObject);
    }

    public static interface EncodeArray<T> extends IEncode<T, JSONArray> {
        public JSONArray encode(T instance);
        public T decode(JSONArray jsonArray);
    }

    public static interface EncodeWithString<T> extends IEncode<T, String> { }

    /**
     * List<String> to JsonArray
     */
    public static final class EncodeStrings implements IJsonBeanV2.EncodeArray<List<String>> {

        @Override
        public JSONArray encode(List<String> instance) {
            JSONArray jsonArray = new JSONArray();
            if (null != instance) {
                for (String item : instance) {
                    if (null != item) jsonArray.put(item);
                }
            }
            return jsonArray;
        }

        @Override
        public List<String> decode(JSONArray jsonArray) {
            LinkedList<String> result = new LinkedList<String>();
            if (null != jsonArray) {
                for (int i = 0; i < jsonArray.length(); ++i) {
                    String item = jsonArray.optString(i);
                    if (null != item) result.add(item);
                }
            }
            return result;
        }
    }

    /**
     * String <--> JsonObject
     */
    public static abstract class EncodeString<T> implements EncodeWithString<T> {

        Class<T> entityClass;

        protected Class<T> getGenericClass() {
            return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }

        @Override
        public T decode(String instance) {
            if (entityClass == null) {
                entityClass = getGenericClass();
            }
            if (TextUtils.isEmpty(instance) || 0 == TextUtils.getTrimmedLength(instance)) {
                return null;
            }

            return JSON.parseObject(instance, entityClass);
        }

        @Override
        public String encode(T object) {
            if (object == null) return null;
            return JSON.toJSONString(object);
        }
    }

    /**
     * Object <--> JsonObject
     * Logic is same as {@link IJsonBeanV2.EncodeString}
     * @param <T>
     */
    public static abstract class EncodeObject<T> implements IEncode<T, JSONObject> {

        Class<T> entityClass;

        protected Class<T> getGenericClassIfNeed() {
            return null == entityClass ? entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0] : entityClass;
        }

        @Override
        public JSONObject encode(T instance) {
            Class<T> genericClassIfNeed = getGenericClassIfNeed();
            try {
                Field field = genericClassIfNeed.getDeclaredField("CODE_STRING");
                Method encodeMethod = field.getType().getDeclaredMethod("encode", Object.class);
                String string = (String) encodeMethod.invoke(field.get(null), instance);
                return new JSONObject(string);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        public T decode(JSONObject jsonObject) {
            Class<T> genericClassIfNeed = getGenericClassIfNeed();
            try {
                Field field = genericClassIfNeed.getDeclaredField("CODE_STRING");
                Method encodeMethod = field.getType().getDeclaredMethod("decode", String.class);
                return (T) encodeMethod.invoke(field.get(null), jsonObject.toString());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
