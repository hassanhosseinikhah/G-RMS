package com.clarity.events;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmInterval {
    private String source; // cell/site
    private String cellName;
    private String siteName;
    private String occurrenceID;
    private Date reported;
    private Date offTime;
    private Date clarityTime;
}