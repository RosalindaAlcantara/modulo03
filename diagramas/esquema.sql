CREATE DATABASE cotizador2025;

-- Tabla componente (clase base)
CREATE TABLE componente (
    id VARCHAR(50) PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    costo DECIMAL(15, 2) NOT NULL,
    precio_base DECIMAL(15, 2) NOT NULL
);

-- Tabla disco_duro (hereda de componente)
CREATE TABLE disco_duro (
    id VARCHAR(50) PRIMARY KEY,
    capacidad VARCHAR(50) NOT NULL,
    FOREIGN KEY (id) REFERENCES componente(id) ON DELETE CASCADE
);

-- Tabla monitor (hereda de componente)
CREATE TABLE monitor (
    id VARCHAR(50) PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES componente(id) ON DELETE CASCADE
);

-- Tabla tarjeta_video (hereda de componente)
CREATE TABLE tarjeta_video (
    id VARCHAR(50) PRIMARY KEY,
    memoria VARCHAR(50) NOT NULL,
    FOREIGN KEY (id) REFERENCES componente(id) ON DELETE CASCADE
);

-- Tabla cotizacion
CREATE TABLE cotizacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    total DECIMAL(15, 2) NOT NULL
);

-- Tabla detalle_cotizacion
CREATE TABLE detalle_cotizacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    num INT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    cant INT NOT NULL,
    importe_cotizado DECIMAL(15, 2) NOT NULL,
    precio_base DECIMAL(15, 2) NOT NULL,
    cotizacion_id INT NOT NULL,
    componente_id VARCHAR(50) NOT NULL,
    FOREIGN KEY (cotizacion_id) REFERENCES cotizacion(id) ON DELETE CASCADE,
    FOREIGN KEY (componente_id) REFERENCES componente(id)
);

-- Tabla cotizador (para almacenar cantidades si es necesario)
CREATE TABLE cotizador (
    id INT AUTO_INCREMENT PRIMARY KEY
);

-- Tabla intermedia para la relación entre cotizador y cantidades
CREATE TABLE cotizador_cantidades (
    cotizador_id INT NOT NULL,
    cantidad INT NOT NULL,
    FOREIGN KEY (cotizador_id) REFERENCES cotizador(id) ON DELETE CASCADE
);

-- Tabla para componentes compuestos (PC)
CREATE TABLE pc (
    id VARCHAR(50) PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES componente(id) ON DELETE CASCADE
);

-- Tablas de relación para los componentes que forman parte de una PC
CREATE TABLE pc_disco_duro (
    pc_id VARCHAR(50) NOT NULL,
    disco_duro_id VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    PRIMARY KEY (pc_id, disco_duro_id),
    FOREIGN KEY (pc_id) REFERENCES pc(id) ON DELETE CASCADE,
    FOREIGN KEY (disco_duro_id) REFERENCES disco_duro(id)
);

CREATE TABLE pc_monitor (
    pc_id VARCHAR(50) NOT NULL,
    monitor_id VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    PRIMARY KEY (pc_id, monitor_id),
    FOREIGN KEY (pc_id) REFERENCES pc(id) ON DELETE CASCADE,
    FOREIGN KEY (monitor_id) REFERENCES monitor(id)
);

CREATE TABLE pc_tarjeta_video (
    pc_id VARCHAR(50) NOT NULL,
    tarjeta_video_id VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    PRIMARY KEY (pc_id, tarjeta_video_id),
    FOREIGN KEY (pc_id) REFERENCES pc(id) ON DELETE CASCADE,
    FOREIGN KEY (tarjeta_video_id) REFERENCES tarjeta_video(id)
);

-- Modificación a detalle_cotizacion para soportar componentes compuestos
ALTER TABLE detalle_cotizacion ADD COLUMN es_compuesto BOOLEAN NOT NULL DEFAULT FALSE;