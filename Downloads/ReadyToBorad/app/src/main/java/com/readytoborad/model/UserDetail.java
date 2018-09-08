package com.readytoborad.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.readytoborad.database.ChildData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "parent_id",
        "father_name",
        "mother_name",
        "email",
        "phone_no",
        "childrenArr",
        "popup_flag"
})

public class UserDetail {
    @JsonProperty("parent_id")
    private String parentId;
    @JsonProperty("father_name")
    private String fatherName;
    @JsonProperty("mother_name")
    private String motherName;
    @JsonProperty("email")
    private String email;
    @JsonProperty("phone_no")
    private String phoneNo;
    @JsonProperty("childrenArr")
    private List<ChildData> childrenArr = null;
    @JsonProperty("popup_flag")
    private Integer popupFlag;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("parent_id")
    public String getParentId() {
        return parentId;
    }

    @JsonProperty("parent_id")
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @JsonProperty("father_name")
    public String getFatherName() {
        return fatherName;
    }

    @JsonProperty("father_name")
    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    @JsonProperty("mother_name")
    public String getMotherName() {
        return motherName;
    }

    @JsonProperty("mother_name")
    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("phone_no")
    public String getPhoneNo() {
        return phoneNo;
    }

    @JsonProperty("phone_no")
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @JsonProperty("childrenArr")
    public List<ChildData> getChildrenArr() {
        return childrenArr;
    }

    @JsonProperty("childrenArr")
    public void setChildrenArr(List<ChildData> childrenArr) {
        this.childrenArr = childrenArr;
    }

    @JsonProperty("popup_flag")
    public Integer getPopupFlag() {
        return popupFlag;
    }

    @JsonProperty("popup_flag")
    public void setPopupFlag(Integer popupFlag) {
        this.popupFlag = popupFlag;
    }


}
