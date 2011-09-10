package com.mapitprices.Utilities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/5/11
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class DotNetGsonDateTimeDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            String date = jsonElement.getAsString();
            String secondsSinceEpochStr = date.substring(date.indexOf("(") + 1, date.indexOf(")"));
            long secondsSinceEpoch = Long.parseLong(secondsSinceEpochStr);
            return new Date(secondsSinceEpoch);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
