package com.hanul.mypet.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.hanul.mypet.dto.ShelterInfoDTO;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 알 수 없는 필드는 무시
public class OpenAPIServiceResponse {
    private CmmMsgHeader cmmMsgHeader;
    private Body body;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // 알 수 없는 필드는 무시
    public static class CmmMsgHeader {
        private String resultCode;
        private String resultMsg;
        private String returnAuthMsg;
        private String returnReasonCode;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // 알 수 없는 필드는 무시
    public static class Body {
        private Items items;
        
        @JacksonXmlProperty(localName = "totalCount")
        private int totalCount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // 알 수 없는 필드는 무시
    public static class Items {
        @JacksonXmlElementWrapper(useWrapping = false) // `items` 태그 없이 바로 리스트 항목들이 오는 경우 사용
        @JacksonXmlProperty(localName = "item")
        private List<ShelterInfoDTO> item;
    }
}


