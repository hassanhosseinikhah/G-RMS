package com.clarity.kafka;

import com.clarity.dto.Alarm;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieSession;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final Gson gson;
    private final KieSession kieSession;

    @KafkaListener(topicPartitions = @org.springframework.kafka.annotation.TopicPartition(topic = "${RMS.kafka.topic}",
            partitionOffsets = {@PartitionOffset(partition = "0", initialOffset = "-1")}))
    void listener(String data) {
        System.out.println("\n " + gson.fromJson(data, Alarm.class) + "\n");
        Alarm alarm = gson.fromJson(data, Alarm.class);
        kieSession.insert(alarm);
        kieSession.fireAllRules();
    }

}