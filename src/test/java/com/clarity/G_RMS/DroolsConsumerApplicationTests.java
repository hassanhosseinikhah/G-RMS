package com.clarity.G_RMS;

import com.clarity.dto.Alarm;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class DroolsConsumerApplicationTests {
    private final KieSession kieSession;

    @Autowired
    public DroolsConsumerApplicationTests(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    @Test
    void contextLoads() {
        createCellFluc();
//        createCellDown();
//        createSiteFluc();
//        createSiteDown();
    }

    public void createCellFluc() {

        List<Alarm> alarms = new ArrayList<>();
        Date onTime = new Date();
        Alarm alarmOn1 = new Alarm("cell", onTime, null, "TH2G1226", "TH2G1226B1", "on", "a");
        Alarm alarmOff1 = new Alarm("cell", null, new Date(onTime.getTime() + (1000 * 4)), "TH2G1226", "TH2G1226B1", "off", "a");
        Alarm alarmOn2 = new Alarm("cell", new Date(onTime.getTime() + (1000 * 5)), null, "TH2G1226", "TH2G1226B1", "on", "b");
        Alarm alarmOff2 = new Alarm("cell", null, new Date(onTime.getTime() + (1000 * 8)), "TH2G1226", "TH2G1226B1", "off", "b");

        alarms.add(alarmOn1);
        alarms.add(alarmOff1);
        alarms.add(alarmOn2);
        alarms.add(alarmOff2);

        for (Alarm alarm : alarms) {
            kieSession.insert(alarm);
            kieSession.fireAllRules();
        }

    }

    public void createCellDown() {
        List<Alarm> alarms = new ArrayList<>();
        Date onTime = new Date();
        Alarm alarmOn1 = new Alarm("cell", onTime, null, "TH1G1214", "TH1G1214B", "on", "a");
        Alarm alarmOff1 = new Alarm("cell", null, new Date(onTime.getTime() + (1000 * 12)), "TH1G1214", "TH1G1214B", "off", "a");

        alarms.add(alarmOn1);
        alarms.add(alarmOff1);
        for (Alarm alarm : alarms) {
            kieSession.insert(alarm);
            kieSession.fireAllRules();
        }
    }

    public void createSiteFluc() {
        List<Alarm> alarms = new ArrayList<>();
        Date onTime = new Date();
        Alarm alarmOn1 = new Alarm("cell", onTime, null, "TH1G1206", "TH1G1206B", "on", "a");
        Alarm alarmOff1 = new Alarm("cell", null, new Date(onTime.getTime() + (1000 * 2)), "TH1G1206", "TH1G1206B", "off", "a");

        Alarm alarmOn2 = new Alarm("cell", new Date(onTime.getTime() + (1000 * 3)), null, "TH1G1206", "TH1G1206B", "on", "b");
        Alarm alarmOff2 = new Alarm("cell", null, new Date(onTime.getTime() + (1000 * 7)), "TH1G1206", "TH1G1206B", "off", "b");

        Alarm alarmOn3 = new Alarm("cell", new Date(onTime.getTime() + (1000 * 1)), null, "TH1G1206", "TH1G1206A", "on", "c");
        Alarm alarmOff3 = new Alarm("cell", null, new Date(onTime.getTime() + (1000 * 3)), "TH1G1206", "TH1G1206A", "off", "c");

        Alarm alarmOn4 = new Alarm("cell", new Date(onTime.getTime() + (1000 * 5)), null, "TH1G1206", "TH1G1206A", "on", "d");
        Alarm alarmOff4 = new Alarm("cell", null, new Date(onTime.getTime() + (1000 * 10)), "TH1G1206", "TH1G1206A", "off", "d");
        alarms.add(alarmOn1);
        alarms.add(alarmOff1);
        alarms.add(alarmOn2);
        alarms.add(alarmOff2);
        alarms.add(alarmOn3);
        alarms.add(alarmOff3);
        alarms.add(alarmOn4);
        alarms.add(alarmOff4);

        for (Alarm alarm : alarms) {
            kieSession.insert(alarm);
            kieSession.fireAllRules();
        }
    }

    public void createSiteDown() {
        List<Alarm> alarms = new ArrayList<>();
        Date onTime = new Date();
        Alarm alarmOn1 = new Alarm("cell", onTime, null, "TH3G1248", "TH3G1248A1", "on", "a");
        Alarm alarmOff1 = new Alarm("cell", null, new Date(onTime.getTime() + (1000 * 10)), "TH3G1248", "TH3G1248A1", "off", "a");

        Alarm alarmOn2 = new Alarm("cell", new Date(onTime.getTime() + (1000 * 1)), null, "TH3G1248", "TH3G1248A2", "on", "b");
        Alarm alarmOff2 = new Alarm("cell", null, new Date(onTime.getTime() + (1000 * 12)), "TH3G1248", "TH3G1248A2", "off", "b");


        alarms.add(alarmOn1);
        alarms.add(alarmOff1);
        alarms.add(alarmOn2);
        alarms.add(alarmOff2);
        for (Alarm alarm : alarms) {
            kieSession.insert(alarm);
            kieSession.fireAllRules();
        }

    }
}
