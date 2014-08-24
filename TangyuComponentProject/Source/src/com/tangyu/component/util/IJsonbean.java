/**
 * The MIT License (MIT)
 * Copyright (c) 2012-2014 唐虞科技(TangyuSoft) Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.tangyu.component.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface IJsonBean {

    public static JParser PARSER = null;

    public static JEncapsulater ENCAPSULATER = null;

    public static JParserSet PARSERSET = null;

    public abstract class JParser {

        public abstract IJsonBean parserOne(String jsonStr);

        public abstract List<IJsonBean> parserList(String jsonStr);
    }

    public abstract class JEncapsulater {

        public abstract JSONObject encapsulaterOne(IJsonBean bean);

        public abstract JSONArray encapsulaterList(List<IJsonBean> beans);
    }

    public abstract class JParserSet {

        public abstract IJsonBean parserSet(String jsonArray);

    }

    public abstract class JEncapsulaterSet {

        public abstract JSONArray encapsulaterSet(IJsonBean bean);

    }

}
