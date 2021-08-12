package com.encrypto.model;

import java.util.Date;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NonNull;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileStamp {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    @Size(max = 100, min = 1)
    private String name;

    @NonNull
    @JsonProperty("password")
    @Size(max = 100, min = 1)
    private String password;

    @JsonProperty("iv")
    @Size(max = 100, min = 1)
    private String iv;

    @Past
    @JsonProperty("atTime")
    private Date atTime;
}