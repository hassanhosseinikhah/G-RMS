package com.clarity;

import com.clarity.config.ApplicationVariables;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
public class DroolsConsumerApplication {

    private final Driver driver;
    private final ApplicationVariables appVars;


    public static void main(String[] args) {
        SpringApplication.run(DroolsConsumerApplication.class, args);
    }

    //    @Sch
    @PostConstruct
    @Transactional
    public void createDataModel() {
        try {
            try (Session session = driver.session()) {
                session.run(appVars.getDeleteAllNodeQuery());
            }
            try (Session session = driver.session()) {
                session.run("CALL apoc.trigger.add('Updating Number of Cells in Site node',\n" +
                        "'UNWIND apoc.trigger.nodesByLabel($assignedLabels,\"Cell\") as n\n" +
                        "match(bts:BTS)-[]->(n) set bts.numberOfCells=COALESCE(bts.numberOfCells,0) + 1', {phase:'afterAsync'});");
            }
            try (Session session = driver.session()) {
                session.run("CALL apoc.trigger.add('Cell Down Inference',\n" +
                        "'UNWIND apoc.trigger.nodesByLabel($assignedLabels,\"AlarmInterval\") as n\n" +
                        "match(p:Cell)-[]->(n) where n.offTime-n.reported>9000\n" +
                        "create(p)-[:hasTicket]->(:Ticket{type:\"CellDown\", reported:n.reported,offTime:n.offTime})', {phase:'afterAsync'});");
            }
            try (Session session = driver.session()) {
                session.run("CALL apoc.trigger.add('Site Down Inference-Step 1',\n" +
                        "'UNWIND apoc.trigger.nodesByLabel($assignedLabels,\"Ticket\") as n\n" +
                        "match(bts:BTS)-[]->(:Cell)-[]->(n{type:\"CellDown\"})\n" +
                        "set bts.numberOfCellsDown = COALESCE(bts.numberOfCellsDown,0) + 1 , bts.minOffTime = apoc.coll.min([bts.minOffTime,n.offTime]) , bts.maxReported = apoc.coll.max([bts.maxReported,n.reported])', {phase:'afterAsync'});");
            }
            try (Session session = driver.session()) {
                session.run("CALL apoc.trigger.add('Site Down Inference-Step 2',\n" +
                        "'UNWIND apoc.trigger.propertiesByKey($assignedNodeProperties,\"numberOfCellsDown\") as prop\n" +
                        "with prop.node as n\n" +
                        "match(n) where n.numberOfCellsDown=n.numberOfCells and n.maxReported <= n.minOffTime create (n)-[:hasTicket]->(:Ticket{type:\"SiteDown\", reported:n.maxReported, offTime:n.minOffTime})', {phase:'afterAsync'});");
            }
            try (Session session = driver.session()) {
                session.run("CALL apoc.trigger.add('Cell Fluctuation Inference',\n" +
                        "'UNWIND apoc.trigger.nodesByLabel($assignedLabels,\"AlarmInterval\") as n\n" +
                        "match(m:AlarmInterval)<-[]-(p:Cell)-[]->(n) where n.offTime-n.reported<9000 and m.offTime-m.reported<9000 and 0<n.reported-m.offTime<=3000\n" +
                        "create(p)-[:hasTicket]->(:Ticket{type:\"CellFluc\", reported:n.reported,offTime:n.offTime})', {phase:'afterAsync'});");
            }
            try (Session session = driver.session()) {

                session.run("CALL apoc.trigger.add('Site Fluctuation Inference-Step 1',\n" +
                        "'UNWIND apoc.trigger.nodesByLabel($assignedLabels,\"Ticket\") as n\n" +
                        "match(bts:BTS)-[]->(:Cell)-[]->(n{type:\"CellFluc\"})\n" +
                        "set bts.numberOfCellsFluctuating = COALESCE(bts.numberOfCellsFluctuating,0) + 1', {phase:'afterAsync'});");
            }
            try (Session session = driver.session()) {
                session.run("CALL apoc.trigger.add('Site Fluctuation Inference-Step 2',\n" +
                        "'UNWIND apoc.trigger.propertiesByKey($assignedNodeProperties,\"numberOfCellsFluctuating\") as prop\n" +
                        "with prop.node as n\n" +
                        "match(n) where n.numberOfCellsFluctuating=n.numberOfCells create (n)-[:hasTicket]->(:Ticket{type:\"SiteFluc\"})', {phase:'afterAsync'});");
            }

            try (Session session = driver.session()) {

                session.run("CREATE (bsc1:BSC{index:'B111H'})\n" +
                        "CREATE (b1200:BTS{index:'TH1G1200'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1200A'})<-[:child]-(b1200)\n" +
                        "CREATE (:Cell{index:'TH1G1200B'})<-[:child]-(b1200)\n" +
                        "CREATE (b1201:BTS{index:'TH1G1201'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1201A'})<-[:child]-(b1201)\n" +
                        "CREATE (:Cell{index:'TH1G1201B'})<-[:child]-(b1201)\n" +
                        "CREATE (:Cell{index:'TH1G1201C'})<-[:child]-(b1201)\n" +
                        "CREATE (b1202:BTS{index:'TH1G1202'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1202A'})<-[:child]-(b1202)\n" +
                        "CREATE (:Cell{index:'TH1G1202B'})<-[:child]-(b1202)\n" +
                        "CREATE (:Cell{index:'TH1G1202C'})<-[:child]-(b1202)\n" +
                        "CREATE (:Cell{index:'TH1G1202D'})<-[:child]-(b1202)\n" +
                        "CREATE (b1203:BTS{index:'TH1G1203'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1203A'})<-[:child]-(b1203)\n" +
                        "CREATE (:Cell{index:'TH1G1203B'})<-[:child]-(b1203)\n" +
                        "CREATE (b1204:BTS{index:'TH1G1204'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1204A'})<-[:child]-(b1204)\n" +
                        "CREATE (:Cell{index:'TH1G1204B'})<-[:child]-(b1204)\n" +
                        "CREATE (:Cell{index:'TH1G1204C'})<-[:child]-(b1204)\n" +
                        "CREATE (b1205:BTS{index:'TH1G1205'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1205A'})<-[:child]-(b1205)\n" +
                        "CREATE (:Cell{index:'TH1G1205B'})<-[:child]-(b1205)\n" +
                        "CREATE (:Cell{index:'TH1G1205C'})<-[:child]-(b1205)\n" +
                        "CREATE (:Cell{index:'TH1G1205D'})<-[:child]-(b1205)\n" +
                        "CREATE (b1206:BTS{index:'TH1G1206'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1206A'})<-[:child]-(b1206)\n" +
                        "CREATE (:Cell{index:'TH1G1206B'})<-[:child]-(b1206)\n" +
                        "CREATE (b1207:BTS{index:'TH1G1207'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1207A'})<-[:child]-(b1207)\n" +
                        "CREATE (:Cell{index:'TH1G1207B'})<-[:child]-(b1207)\n" +
                        "CREATE (:Cell{index:'TH1G1207C'})<-[:child]-(b1207)\n" +
                        "CREATE (b1208:BTS{index:'TH1G1208'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1208A'})<-[:child]-(b1208)\n" +
                        "CREATE (:Cell{index:'TH1G1208B'})<-[:child]-(b1208)\n" +
                        "CREATE (:Cell{index:'TH1G1208C'})<-[:child]-(b1208)\n" +
                        "CREATE (:Cell{index:'TH1G1208D'})<-[:child]-(b1208)\n" +
                        "CREATE (b1209:BTS{index:'TH1G1209'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1209A'})<-[:child]-(b1209)\n" +
                        "CREATE (:Cell{index:'TH1G1209B'})<-[:child]-(b1209)\n" +
                        "CREATE (b1210:BTS{index:'TH1G1210'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1210A'})<-[:child]-(b1210)\n" +
                        "CREATE (:Cell{index:'TH1G1210B'})<-[:child]-(b1210)\n" +
                        "CREATE (:Cell{index:'TH1G1210C'})<-[:child]-(b1210)\n" +
                        "CREATE (b1211:BTS{index:'TH1G1211'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1211A'})<-[:child]-(b1211)\n" +
                        "CREATE (:Cell{index:'TH1G1211B'})<-[:child]-(b1211)\n" +
                        "CREATE (:Cell{index:'TH1G1211C'})<-[:child]-(b1211)\n" +
                        "CREATE (:Cell{index:'TH1G1211D'})<-[:child]-(b1211)\n" +
                        "CREATE (b1212:BTS{index:'TH1G1212'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1212A'})<-[:child]-(b1212)\n" +
                        "CREATE (:Cell{index:'TH1G1212B'})<-[:child]-(b1212)\n" +
                        "CREATE (b1213:BTS{index:'TH1G1213'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1213A'})<-[:child]-(b1213)\n" +
                        "CREATE (:Cell{index:'TH1G1213B'})<-[:child]-(b1213)\n" +
                        "CREATE (:Cell{index:'TH1G1213C'})<-[:child]-(b1213)\n" +
                        "CREATE (b1214:BTS{index:'TH1G1214'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1214A'})<-[:child]-(b1214)\n" +
                        "CREATE (:Cell{index:'TH1G1214B'})<-[:child]-(b1214)\n" +
                        "CREATE (:Cell{index:'TH1G1214C'})<-[:child]-(b1214)\n" +
                        "CREATE (:Cell{index:'TH1G1214D'})<-[:child]-(b1214)\n" +
                        "CREATE (b1215:BTS{index:'TH1G1215'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1215A'})<-[:child]-(b1215)\n" +
                        "CREATE (:Cell{index:'TH1G1215B'})<-[:child]-(b1215)\n" +
                        "CREATE (b1216:BTS{index:'TH1G1216'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1216A'})<-[:child]-(b1216)\n" +
                        "CREATE (:Cell{index:'TH1G1216B'})<-[:child]-(b1216)\n" +
                        "CREATE (:Cell{index:'TH1G1216C'})<-[:child]-(b1216)\n" +
                        "CREATE (b1217:BTS{index:'TH1G1217'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1217A'})<-[:child]-(b1217)\n" +
                        "CREATE (:Cell{index:'TH1G1217B'})<-[:child]-(b1217)\n" +
                        "CREATE (:Cell{index:'TH1G1217C'})<-[:child]-(b1217)\n" +
                        "CREATE (:Cell{index:'TH1G1217D'})<-[:child]-(b1217)\n" +
                        "CREATE (b1218:BTS{index:'TH1G1218'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1218A'})<-[:child]-(b1218)\n" +
                        "CREATE (:Cell{index:'TH1G1218B'})<-[:child]-(b1218)\n" +
                        "CREATE (b1219:BTS{index:'TH1G1219'})<-[:child]-(bsc1)\n" +
                        "CREATE (:Cell{index:'TH1G1219A'})<-[:child]-(b1219)\n" +
                        "CREATE (:Cell{index:'TH1G1219B'})<-[:child]-(b1219)\n" +
                        "CREATE (:Cell{index:'TH1G1219C'})<-[:child]-(b1219)\n" +
                        "CREATE (bsc2:BSC{index:'B222H'})\n" +
                        "CREATE (b1220:BTS{index:'TH2G1220'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1220A1'})<-[:child]-(b1220)\n" +
                        "CREATE (:Cell{index:'TH2G1220B1'})<-[:child]-(b1220)\n" +
                        "CREATE (:Cell{index:'TH2G1220C1'})<-[:child]-(b1220)\n" +
                        "CREATE (:Cell{index:'TH2G1220D1'})<-[:child]-(b1220)\n" +
                        "CREATE (b1221:BTS{index:'TH2G1221'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1221A1'})<-[:child]-(b1221)\n" +
                        "CREATE (:Cell{index:'TH2G1221B1'})<-[:child]-(b1221)\n" +
                        "CREATE (b1222:BTS{index:'TH2G1222'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1222A1'})<-[:child]-(b1222)\n" +
                        "CREATE (:Cell{index:'TH2G1222B1'})<-[:child]-(b1222)\n" +
                        "CREATE (:Cell{index:'TH2G1222C1'})<-[:child]-(b1222)\n" +
                        "CREATE (b1223:BTS{index:'TH2G1223'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1223A1'})<-[:child]-(b1223)\n" +
                        "CREATE (:Cell{index:'TH2G1223B1'})<-[:child]-(b1223)\n" +
                        "CREATE (:Cell{index:'TH2G1223C1'})<-[:child]-(b1223)\n" +
                        "CREATE (:Cell{index:'TH2G1223D1'})<-[:child]-(b1223)\n" +
                        "CREATE (b1224:BTS{index:'TH2G1224'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1224A1'})<-[:child]-(b1224)\n" +
                        "CREATE (:Cell{index:'TH2G1224B1'})<-[:child]-(b1224)\n" +
                        "CREATE (b1225:BTS{index:'TH2G1225'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1225A1'})<-[:child]-(b1225)\n" +
                        "CREATE (:Cell{index:'TH2G1225B1'})<-[:child]-(b1225)\n" +
                        "CREATE (:Cell{index:'TH2G1225C1'})<-[:child]-(b1225)\n" +
                        "CREATE (b1226:BTS{index:'TH2G1226'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1226A1'})<-[:child]-(b1226)\n" +
                        "CREATE (:Cell{index:'TH2G1226B1'})<-[:child]-(b1226)\n" +
                        "CREATE (:Cell{index:'TH2G1226C1'})<-[:child]-(b1226)\n" +
                        "CREATE (:Cell{index:'TH2G1226D1'})<-[:child]-(b1226)\n" +
                        "CREATE (b1227:BTS{index:'TH2G1227'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1227A1'})<-[:child]-(b1227)\n" +
                        "CREATE (:Cell{index:'TH2G1227B1'})<-[:child]-(b1227)\n" +
                        "CREATE (b1228:BTS{index:'TH2G1228'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1228A1'})<-[:child]-(b1228)\n" +
                        "CREATE (:Cell{index:'TH2G1228B1'})<-[:child]-(b1228)\n" +
                        "CREATE (:Cell{index:'TH2G1228C1'})<-[:child]-(b1228)\n" +
                        "CREATE (b1229:BTS{index:'TH2G1229'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1229A1'})<-[:child]-(b1229)\n" +
                        "CREATE (:Cell{index:'TH2G1229B1'})<-[:child]-(b1229)\n" +
                        "CREATE (:Cell{index:'TH2G1229C1'})<-[:child]-(b1229)\n" +
                        "CREATE (:Cell{index:'TH2G1229D1'})<-[:child]-(b1229)\n" +
                        "CREATE (b1230:BTS{index:'TH2G1230'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1230A1'})<-[:child]-(b1230)\n" +
                        "CREATE (:Cell{index:'TH2G1230B1'})<-[:child]-(b1230)\n" +
                        "CREATE (b1231:BTS{index:'TH2G1231'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1231A1'})<-[:child]-(b1231)\n" +
                        "CREATE (:Cell{index:'TH2G1231B1'})<-[:child]-(b1231)\n" +
                        "CREATE (:Cell{index:'TH2G1231C1'})<-[:child]-(b1231)\n" +
                        "CREATE (b1232:BTS{index:'TH2G1232'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1232A1'})<-[:child]-(b1232)\n" +
                        "CREATE (:Cell{index:'TH2G1232B1'})<-[:child]-(b1232)\n" +
                        "CREATE (:Cell{index:'TH2G1232C1'})<-[:child]-(b1232)\n" +
                        "CREATE (:Cell{index:'TH2G1232D1'})<-[:child]-(b1232)\n" +
                        "CREATE (b1233:BTS{index:'TH2G1233'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1233A1'})<-[:child]-(b1233)\n" +
                        "CREATE (:Cell{index:'TH2G1233B1'})<-[:child]-(b1233)\n" +
                        "CREATE (b1234:BTS{index:'TH2G1234'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1234A1'})<-[:child]-(b1234)\n" +
                        "CREATE (:Cell{index:'TH2G1234B1'})<-[:child]-(b1234)\n" +
                        "CREATE (:Cell{index:'TH2G1234C1'})<-[:child]-(b1234)\n" +
                        "CREATE (b1235:BTS{index:'TH2G1235'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1235A1'})<-[:child]-(b1235)\n" +
                        "CREATE (:Cell{index:'TH2G1235B1'})<-[:child]-(b1235)\n" +
                        "CREATE (:Cell{index:'TH2G1235C1'})<-[:child]-(b1235)\n" +
                        "CREATE (:Cell{index:'TH2G1235D1'})<-[:child]-(b1235)\n" +
                        "CREATE (b1236:BTS{index:'TH2G1236'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1236A1'})<-[:child]-(b1236)\n" +
                        "CREATE (:Cell{index:'TH2G1236B1'})<-[:child]-(b1236)\n" +
                        "CREATE (b1237:BTS{index:'TH2G1237'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1237A1'})<-[:child]-(b1237)\n" +
                        "CREATE (:Cell{index:'TH2G1237B1'})<-[:child]-(b1237)\n" +
                        "CREATE (:Cell{index:'TH2G1237C1'})<-[:child]-(b1237)\n" +
                        "CREATE (b1238:BTS{index:'TH2G1238'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1238A1'})<-[:child]-(b1238)\n" +
                        "CREATE (:Cell{index:'TH2G1238B1'})<-[:child]-(b1238)\n" +
                        "CREATE (:Cell{index:'TH2G1238C1'})<-[:child]-(b1238)\n" +
                        "CREATE (:Cell{index:'TH2G1238D1'})<-[:child]-(b1238)\n" +
                        "CREATE (b1239:BTS{index:'TH2G1239'})<-[:child]-(bsc2)\n" +
                        "CREATE (:Cell{index:'TH2G1239A1'})<-[:child]-(b1239)\n" +
                        "CREATE (:Cell{index:'TH2G1239B1'})<-[:child]-(b1239)\n" +
                        "CREATE (bsc3:BSC{index:'B333H'})\n" +
                        "CREATE (b1240:BTS{index:'TH3G1240'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1240A1'})<-[:child]-(b1240)\n" +
                        "CREATE (:Cell{index:'TH3G1240A2'})<-[:child]-(b1240)\n" +
                        "CREATE (:Cell{index:'TH3G1240A3'})<-[:child]-(b1240)\n" +
                        "CREATE (b1241:BTS{index:'TH3G1241'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1241A1'})<-[:child]-(b1241)\n" +
                        "CREATE (:Cell{index:'TH3G1241A2'})<-[:child]-(b1241)\n" +
                        "CREATE (:Cell{index:'TH3G1241A3'})<-[:child]-(b1241)\n" +
                        "CREATE (:Cell{index:'TH3G1241A4'})<-[:child]-(b1241)\n" +
                        "CREATE (b1242:BTS{index:'TH3G1242'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1242A1'})<-[:child]-(b1242)\n" +
                        "CREATE (:Cell{index:'TH3G1242A2'})<-[:child]-(b1242)\n" +
                        "CREATE (b1243:BTS{index:'TH3G1243'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1243A1'})<-[:child]-(b1243)\n" +
                        "CREATE (:Cell{index:'TH3G1243A2'})<-[:child]-(b1243)\n" +
                        "CREATE (:Cell{index:'TH3G1243A3'})<-[:child]-(b1243)\n" +
                        "CREATE (b1244:BTS{index:'TH3G1244'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1244A1'})<-[:child]-(b1244)\n" +
                        "CREATE (:Cell{index:'TH3G1244A2'})<-[:child]-(b1244)\n" +
                        "CREATE (:Cell{index:'TH3G1244A3'})<-[:child]-(b1244)\n" +
                        "CREATE (:Cell{index:'TH3G1244A4'})<-[:child]-(b1244)\n" +
                        "CREATE (b1245:BTS{index:'TH3G1245'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1245A1'})<-[:child]-(b1245)\n" +
                        "CREATE (:Cell{index:'TH3G1245A2'})<-[:child]-(b1245)\n" +
                        "CREATE (b1246:BTS{index:'TH3G1246'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1246A1'})<-[:child]-(b1246)\n" +
                        "CREATE (:Cell{index:'TH3G1246A2'})<-[:child]-(b1246)\n" +
                        "CREATE (:Cell{index:'TH3G1246A3'})<-[:child]-(b1246)\n" +
                        "CREATE (b1247:BTS{index:'TH3G1247'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1247A1'})<-[:child]-(b1247)\n" +
                        "CREATE (:Cell{index:'TH3G1247A2'})<-[:child]-(b1247)\n" +
                        "CREATE (:Cell{index:'TH3G1247A3'})<-[:child]-(b1247)\n" +
                        "CREATE (:Cell{index:'TH3G1247A4'})<-[:child]-(b1247)\n" +
                        "CREATE (b1248:BTS{index:'TH3G1248'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1248A1'})<-[:child]-(b1248)\n" +
                        "CREATE (:Cell{index:'TH3G1248A2'})<-[:child]-(b1248)\n" +
                        "CREATE (b1249:BTS{index:'TH3G1249'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1249A1'})<-[:child]-(b1249)\n" +
                        "CREATE (:Cell{index:'TH3G1249A2'})<-[:child]-(b1249)\n" +
                        "CREATE (:Cell{index:'TH3G1249A3'})<-[:child]-(b1249)\n" +
                        "CREATE (b1250:BTS{index:'TH3G1250'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1250A1'})<-[:child]-(b1250)\n" +
                        "CREATE (:Cell{index:'TH3G1250A2'})<-[:child]-(b1250)\n" +
                        "CREATE (:Cell{index:'TH3G1250A3'})<-[:child]-(b1250)\n" +
                        "CREATE (:Cell{index:'TH3G1250A4'})<-[:child]-(b1250)\n" +
                        "CREATE (b1251:BTS{index:'TH3G1251'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1251A1'})<-[:child]-(b1251)\n" +
                        "CREATE (:Cell{index:'TH3G1251A2'})<-[:child]-(b1251)\n" +
                        "CREATE (b1252:BTS{index:'TH3G1252'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1252A1'})<-[:child]-(b1252)\n" +
                        "CREATE (:Cell{index:'TH3G1252A2'})<-[:child]-(b1252)\n" +
                        "CREATE (:Cell{index:'TH3G1252A3'})<-[:child]-(b1252)\n" +
                        "CREATE (b1253:BTS{index:'TH3G1253'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1253A1'})<-[:child]-(b1253)\n" +
                        "CREATE (:Cell{index:'TH3G1253A2'})<-[:child]-(b1253)\n" +
                        "CREATE (:Cell{index:'TH3G1253A3'})<-[:child]-(b1253)\n" +
                        "CREATE (:Cell{index:'TH3G1253A4'})<-[:child]-(b1253)\n" +
                        "CREATE (b1254:BTS{index:'TH3G1254'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1254A1'})<-[:child]-(b1254)\n" +
                        "CREATE (:Cell{index:'TH3G1254A2'})<-[:child]-(b1254)\n" +
                        "CREATE (b1255:BTS{index:'TH3G1255'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1255A1'})<-[:child]-(b1255)\n" +
                        "CREATE (:Cell{index:'TH3G1255A2'})<-[:child]-(b1255)\n" +
                        "CREATE (:Cell{index:'TH3G1255A3'})<-[:child]-(b1255)\n" +
                        "CREATE (b1256:BTS{index:'TH3G1256'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1256A1'})<-[:child]-(b1256)\n" +
                        "CREATE (:Cell{index:'TH3G1256A2'})<-[:child]-(b1256)\n" +
                        "CREATE (:Cell{index:'TH3G1256A3'})<-[:child]-(b1256)\n" +
                        "CREATE (:Cell{index:'TH3G1256A4'})<-[:child]-(b1256)\n" +
                        "CREATE (b1257:BTS{index:'TH3G1257'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1257A1'})<-[:child]-(b1257)\n" +
                        "CREATE (:Cell{index:'TH3G1257A2'})<-[:child]-(b1257)\n" +
                        "CREATE (b1258:BTS{index:'TH3G1258'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1258A1'})<-[:child]-(b1258)\n" +
                        "CREATE (:Cell{index:'TH3G1258A2'})<-[:child]-(b1258)\n" +
                        "CREATE (:Cell{index:'TH3G1258A3'})<-[:child]-(b1258)\n" +
                        "CREATE (b1259:BTS{index:'TH3G1259'})<-[:child]-(bsc3)\n" +
                        "CREATE (:Cell{index:'TH3G1259A1'})<-[:child]-(b1259)\n" +
                        "CREATE (:Cell{index:'TH3G1259A2'})<-[:child]-(b1259)\n" +
                        "CREATE (:Cell{index:'TH3G1259A3'})<-[:child]-(b1259)\n" +
                        "CREATE (:Cell{index:'TH3G1259A4'})<-[:child]-(b1259)");
            }
            System.err.println("DONE");

        } catch (Exception e) {
            System.err.println("parham   -----------------------    "+e);
            createDataModel();
            System.err.println("call again this method");
        }
    }
}
