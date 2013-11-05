/**
 * The MIT License (MIT)
 * Copyright (c) 2012  Tang Yu Software Corporation
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

public interface IJsonbean {

    public static JParser PARSER = null;

    public static JEncapsulater ENCAPSULATER = null;

    public static JParserSet PARSERSET = null;

    /**
     * {@link Recorder#record_contents} parser to local data
     *
     * @author bin
     */
    public abstract class JParser {

        /**
         * @param jsonStr is {@link Recorder#record_contents}
         * @return IJsonbean object
         */
        public abstract IJsonbean parserOne(String jsonStr);

        public abstract List<IJsonbean> parserList(String jsonStr);
    }

    /**
     * local data encapsulate to {@link Recorder#record_contents}.
     *
     * @author bin
     */
    public abstract class JEncapsulater {

        /**
         * @param bean IJsonbean object
         * @return is {@link Recorder#record_contents}
         */
        public abstract JSONObject encapsulaterOne(IJsonbean bean);

        public abstract JSONArray encapsulaterList(List<IJsonbean> beans);
    }

    public abstract class JParserSet {

        /**
         * @param jsonArray the array of non-Set data.
         * @return Set data
         */
        public abstract IJsonbean parserSet(String jsonArray);

    }

    public abstract class JEncapsulaterSet {

        /**
         * @param set data
         * @return non-set data array
         */
        public abstract JSONArray encapsulaterSet(IJsonbean bean);

    }

}
