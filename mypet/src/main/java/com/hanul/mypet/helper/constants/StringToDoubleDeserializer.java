package com.hanul.mypet.helper.constants;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class StringToDoubleDeserializer extends StdDeserializer<Double> {

    // 기본 생성자
    public StringToDoubleDeserializer() {
        super(Double.class);
    }

    @Override
    public Double deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException {
        String value = jsonParser.getText();
        if (value == null || value.isEmpty()) {
            return 0.0; // 기본값 처리
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // 숫자로 변환할 수 없는 경우 기본값 반환
            return 0.0;
        }
    }
}
