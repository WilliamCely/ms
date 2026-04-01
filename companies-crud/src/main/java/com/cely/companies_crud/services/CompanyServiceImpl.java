package com.cely.companies_crud.services;

import com.cely.companies_crud.entities.Category;
import com.cely.companies_crud.entities.Company;
import com.cely.companies_crud.repositories.CompanyRepository;
import io.micrometer.tracing.Tracer;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class CompanyServiceImpl implements  CompanyService{

    private final CompanyRepository companyRepository;
    private final Tracer tracer;

    @Override
    public Company create(Company company) {
        company.getWebSites().forEach(webSite -> {
            if(Objects.isNull(webSite.getCategory())){
                webSite.setCategory(Category.NONE);
            }
        });
        log.info("Saving company with {} websites", company.getWebSites().size());
        return this.companyRepository.save(company);
    }

    @Override
    public Company readByName(String name) {
        var spam = tracer.nextSpan().name("readByName");
        try (Tracer.SpanInScope spanInScope = this.tracer.withSpan(spam.start())){
           log.info("Getting company from DB");
        }finally {
            spam.end();
        }
        return this.companyRepository.findByName(name)
                .orElseThrow(()-> new NoSuchElementException("Company not found"));
    }

    @Override
    public Company update(Company company, String name) {
        var companyToUpdate= this.companyRepository.findByName(name)
                .orElseThrow(()-> new NoSuchElementException("Company not found"));
        companyToUpdate.setLogo(company.getLogo());
        companyToUpdate.setFoundationDate(company.getFoundationDate());
        companyToUpdate.setFounder(company.getFounder());
        return this.companyRepository.save(companyToUpdate);
    }

    @Override
    public void delete(String name) {
        var companyToDelete= this.companyRepository.findByName(name)
                .orElseThrow(()-> new NoSuchElementException("Company not found"));
        this.companyRepository.delete(companyToDelete);

    }
}
