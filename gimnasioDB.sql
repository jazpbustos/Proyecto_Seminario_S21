-- Creación de la base de datos
CREATE DATABASE gimnasioDB;
USE gimnasioDB;

-- ======================
-- Tabla Cliente
-- ======================
CREATE TABLE Cliente (
    idCliente INT PRIMARY KEY AUTO_INCREMENT,
    DNI INT NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(100),
    fechaNacimiento DATE NOT NULL
);

-- ======================
-- Tabla Actividad
-- ======================
CREATE TABLE Actividad (
    idActividad INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    precio DOUBLE NOT NULL,
    duracion INT NOT NULL
);

-- ======================
-- Tabla Pago
-- ======================
CREATE TABLE Pago (
    idPago INT PRIMARY KEY AUTO_INCREMENT,
    idCliente INT NOT NULL,
    idActividad INT NOT NULL,
    fecha DATE NOT NULL,
    monto DOUBLE NOT NULL,
    estadoCuota VARCHAR(20) NOT NULL,
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente),
    FOREIGN KEY (idActividad) REFERENCES Actividad(idActividad)
);

-- ======================
-- Tabla Rutina
-- ======================
CREATE TABLE Rutina (
    idRutina INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT,
    fechaInicio DATE NOT NULL,
    fechaFin DATE NOT NULL
);

-- ======================
-- Tabla intermedia Cliente_Rutina
-- ======================
CREATE TABLE Cliente_Rutina (
    idCliente INT NOT NULL,
    idRutina INT NOT NULL,
    fechaAsignacion DATE NOT NULL,
    PRIMARY KEY (idCliente, idRutina),
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente),
    FOREIGN KEY (idRutina) REFERENCES Rutina(idRutina)
);

SHOW TABLES;
DESCRIBE Cliente;
DESCRIBE Pago;
DESCRIBE Rutina;
DESCRIBE Actividad;

-- ======================
-- Inserción en Cliente
-- ======================
INSERT INTO Cliente (DNI, nombre, apellido, telefono, correo, fechaNacimiento)
VALUES
(42556111, 'Jazmín', 'Bustos', '3496112233', 'jazminbustos@gmail.com', '2001-04-12'),
(40788999, 'Lucas', 'Fernández', '3496223344', 'lucasf@gmail.com', '1999-11-08'),
(40111222, 'Sofía', 'Gómez', '3496889900', NULL, '1998-05-23'),
(43000222, 'Martín', 'Acosta', '3496555777', 'martinacosta@gmail.com', '2003-09-17'),
(41555444, 'Valentina', 'Ruiz', '3496333444', NULL, '2000-01-28');

-- ======================
-- Inserción en Actividad
-- ======================
INSERT INTO Actividad (nombre, precio, duracion)
VALUES
('Musculación', 15000, 30),
('Crossfit', 20000, 30),
('Funcional', 18000, 30),
('Yoga', 16000, 30);

-- ======================
-- Inserción en Pago
-- ======================
INSERT INTO Pago (idCliente, idActividad, fecha, monto, estadoCuota)
VALUES
(1, 1, '2025-09-01', 15000, 'Pagado'),
(2, 2, '2025-09-01', 20000, 'Pendiente'),
(3, 3, '2025-09-01', 18000, 'Pagado'),
(4, 1, '2025-09-01', 15000, 'Pagado'),
(5, 4, '2025-09-01', 16000, 'Pendiente');

-- ======================
-- Inserción en Rutina
-- ======================
INSERT INTO Rutina (nombre, descripcion, fechaInicio, fechaFin)
VALUES
('Fullbody principiantes 3 días', 'Rutina general de adaptación, tres días por semana.', '2025-09-01', '2025-09-30'),
('Fuerza 4 días', 'Rutina centrada en progresión de cargas.', '2025-09-01', '2025-09-30'),
('Hipertrofia avanzada 5 días', 'Rutina dividida por grupos musculares, cinco días.', '2025-09-01', '2025-09-30'),
('Cardio + Core', 'Entrenamiento combinado aeróbico y abdominales.', '2025-09-01', '2025-09-30'),
('Stretch & Mobility', 'Enfoque en flexibilidad y movilidad articular.', '2025-09-01', '2025-09-30');

-- ======================
-- Inserción en Cliente_Rutina
-- ======================
INSERT INTO Cliente_Rutina (idCliente, idRutina, fechaAsignacion)
VALUES
(1, 1, '2025-09-01'),
(2, 2, '2025-09-01'),
(3, 3, '2025-09-01'),
(4, 1, '2025-09-01'),
(5, 4, '2025-09-01'),
(1, 5, '2025-09-15');

-- Consultar todos los clientes registrados
SELECT * FROM Cliente;

-- Consultar los pagos realizados y su vínculo con actividades
SELECT 
    P.idPago,
    C.nombre AS Cliente,
    A.nombre AS Actividad,
    P.fecha,
    P.monto,
    P.estadoCuota
FROM Pago P
JOIN Cliente C ON P.idCliente = C.idCliente
JOIN Actividad A ON P.idActividad = A.idActividad;

-- Consultar rutinas asignadas a los clientes
SELECT 
    C.nombre AS Cliente,
    R.nombre AS Rutina,
    CR.fechaAsignacion
FROM Cliente_Rutina CR
JOIN Cliente C ON CR.idCliente = C.idCliente
JOIN Rutina R ON CR.idRutina = R.idRutina;

SET SQL_SAFE_UPDATES = 0;

DELETE FROM Cliente_Rutina;
DELETE FROM Pago;
DELETE FROM Rutina;
DELETE FROM Actividad;
DELETE FROM Cliente;

-- Verificación posterior
SELECT * FROM Cliente;
SELECT * FROM Pago;
SELECT * FROM Rutina;
SELECT * FROM Actividad;