package com.reposteria.productos.controller;

import com.reposteria.productos.model.AuditoriaProducto;
import com.reposteria.productos.repository.AuditoriaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@Tag(name = "Auditoría")
public class AuditoriaController {

    private final AuditoriaRepository repository;

    public AuditoriaController(AuditoriaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Traza de responsabilidad: últimas 50 acciones (ADMIN)")
    public List<AuditoriaProducto> ultimas() {
        return repository.findTop50ByOrderByFechaDesc();
    }
}
