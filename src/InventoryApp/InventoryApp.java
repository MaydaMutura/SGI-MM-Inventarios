/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package InventoryApp;

/**
 *
 * @author mayda
 */
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// ====================================================================
// CONFIGURACIÓN DE LA BASE DE DATOS (MySQL) Y ESQUEMA SQL
// ====================================================================

/**
 * Clase de utilidad para manejar la configuración de la conexión a MySQL.
 * En un proyecto real, estas credenciales deberían estar en un archivo de configuración seguro.
 */
class DBConfig {
    // Constantes de conexión a MySQL
    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/db_inventario_mm";
    public static final String USER = "root";
    public static final String PASSWORD = "Seminario2025.";

    /**
     * Intenta establecer la conexión a la base de datos.
     * @return Objeto Connection, o null si falla.
     */
    public static Connection getConnection() {
        try {
            // Cargar el driver JDBC (asegúrate de incluir el JAR en tu proyecto NetBeans)
            // Class.forName("com.mysql.cj.jdbc.Driver"); // No necesario en versiones modernas de JDBC
            return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (SQLException e) {
            // En un entorno real, manejar el error con un diálogo de alerta
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Esquema SQL (Modelo Entidad-Relación)
     * Este código es solo informativo y debe ejecutarse una vez en MySQL Workbench o similar.
     */
    public static final String SQL_SCHEMA = 
        "-- Tabla Producto\n" +
        "CREATE TABLE Producto (\n" +
        "    idProducto INT PRIMARY KEY AUTO_INCREMENT,\n" +
        "    sku VARCHAR(30) UNIQUE NOT NULL, -- Código único de artículo\n" +
        "    nombre VARCHAR(80) NOT NULL,\n" +
        "    categoria VARCHAR(40),\n" +
        "    umbralStock INT DEFAULT 10, -- Mínimo para alerta\n" +
        "    estado ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO'\n" +
        ");\n\n" +
        "-- Tabla Lote (Clave para gestión FEFO)\n" +
        "CREATE TABLE Lote (\n" +
        "    idLote INT PRIMARY KEY AUTO_INCREMENT,\n" +
        "    idProducto INT, \n" +
        "    fechaVencimiento DATE NOT NULL, \n" +
        "    cantidad INT NOT NULL, -- Cantidad actual en stock\n" +
        "    codigo VARCHAR(30),\n" +
        "    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)\n" +
        ");\n\n" +
        "-- ... Otras tablas (Usuario, Movimiento, Proveedor)";
}

// ====================================================================
// CAPA DEL MODELO (Lógica de Negocio y Entidades)
// ====================================================================

/**
 * Entidad Lote. Implementa el atributo clave para el control FEFO.
 */
class Lote {
    private int idLote;
    private String nombreProducto; // Simplificación para la vista
    private LocalDate fechaVencimiento;
    private int cantidad;

    public Lote(int idLote, String nombreProducto, LocalDate fechaVencimiento, int cantidad) {
        this.idLote = idLote;
        this.nombreProducto = nombreProducto;
        this.fechaVencimiento = fechaVencimiento;
        this.cantidad = cantidad;
    }

    // Getters
    public int getIdLote() { return idLote; }
    public String getNombreProducto() { return nombreProducto; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public int getCantidad() { return cantidad; }
}

/**
 * Data Access Object (DAO) - Simula la capa de persistencia y la lógica de negocio clave.
 */
class InventarioDAO {
    // ==========================================================
    // LÓGICA DE ALERTA (REQUERIMIENTO CLAVE)
    // ==========================================================
    /**
     * Simula la obtención de lotes próximos a vencer o con stock bajo.
     * @param diasUmbral Días límite para considerar 'próximo a vencer'.
     * @return Lista filtrada de alertas.
     */
    public ObservableList<Lote> getAlertasVencimiento(int diasUmbral) {
        // En un proyecto real: Ejecutar una consulta SQL
        /*
        String sql = "SELECT * FROM Lote l JOIN Producto p ON l.idProducto = p.idProducto " +
                     "WHERE l.fechaVencimiento <= DATE_ADD(CURDATE(), INTERVAL ? DAY) AND l.cantidad > 0";
        */
        
        // Datos de simulación
        ObservableList<Lote> allLotes = getLotesSimulados();

        LocalDate fechaUmbral = LocalDate.now().plusDays(diasUmbral);

        return allLotes.stream()
                .filter(lote -> lote.getCantidad() > 0)
                .filter(lote -> lote.getFechaVencimiento().isBefore(fechaUmbral) || lote.getFechaVencimiento().isEqual(fechaUmbral))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    // ==========================================================
    // LÓGICA DE INVENTARIO (CRITERIO FEFO)
    // ==========================================================
    /**
     * Simula la obtención de lotes de un producto, ordenados por FEFO.
     * FEFO = First Expired, First Out (Primero en Vencer, Primero en Salir).
     * @return Lista de lotes ordenados por fecha de vencimiento ascendente.
     */
    public ObservableList<Lote> getLotesOrdenadosPorFEFO() {
        // En un proyecto real: Ejecutar una consulta SQL
        /*
        String sql = "SELECT * FROM Lote ORDER BY fechaVencimiento ASC";
        */

        // Datos de simulación
        ObservableList<Lote> allLotes = getLotesSimulados();

        allLotes.sort(Comparator.comparing(Lote::getFechaVencimiento)
                                .thenComparing(Lote::getIdLote)); // Desempate por ID

        return allLotes;
    }
    
    // ==========================================================
    // MOCK DATA (Simulación de datos para la UI)
    // ==========================================================
    private ObservableList<Lote> getLotesSimulados() {
        return FXCollections.observableArrayList(
                new Lote(101, "Leche Entera", LocalDate.now().plusDays(5), 50),
                new Lote(102, "Jamón Cocido", LocalDate.now().plusDays(15), 120),
                new Lote(103, "Yogur Frutilla", LocalDate.now().plusDays(2), 30), // Vencimiento más próximo
                new Lote(104, "Queso Cremoso", LocalDate.now().plusDays(60), 200),
                new Lote(105, "Leche Entera", LocalDate.now().plusDays(20), 80) // Otro lote de Leche
        );
    }
}

// ====================================================================
// CAPA DE PRESENTACIÓN (JavaFX Application - Simula el VISTA y el CONTROLADOR)
// ====================================================================

/**
 * Clase principal de la aplicación JavaFX.
 * Muestra el diseño de la interfaz y la integración con la lógica de negocio.
 */
public class InventoryApp extends Application {

    private Stage primaryStage;
    private final InventarioDAO dao = new InventarioDAO();
    private String userRole = "ADMINISTRADOR"; // Simulación de rol

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("SGI M&M - Sistema de Gestión de Inventarios");
        showLoginScene();
    }

    /**
     * Muestra la pantalla de login.
     */
    private void showLoginScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Label title = new Label("SGI M&M - Iniciar Sesión");
        title.setStyle("-fx-font-size: 18pt; -fx-font-weight: bold;");
        grid.add(title, 0, 0, 2, 1);

        Label userLabel = new Label("Usuario:");
        grid.add(userLabel, 0, 1);

        TextField userTextField = new TextField("admin");
        grid.add(userTextField, 1, 1);

        Label pwLabel = new Label("Contraseña:");
        grid.add(pwLabel, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Acceder");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        Label message = new Label();
        grid.add(message, 1, 6);

        // Lógica de autenticación simplificada
        btn.setOnAction(e -> {
            if ("admin".equals(userTextField.getText()) && "123".equals(pwBox.getText())) {
                userRole = "ADMINISTRADOR";
                showMainScene();
            } else if ("operaria".equals(userTextField.getText()) && "123".equals(pwBox.getText())) {
                 userRole = "OPERARIA";
                 showMainScene();
            } else {
                message.setText("Usuario o Contraseña incorrecta.");
            }
        });

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Muestra la pantalla principal con las pestañas de navegación (MVC-VISTA).
     */
    private void showMainScene() {
        BorderPane root = new BorderPane();
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Pestañas de la aplicación
        Tab productTab = new Tab("Productos y Proveedores", createProductView());
        Tab inventoryTab = new Tab("Inventario (FEFO)", createInventoryView());
        Tab alertsTab = new Tab("Alertas Críticas", createAlertsView());
        Tab reportsTab = new Tab("Reportes y Estadísticas", createReportsView());
        
        tabPane.getTabs().addAll(productTab, inventoryTab, alertsTab, reportsTab);
        root.setCenter(tabPane);
        
        // Mostrar rol del usuario en la parte inferior
        Label footer = new Label("Usuario: " + userRole + " | Permisos: " + userRole);
        footer.setPadding(new Insets(10));
        root.setBottom(footer);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }
    
    // ==========================================================
    // VISTAS Y CONTROLADORES SIMPLIFICADOS
    // ==========================================================

    /**
     * Vista de Productos y Proveedores (Operaciones: C R U D / Alta, Baja, Modificación)
     */
    private VBox createProductView() {
        Label header = new Label("Gestión de Productos y Proveedores");
        header.setStyle("-fx-font-size: 16pt;");
        
        Label accessInfo = new Label(
            "Funcionalidad CRUD: Registro, modificación y baja lógica de productos. " +
            "La gestión de proveedores se enlaza a la planificación de compras. " +
            (userRole.equals("OPERARIA") ? "\nADVERTENCIA: Su rol de OPERARIA solo permite la consulta." : "")
        );
        accessInfo.setWrapText(true);
        
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.getChildren().addAll(header, accessInfo);

        // Si es Operario, limitar acceso
        if (!userRole.equals("ADMINISTRADOR")) {
            Button addBtn = new Button("Registrar Nuevo Producto (ACCESO DENEGADO)");
            addBtn.setDisable(true);
            view.getChildren().add(addBtn);
        } else {
            Button addBtn = new Button("Registrar Nuevo Producto");
            addBtn.setOnAction(e -> System.out.println("Acción: Abrir modal de registro de producto"));
            view.getChildren().add(addBtn);
        }

        return view;
    }

    /**
     * Vista de Inventario, enfocada en la lógica FEFO.
     */
    private VBox createInventoryView() {
        Label header = new Label("Control de Inventario y Movimientos de Lotes (Criterio FEFO)");
        header.setStyle("-fx-font-size: 16pt;");
        
        // 1. Mostrar la tabla de Lotes ordenados por FEFO
        TableView<Lote> tableView = new TableView<>();
        TableColumn<Lote, String> productCol = new TableColumn<>("Producto");
        productCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNombreProducto()));
        
        TableColumn<Lote, Integer> lotCol = new TableColumn<>("ID Lote");
        lotCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getIdLote()));

        TableColumn<Lote, LocalDate> expiryCol = new TableColumn<>("Fecha Vencimiento");
        expiryCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getFechaVencimiento()));
        
        TableColumn<Lote, Integer> quantityCol = new TableColumn<>("Stock (Cant.)");
        quantityCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCantidad()));
        
        tableView.getColumns().addAll(lotCol, productCol, expiryCol, quantityCol);
        
        // Aplicación del Criterio FEFO (Lógica de Negocio)
        tableView.setItems(dao.getLotesOrdenadosPorFEFO());
        tableView.setPlaceholder(new Label("No hay lotes en el inventario."));
        
        Label fefoInfo = new Label("La tabla ordena automáticamente los lotes por la fecha de vencimiento más próxima (FEFO), garantizando que las salidas de stock prioricen el lote superior de la lista.");
        fefoInfo.setWrapText(true);
        fefoInfo.setStyle("-fx-font-style: italic;");
        
        VBox view = new VBox(10);
        view.setPadding(new Insets(20));
        view.getChildren().addAll(header, fefoInfo, tableView);
        return view;
    }
    
    /**
     * Vista de Alertas, mostrando la gestión de vencimientos.
     */
    private VBox createAlertsView() {
        Label header = new Label("Alertas Críticas de Inventario (Vencimiento y Stock Mínimo)");
        header.setStyle("-fx-font-size: 16pt; -fx-text-fill: red;");
        
        // Control para definir el umbral de días
        Spinner<Integer> daysSpinner = new Spinner<>(1, 60, 15);
        Label daysLabel = new Label("Alertar si vence en (días):");
        HBox controls = new HBox(10, daysLabel, daysSpinner);
        controls.setAlignment(Pos.CENTER_LEFT);
        
        TableView<Lote> tableView = new TableView<>();
        TableColumn<Lote, String> productCol = new TableColumn<>("Producto");
        productCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNombreProducto()));
        
        TableColumn<Lote, LocalDate> expiryCol = new TableColumn<>("Vencimiento");
        expiryCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getFechaVencimiento()));
        
        TableColumn<Lote, Integer> quantityCol = new TableColumn<>("Stock");
        quantityCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCantidad()));
        
        tableView.getColumns().addAll(productCol, expiryCol, quantityCol);
        
        // Lógica de alerta inicial
        tableView.setItems(dao.getAlertasVencimiento(daysSpinner.getValue()));
        
        // Actualizar alertas al cambiar el umbral
        daysSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            tableView.setItems(dao.getAlertasVencimiento(newValue));
        });
        
        Label alertInfo = new Label("Esta funcionalidad verifica los lotes cuya fecha de vencimiento está dentro del umbral definido, ayudando a M&M a evitar pérdidas por mercadería caducada.");
        alertInfo.setWrapText(true);
        
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        view.getChildren().addAll(header, controls, alertInfo, tableView);
        return view;
    }
    
    /**
     * Vista de Reportes.
     */
    private VBox createReportsView() {
        Label header = new Label("Reportes e Indicadores para la Toma de Decisiones");
        header.setStyle("-fx-font-size: 16pt;");
        
        ListView<String> reportList = new ListView<>(FXCollections.observableArrayList(
            "1. Reporte de Lotes Próximos a Vencer (Basado en alerta)",
            "2. Reporte Histórico de Movimientos",
            "3. Reporte de Stock Mínimo (Necesidad de compra)",
            "4. KPi: Tasa de Rotación de Inventario"
        ));
        
        Label reportInfo = new Label("Los reportes permiten a la gerencia evaluar la rentabilidad y la eficiencia de la gestión de stock.");
        reportInfo.setWrapText(true);
        
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        view.getChildren().addAll(header, reportInfo, reportList);
        return view;
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    // CLASE AUXILIAR NECESARIA PARA LAS TABLEVIEWS, SI NO SE USA FXML
    // Se incluye para que el código compile y sea funcional.
    public static class ReadOnlyObjectWrapper<T> extends javafx.beans.property.ReadOnlyObjectWrapper<T> {
        public ReadOnlyObjectWrapper(T initialValue) {
            super(initialValue);
        }
        public ReadOnlyObjectWrapper() {
            super();
        }
        // Necesario para que la tabla pueda obtener el valor
        public final T getValue() {
            return get();
        }
    }
    
    public static class ReadOnlyStringWrapper extends javafx.beans.property.ReadOnlyStringWrapper {
        public ReadOnlyStringWrapper(String initialValue) {
            super(initialValue);
        }
        public ReadOnlyStringWrapper() {
            super();
        }
        // Necesario para que la tabla pueda obtener el valor
        public final String getValue() {
            return get();
        }
    }
}
