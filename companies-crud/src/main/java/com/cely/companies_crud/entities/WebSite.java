package com.cely.companies_crud.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class WebSite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "category")
    @Enumerated(EnumType.STRING)
    private  Category category;
    private String description;

}
