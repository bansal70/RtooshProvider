package com.rtoosh.provider.model.POJO.register;

/*
 * Created by rishav on 10/30/2017.
 */

import com.google.gson.annotations.SerializedName;

public class RegisterID {
    @SerializedName("id_number")
    private String idNumber;
    @SerializedName("id_type")
    private String idType;
    @SerializedName("issue_date")
    private String issueDate;
    @SerializedName("work_online")
    private String workOnline;
    @SerializedName("work_shedule")
    private String workSchedule;

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getWorkOnline() {
        return workOnline;
    }

    public void setWorkOnline(String workOnline) {
        this.workOnline = workOnline;
    }

    public String getWorkSchedule() {
        return workSchedule;
    }

    public void setWorkSchedule(String workSchedule) {
        this.workSchedule = workSchedule;
    }
}
