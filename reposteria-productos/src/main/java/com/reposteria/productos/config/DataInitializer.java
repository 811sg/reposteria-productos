package com.reposteria.productos.config;

import com.reposteria.productos.model.Usuario;
import com.reposteria.productos.patterns.builder.ProductoBuilder;
import com.reposteria.productos.repository.ProductoRepository;
import com.reposteria.productos.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

/** Datos iniciales para poder probar el módulo: dos usuarios y algunos productos de ejemplo. */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarDatos(UsuarioRepository usuarios, ProductoRepository productos, PasswordEncoder encoder) {
        return args -> {
            if (usuarios.count() == 0) {
                usuarios.save(new Usuario("admin", encoder.encode("admin123"), "ADMIN"));
                usuarios.save(new Usuario("consultor", encoder.encode("consultor123"), "CONSULTOR"));
            }
            if (productos.count() == 0) {
                productos.save(new ProductoBuilder().nombre("Torta de chocolate").descripcion("Bizcocho húmedo de cacao con ganache y cobertura de chocolate")
                        .categoria("Torta").precio(new BigDecimal("58000")).disponibilidad(true).build());
                productos.save(new ProductoBuilder().nombre("Torta de zanahoria").descripcion("Torta especiada con nueces y crema de queso")
                        .categoria("Torta").precio(new BigDecimal("52000")).disponibilidad(true).build());
                productos.save(new ProductoBuilder().nombre("Tiramisú").descripcion("Postre en capas de café, mascarpone y cacao")
                        .categoria("Postre").precio(new BigDecimal("14500")).disponibilidad(true).build());
                productos.save(new ProductoBuilder().nombre("Cheesecake de frutos rojos").descripcion("Base de galleta, crema de queso y salsa de frutos rojos")
                        .categoria("Postre").precio(new BigDecimal("16000")).disponibilidad(false).build());
                productos.save(new ProductoBuilder().nombre("Galletas de avena y pasas").descripcion("Paquete de 6 galletas artesanales")
                        .categoria("Galleta").precio(new BigDecimal("9000")).disponibilidad(true).build());
                productos.save(new ProductoBuilder().nombre("Brownie con nueces").descripcion("Porción individual de brownie denso con nueces")
                        .categoria("Brownie").precio(new BigDecimal("7500")).disponibilidad(true).build());
            }
        };
    }
}
