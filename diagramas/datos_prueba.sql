-- 1. Limpieza inicial (opcional, solo si necesitas reiniciar la base)
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE pc_tarjeta_video;
-- TRUNCATE TABLE pc_monitor;
-- TRUNCATE TABLE pc_disco_duro;
-- TRUNCATE TABLE pc;
-- TRUNCATE TABLE detalle_cotizacion;
-- TRUNCATE TABLE cotizacion;
-- TRUNCATE TABLE tarjeta_video;
-- TRUNCATE TABLE monitor;
-- TRUNCATE TABLE disco_duro;
-- TRUNCATE TABLE componente;
-- TRUNCATE TABLE cotizador_cantidades;
-- TRUNCATE TABLE cotizador;
-- SET FOREIGN_KEY_CHECKS = 1;

-- 2. Insertar componentes básicos
-- Componentes genéricos
INSERT INTO componente (id, descripcion, marca, modelo, costo, precio_base) VALUES
('comp001', 'Disco SSD 500GB', 'Samsung', '870 EVO', 120.50, 199.99),
('comp002', 'Disco HDD 1TB', 'Seagate', 'Barracuda', 45.00, 79.99),
('comp003', 'Monitor 24" FHD', 'LG', '24MK600', 150.00, 249.99),
('comp004', 'Monitor 27" QHD', 'Dell', 'S2721DGF', 300.00, 499.99),
('comp005', 'Tarjeta Video 8GB', 'NVIDIA', 'RTX 3060', 350.00, 599.99),
('comp006', 'Tarjeta Video 12GB', 'AMD', 'RX 6700 XT', 400.00, 699.99),
('comp007', 'PC Básica', 'Generic', 'Basic', 0, 0), -- Precio se calculará
('comp008', 'PC Gamer', 'Generic', 'Gamer', 0, 0), -- Precio se calculará
('comp009', 'PC Workstation', 'Generic', 'Work', 0, 0), -- Precio se calculará
('comp010', 'PC All-in-One', 'Generic', 'AIO', 0, 0); -- Precio se calculará

-- Discos duros
INSERT INTO disco_duro (id, capacidad) VALUES
('comp001', '500GB SSD'),
('comp002', '1TB HDD');

-- Monitores
INSERT INTO monitor (id) VALUES
('comp003'),
('comp004');

-- Tarjetas de video
INSERT INTO tarjeta_video (id, memoria) VALUES
('comp005', '8GB GDDR6'),
('comp006', '12GB GDDR6');

-- PCs (componentes compuestos)
INSERT INTO pc (id) VALUES
('comp007'),
('comp008'),
('comp009'),
('comp010');

-- 3. Configurar las PCs con sus componentes
-- PC Básica: 1 HDD, 1 Monitor básico
INSERT INTO pc_disco_duro (pc_id, disco_duro_id, cantidad) VALUES
('comp007', 'comp002', 1);

INSERT INTO pc_monitor (pc_id, monitor_id, cantidad) VALUES
('comp007', 'comp003', 1);

-- PC Gamer: 1 SSD, 1 HDD, 1 Tarjeta gamer, 1 Monitor gamer
INSERT INTO pc_disco_duro (pc_id, disco_duro_id, cantidad) VALUES
('comp008', 'comp001', 1),
('comp008', 'comp002', 1);

INSERT INTO pc_tarjeta_video (pc_id, tarjeta_video_id, cantidad) VALUES
('comp008', 'comp005', 1);

INSERT INTO pc_monitor (pc_id, monitor_id, cantidad) VALUES
('comp008', 'comp004', 1);

-- PC Workstation: 2 SSD, Tarjeta profesional, 2 Monitores
INSERT INTO pc_disco_duro (pc_id, disco_duro_id, cantidad) VALUES
('comp009', 'comp001', 2);

INSERT INTO pc_tarjeta_video (pc_id, tarjeta_video_id, cantidad) VALUES
('comp009', 'comp006', 1);

INSERT INTO pc_monitor (pc_id, monitor_id, cantidad) VALUES
('comp009', 'comp003', 2);

-- PC All-in-One: 1 SSD, 1 Tarjeta básica (el monitor está integrado)
INSERT INTO pc_disco_duro (pc_id, disco_duro_id, cantidad) VALUES
('comp010', 'comp001', 1);

INSERT INTO pc_tarjeta_video (pc_id, tarjeta_video_id, cantidad) VALUES
('comp010', 'comp005', 1);

-- 4. Actualizar precios de PCs basados en sus componentes
-- Primero actualizamos el precio_base
UPDATE componente c
JOIN (
    SELECT 
        p.id,
        COALESCE(SUM(comp.precio_base * pd.cantidad), 0) + 
        COALESCE(SUM(comp2.precio_base * pm.cantidad), 0) + 
        COALESCE(SUM(comp3.precio_base * pt.cantidad), 0) AS total_precio
    FROM pc p
    LEFT JOIN pc_disco_duro pd ON p.id = pd.pc_id
    LEFT JOIN componente comp ON pd.disco_duro_id = comp.id
    LEFT JOIN pc_monitor pm ON p.id = pm.pc_id
    LEFT JOIN componente comp2 ON pm.monitor_id = comp2.id
    LEFT JOIN pc_tarjeta_video pt ON p.id = pt.pc_id
    LEFT JOIN componente comp3 ON pt.tarjeta_video_id = comp3.id
    GROUP BY p.id
) AS calculos ON c.id = calculos.id
SET c.precio_base = calculos.total_precio
WHERE c.id IN ('comp007', 'comp008', 'comp009', 'comp010');

-- Luego actualizamos el costo
UPDATE componente c
JOIN (
    SELECT 
        p.id,
        COALESCE(SUM(comp.costo * pd.cantidad), 0) + 
        COALESCE(SUM(comp2.costo * pm.cantidad), 0) + 
        COALESCE(SUM(comp3.costo * pt.cantidad), 0) AS total_costo
    FROM pc p
    LEFT JOIN pc_disco_duro pd ON p.id = pd.pc_id
    LEFT JOIN componente comp ON pd.disco_duro_id = comp.id
    LEFT JOIN pc_monitor pm ON p.id = pm.pc_id
    LEFT JOIN componente comp2 ON pm.monitor_id = comp2.id
    LEFT JOIN pc_tarjeta_video pt ON p.id = pt.pc_id
    LEFT JOIN componente comp3 ON pt.tarjeta_video_id = comp3.id
    GROUP BY p.id
) AS calculos ON c.id = calculos.id
SET c.costo = calculos.total_costo
WHERE c.id IN ('comp007', 'comp008', 'comp009', 'comp010');

-- 5. Crear cotizaciones de prueba
INSERT INTO cotizacion (fecha, total) VALUES
('2023-01-15', 0), -- El total se actualizará con los detalles
('2023-02-20', 0),
('2023-03-10', 0);

-- 6. Agregar detalles a las cotizaciones (mezclando componentes simples y compuestos)
INSERT INTO detalle_cotizacion (num, descripcion, cant, importe_cotizado, precio_base, cotizacion_id, componente_id, es_compuesto) VALUES
-- Cotización 1 (componentes simples)
(1, 'Disco SSD 500GB', 2, 399.98, 199.99, 1, 'comp001', FALSE),
(2, 'Monitor 24" FHD', 1, 249.99, 249.99, 1, 'comp003', FALSE),
-- Cotización 2 (mezcla)
(1, 'PC Gamer Completa', 1, 1579.96, 1579.96, 2, 'comp008', TRUE),
(2, 'Tarjeta Video extra', 1, 599.99, 599.99, 2, 'comp005', FALSE),
-- Cotización 3 (solo PCs)
(1, 'PC Workstation', 2, 2599.96, 1299.98, 3, 'comp009', TRUE),
(2, 'PC All-in-One', 1, 899.98, 899.98, 3, 'comp010', TRUE);

-- 7. Actualizar totales de cotizaciones
UPDATE cotizacion c
SET c.total = (
    SELECT SUM(importe_cotizado)
    FROM detalle_cotizacion dc
    WHERE dc.cotizacion_id = c.id
)
WHERE c.id IN (1, 2, 3);

-- 8. Datos para cotizador (opcional)
INSERT INTO cotizador (id) VALUES (1), (2), (3);

INSERT INTO cotizador_cantidades (cotizador_id, cantidad) VALUES
(1, 5), (1, 3), (2, 2), (3, 1);