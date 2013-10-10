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
