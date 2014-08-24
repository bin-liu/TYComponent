package com.tangyu.component;

import android.test.AndroidTestCase;

import com.alibaba.fastjson.JSON;
import com.tangyu.component.demo.util.JavaBeanV2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author binliu on 8/24/14.
 */
public class TestJavaBean extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEnDecode() throws Exception {
        enDeCode(JavaBeanV2.class);
    }

    /**
     * 判定范围:
     * 1. json封装解析
     * 2. 函数所包含的函数名是否符合预期，get + is = set
     * 2. 解析后数量是否为预期 json_key_count = set.
     * @param tClass
     * @param <T>
     */
    private <T> void enDeCode(Class<T> tClass) {
        try {
            T t = tClass.newInstance();
            enDeCode(t);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private <T> void enDeCode(T t) throws JSONException {
        Class<T> tClass = (Class<T>) t.getClass();
        String s = JSON.toJSONString(t);
        JSONObject jsonObject = new JSONObject(s);
        T t1 = JSON.parseObject(s, tClass);
        assertEquals(t, t1);
        Method[] declaredMethods = tClass.getDeclaredMethods();
        int sumGet = 0, sumSet = 0, sumIs = 0;
        for (Method item : declaredMethods) {
            int modifiers = item.getModifiers();
            if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
                String name = item.getName();
                if (name.startsWith("get")) {
                    ++sumGet;
                } else if (name.startsWith("set")) {
                    ++sumSet;
                } else if (name.startsWith("is")) {
                    ++sumIs;
                }
            }
        }
        assertEquals(sumGet + sumIs, sumSet);
        JSONArray names = jsonObject.names();
        int length = null == names ? 0 : names.length();
        assertTrue(length <= sumSet);
//            assertEquals(length, sumSet);// 不能相等。对于值为null的对象，会忽略解析。所以length<=sumSet.
    }

}
