package com.cely.report_listener.streams;

import com.cely.report_listener.documents.ReportDocument;
import com.cely.report_listener.repositories.ReportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
@AllArgsConstructor
public class ReportListener {

    private final ReportRepository reportRepository;

    @Bean
    public Consumer<String>consumerReport(){
        return report ->{
            this.reportRepository.save(ReportDocument.builder().content(report).build());
            log.info("Consuming report {}",report);
        };
    }
}
