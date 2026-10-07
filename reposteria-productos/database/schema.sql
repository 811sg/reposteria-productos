-- OPCIONAL: la aplicación crea estas tablas automáticamente (spring.jpa.hibernate.ddl-auto=update).
-- Este script solo documenta la estructura del módulo (sección 3 del documento).
CREATE DATABASE IF NOT EXISTS reposteria_db CHARACTER SET utf8mb4;
USE reposteria_db;

CREATE TABLE IF NOT EXISTS producto (
    id_producto    INT            NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(100)   NOT NULL,
    descripcion    VARCHAR(255)   NOT NULL,
    categoria      VARCHAR(100)   NOT NULL,
    precio         DECIMAL(10,2)  NOT NULL,
    disponibilidad BOOLEAN        NOT NULL,
    imagen         VARCHAR(255),
    fecha_registro DATETIME       NOT NULL,
    PRIMARY KEY (id_producto)
);
