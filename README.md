# Sistema de Gestión para Gimnasios - Proyecto Seminario S21

Este repositorio contiene el proyecto completo desarrollado como Trabajo Práctico de la materia **Seminario de Práctica**, orientado a la creación de un **Sistema de Gestión Integral para Gimnasios**.

El sistema permite administrar clientes, actividades, pagos y rutinas, optimizando los procesos internos y mejorando la experiencia del usuario.

## Autor
**Jazmín Priscila Bustos**
DNI 43.644.281

## Contenido del repositorio

- `gimnasiodb.sql` : Script SQL para crear las tablas y relaciones de la base de datos.
- `SistemaGestionGimnasio/` : Carpeta con el proyecto completo desarrollado en **Java** (IntelliJ IDEA), incluyendo:
  - Código fuente Java
  - Recursos e interfaces gráficas
  - Paquetes de análisis y diseño
  - Documentación interna del proyecto

## Funcionalidades principales

1. **Gestión de clientes**  
   - Registro, modificación y consulta de datos personales.  
   - Seguimiento de pagos, actividades y rutinas asignadas.  

2. **Control de pagos y cuotas**  
   - Registro automático y manual de pagos.  
   - Consulta de historial y estado de cuotas.  
   - Generación de reportes financieros.  

3. **Gestión de actividades**  
   - Registro y modificación de clases y planes de entrenamiento.  
   - Asignación de actividades a los clientes.  

4. **Módulo de rutinas**  
   - Creación, asignación y actualización de rutinas.  
   - Envío automático de rutinas a los clientes vía WhatsApp o correo electrónico.  
   - Registro del historial de rutinas para trazabilidad.  

5. **Reportes y exportación**  
   - Generación de reportes de ingresos mensuales.  
   - Exportación a PDF y/o Excel.  

6. **Seguridad y roles**  
   - Control de acceso mediante usuarios y contraseñas.  
   - Diferenciación de roles (Administrador, Instructor, Cliente).

## Tecnologías utilizadas

- **Lenguaje de programación:** Java  
- **Base de datos:** MySQL  
- **Conexión a la base de datos:** JDBC  
- **Interfaz de usuario:** JavaFX  
- **Arquitectura:** Cliente-servidor en 3 capas (Presentación, Negocio, Datos)  
- **IDE recomendado:** IntelliJ IDEA

## Instalación y uso

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/tu_usuario/SistemaGestionGimnasio.git
