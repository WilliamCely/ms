package com.cely.companies_crud.controllers;

import com.cely.companies_crud.entities.Company;
import com.cely.companies_crud.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping(path = "company")
@Slf4j
@Tag(name ="Companies resource")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "get a company given a company name")
    @GetMapping(path = "{name}")
    public ResponseEntity<Company> get(@PathVariable String name) {
        log.info("GET: company {}", name);
        return ResponseEntity.ok(this.companyService.readByName(name));
    }

    @Operation(summary = "post in DB a company given a company from body")
    @PostMapping
    public ResponseEntity<Company> post(@RequestBody Company company) {
        log.info("POST: company {}", company.getName());
        Company createdCompany = this.companyService.create(company);
        URI location = URI.create("/company/" + createdCompany.getName());
        return ResponseEntity.created(location).body(createdCompany);
    }

    @Operation(summary = "update in DB a company given a company from body")
    @PutMapping(path = "{name}")
    public ResponseEntity<Company> put(@RequestBody Company company, @PathVariable String name) {
        log.info("PUT: company {}", name);
        return ResponseEntity.ok(this.companyService.update(company, name));
    }

    @Operation(summary = "delete in DB a company given a company name")
    @DeleteMapping(path = "{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        log.info("DELETE: company {}", name);
        this.companyService.delete(name); // IMPORTANTE: Llama al servicio
        return ResponseEntity.noContent().build();
    }
}
