package com.clarity.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alarm implements Serializable {
    static final long serialVersionUID = 42L;

    private String province;
    private String tech;
    private String locCode;
    private String cell;
    private String source;
    private String alarmType;
    private Date onTime;
    private Date offTime;
    private int severity;
    private int duration;
    private String siteName;
    private String cellName;
    private String status;
    private String occurrenceID;

    public Alarm(String source, Date onTime, Date offTime, String siteName, String cellName, String status, String occurrenceID) {
        this.source = source;
        this.onTime = onTime;
        this.offTime = offTime;
        this.siteName = siteName;
        this.cellName = cellName;
        this.status = status;
        this.occurrenceID = occurrenceID;
    }
}