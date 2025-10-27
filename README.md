**📦 SGI M&M - Sistema de Gestión y Optimización de Inventarios**

✨ Descripción del Proyecto

Este proyecto consiste en un Sistema de Gestión de Inventarios (SGI) desarrollado para la PyME M&M Distribución de Alimentos. El objetivo principal es automatizar el control de stock de productos perecederos (lácteos y fiambres) y minimizar las pérdidas por vencimiento mediante la aplicación del criterio FEFO (First-Expired, First-Out) y alertas automáticas.

El sistema fue desarrollado bajo la arquitectura MVC (Modelo-Vista-Controlador) utilizando JavaFX para la interfaz de escritorio y MySQL para la persistencia de datos.

🛠️ Tecnologías Utilizadas

➡️Lenguaje: Java 11+

➡️Framework de Interfaz: JavaFX

➡️Gestor de Base de Datos: MySQL

➡️Conectividad: JDBC (Java Database Connectivity)

➡️Entorno de Desarrollo: Apache NetBeans

🚀 Puesta en Marcha del Proyecto

Sigue estos pasos para configurar y ejecutar la aplicación localmente.

1. 💾 Configuración de la Base de Datos

El sistema se conecta a una base de datos MySQL llamada gestion_inventario.

Crear la Base de Datos: Crea una nueva base de datos llamada gestion_inventario en tu servidor MySQL.

------------------------------------------
CREATE DATABASE gestion_inventario;
USE gestion_inventario;
-----------------------------------------

Cargar el Esquema: Ejecuta el script SQL proporcionado (schema.sql) para crear las tablas (Producto, Lote, Movimiento, Usuario, etc.) con sus respectivas claves primarias y foráneas.

2. 💻 Configuración del Proyecto Java (NetBeans)

Clonar el Repositorio: Clona este repositorio a tu máquina local.

Abrir en NetBeans: Abre la carpeta clonada como un proyecto existente en NetBeans.

Añadir Librería JDBC: Descarga el MySQL Connector/J (el archivo .jar) y añádelo a las librerías del proyecto.

Actualizar Credenciales: Edita la clase DBConfig en InventoryApp.java con tus credenciales de MySQL:

public static final String JDBC_URL = "jdbc:mysql://localhost:3306/gestion_inventario";
public static final String USER = "tu_usuario_mysql";       // <--
public static final String PASSWORD = "tu_contraseña_mysql"; // <-- 


3. ▶️ Ejecución

Ejecuta el archivo principal InventoryApp.java.

Login de Prueba (Mock): Usa admin/123 o operaria/123.

⚙️ Estructura del Modelo (Modelo Entidad-Relación)

El modelo de datos se basa en las siguientes entidades clave:

Producto: ID, SKU, Nombre, Umbral de Stock.

Lote: ID, FK Producto, Fecha de Vencimiento, Cantidad.

Movimiento: ID, FK Lote, FK Usuario, Cantidad, Tipo (Entrada/Salida/Ajuste), Fecha.

Usuario: ID, Username, Rol (ADMINISTRADOR/OPERARIA).
