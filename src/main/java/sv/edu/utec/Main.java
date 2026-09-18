package sv.edu.utec;

import sv.edu.utec.api.ProveedorAPI;
import sv.edu.utec.datos.ProductoDAO;
import sv.edu.utec.modelo.Producto;
import sv.edu.utec.servicio.InventarioJsonService;
import sv.edu.utec.servicio.SincronizacionService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {

    private static final ProductoDAO dao = new ProductoDAO();
    private static final InventarioJsonService jsonService =
            new InventarioJsonService();

    private static final ProveedorAPI proveedorAPI =
            new ProveedorAPI();

    private static final SincronizacionService sincronizacionService =
            new SincronizacionService(proveedorAPI, dao);

    private static final String ARCHIVO = "inventario.json";

    public static void main(String[] args) {
        try {
            dao.crearTabla();
            System.out.println("Tabla producto lista.");

            sembrarDatos();

            System.out.println("\n--- Inventario inicial ---");
            imprimir(dao.listar());

            jsonService.exportar(ARCHIVO);
            System.out.println("\nRespaldo generado en " + ARCHIVO);

            if (dao.actualizar(
                    new Producto(2, "Monitor 24 pulgadas", 12))) {
                System.out.println("Producto 2 actualizado.");
            }

            if (dao.eliminar(1)) {
                System.out.println("Producto 1 eliminado.");
            }

            System.out.println("\n--- Despues de los cambios ---");
            imprimir(dao.listar());

            int restaurados = jsonService.importar(ARCHIVO);
            System.out.println(
                    "\nRegistros restaurados desde JSON: " + restaurados
            );

            System.out.println("\n--- Inventario final ---");
            imprimir(dao.listar());

            int[] resultado =
                    sincronizacionService.sincronizar(10);

            System.out.printf(
                    "\nSincronizacion con la API -> insertados: %d | actualizados: %d%n",
                    resultado[0],
                    resultado[1]
            );

            System.out.println("\n--- Inventario sincronizado ---");
            imprimir(dao.listar());

        } catch (SQLException e) {
            System.out.println(
                    "Error de base de datos: " + e.getMessage()
            );

        } catch (IOException e) {
            System.out.println(
                    "Error al leer o escribir el archivo JSON o consumir la API: "
                            + e.getMessage()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            System.out.println(
                    "La sincronizacion fue interrumpida: "
                            + e.getMessage()
            );
        }
    }

    private static void sembrarDatos() throws SQLException {
        if (!dao.existe(1)) {
            dao.insertar(
                    new Producto(1, "Teclado mecanico", 15)
            );
        }

        if (!dao.existe(2)) {
            dao.insertar(
                    new Producto(2, "Monitor 24 pulgadas", 8)
            );
        }
    }

    private static void imprimir(List<Producto> productos) {
        System.out.printf(
                "%-5s %-25s %10s%n",
                "ID",
                "PRODUCTO",
                "CANTIDAD"
        );

        for (Producto p : productos) {
            System.out.printf(
                    "%-5d %-25s %10d%n",
                    p.getId(),
                    p.getNombre(),
                    p.getCantidad()
            );
        }
    }
}