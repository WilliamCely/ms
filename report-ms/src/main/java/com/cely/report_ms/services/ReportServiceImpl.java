package com.cely.report_ms.services;

import com.cely.report_ms.helpers.ReportHelper;
import com.cely.report_ms.models.Company;
import com.cely.report_ms.models.WebSite;
import com.cely.report_ms.repositories.CompaniesFallbackRepository;
import com.cely.report_ms.repositories.CompaniesRepository;
import com.cely.report_ms.streams.ReportPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final CompaniesRepository companiesRepository;
    private final ReportHelper reportHelper;
    private final CompaniesFallbackRepository companiesFallbackRepository;
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;
    private final ReportPublisher reportPublisher;
    @Override
    public String makeReport(String name) {

           var circuitBreaker = this.circuitBreakerFactory.create("companies-circuitBreaker");
           return circuitBreaker.run(
                   ()->this.makeReportMain(name),
                   throwable -> this.makeReportFallback(name, throwable)
           );
    }

    @Override
    public String saveReport(String report) {
        var format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        var placeholder = this.reportHelper.getPlaceholdersFromTemplate(report);
        var websites = Stream.of(placeholder.get(3))
                .map(website-> WebSite.builder().name(website).build())
                .toList();
        var company = Company.builder()
                .name(placeholder.get(0))
                .foundationDate(LocalDate.parse(placeholder.get(1), format))
                .founder(placeholder.get(2))
                .webSites(websites)
                .build();

        this.reportPublisher.publishReport(report);
        this.companiesRepository.postByName(company);
        return "Saved";
    }

    @Override
    public void deleteReport(String name) {
        this.companiesRepository.deleteByName(name);
    }

    private String makeReportMain(String name) {
        return reportHelper.readTemplate(this.companiesRepository.getByName(name).orElseThrow());

    }

    private String makeReportFallback(String name,Throwable error) {
        log.warn(error.getMessage());
        return reportHelper.readTemplate(this.companiesFallbackRepository.getByName(name));

    }
}
