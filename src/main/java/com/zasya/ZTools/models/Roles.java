package com.zasya.ZTools.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Roles")
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
