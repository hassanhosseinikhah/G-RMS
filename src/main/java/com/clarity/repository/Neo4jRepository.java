package com.clarity.repository;

import com.clarity.events.AlarmInterval;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Neo4jRepository {

    private final Driver driver;

    @Transactional
    public void createAlarmInterval(AlarmInterval alarmInterval) {
        try {
            String query = "match (n:Cell{ index: '" + alarmInterval.getCellName() + "' }) create (:AlarmInterval{occurrenceID:'"
                    + alarmInterval.getOccurrenceID() + "', reported:" + alarmInterval.getReported().getTime() + ",offTime:"
                    + alarmInterval.getOffTime().getTime() + "}) <-[:hasAlarmInterval]- (n)";
            try (Session session = driver.session()) {
                session.run(query);
            }

            System.out.println("Alarm Added");
        } catch (Exception e) {
            System.out.println(e.getCause() + "         " + e.getMessage());
        }
    }

    public void updateAlarmInterval(AlarmInterval alarmInterval) {
        String query = "match(n:AlarmInterval{occurrenceID:'" + alarmInterval.getOccurrenceID() + "',reported:"
                + alarmInterval.getReported().getTime() + "})<-[]-(:Cell{index:'"
                + alarmInterval.getCellName() + "'}) SET n.offTime=" + alarmInterval.getOffTime().getTime();
        try (Session session = driver.session()) {
            session.run(query);
        }

        System.out.println("update");
    }
}
