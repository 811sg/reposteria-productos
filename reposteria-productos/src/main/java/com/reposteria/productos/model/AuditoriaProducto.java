package com.reposteria.productos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Registro de la traza de responsabilidad: quién hizo qué sobre un producto. */
@Entity
@Table(name = "auditoria_producto")
@Getter
@Setter
@NoArgsConstructor
public class AuditoriaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(nullable = false, length = 30)
    private String accion;

    private Integer idProducto;

    @Column(length = 100)
    private String nombreProducto;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public AuditoriaProducto(String usuario, String accion, Integer idProducto, String nombreProducto) {
        this.usuario = usuario;
        this.accion = accion;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.fecha = LocalDateTime.now();
    }
}
