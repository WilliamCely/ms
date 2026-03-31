package com.cely.report_ms.streams;

import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReportPublisher {

    private final StreamBridge streamBridge;

    /*
    *topic name -> consumerReport
     */
    public void publishReport(String report){
        this.streamBridge.send("consumerReport", report);
        this.streamBridge.send("consumerReport-in-O", report);
        this.streamBridge.send("consumerReport-out-O", report);
    }
}
