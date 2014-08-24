package com.tangyu.component.demo.util;

import com.tangyu.component.util.IJsonBeanV2;

/**
 * @author binliu on 8/24/14.
 */
public class JavaBeanV2 implements IJsonBeanV2 {

    private int paramA;

    private String paramB;

    public JavaBeanV2() {
    }

    public int getParamA() {
        return paramA;
    }

    public void setParamA(int paramA) {
        this.paramA = paramA;
    }

    public String getParamB() {
        return paramB;
    }

    public void setParamB(String paramB) {
        this.paramB = paramB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavaBeanV2 that = (JavaBeanV2) o;

        if (paramA != that.paramA) return false;
        if (paramB != null ? !paramB.equals(that.paramB) : that.paramB != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = paramA;
        result = 31 * result + (paramB != null ? paramB.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JavaBeanV2{" +
                "paramA=" + paramA +
                ", paramB='" + paramB + '\'' +
                '}';
    }

    public static final EncodeObject<JavaBeanV2> CODE = new EncodeObject<JavaBeanV2>() { };
    public static final EncodeString<JavaBeanV2> CODE_STRING = new EncodeString<JavaBeanV2>() { };
}
