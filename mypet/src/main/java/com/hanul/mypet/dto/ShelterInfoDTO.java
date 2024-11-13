package com.hanul.mypet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShelterInfoDTO {
    @JacksonXmlProperty(localName = "careNm")
    private String careNm;
    
    @JacksonXmlProperty(localName = "careRegNo")
    private String careRegNo;

    @JacksonXmlProperty(localName = "orgNm")
    private String orgNm;

    @JacksonXmlProperty(localName = "divisionNm")
    private String divisionNm;

    @JacksonXmlProperty(localName = "saveTrgtAnimal")
    private String saveTrgtAnimal;

    @JacksonXmlProperty(localName = "careAddr")
    private String careAddr;

    @JacksonXmlProperty(localName = "jibunAddr")
    private String jibunAddr;

    @JacksonXmlProperty(localName = "lat")
    private double lat;

    @JacksonXmlProperty(localName = "lng")
    private double lng;

    @JacksonXmlProperty(localName = "dsignationDate")
    private String dsignationDate;

    @JacksonXmlProperty(localName = "weekOprStime")
    private String weekOprStime;

    @JacksonXmlProperty(localName = "weekOprEtime")
    private String weekOprEtime;

    @JacksonXmlProperty(localName = "closeDay")
    private String closeDay;

    @JacksonXmlProperty(localName = "vetPersonCnt")
    private int vetPersonCnt;

    @JacksonXmlProperty(localName = "specsPersonCnt")
    private int specsPersonCnt;

    @JacksonXmlProperty(localName = "careTel")
    private String careTel;

    @JacksonXmlProperty(localName = "dataStdDt")
    private String dataStdDt;
}
