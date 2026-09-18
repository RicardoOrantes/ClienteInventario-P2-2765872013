package sv.edu.utec.servicio;

import sv.edu.utec.api.ApiClient;
import sv.edu.utec.api.ProductoApi;
import sv.edu.utec.datos.ProductoDAO;
import sv.edu.utec.modelo.Producto;

import java.sql.SQLException;
import java.util.List;

public class SincronizacionService {

    private final ApiClient apiClient;
    private final ProductoDAO productoDAO;

    public SincronizacionService() {
        this.apiClient = new ApiClient();
        this.productoDAO = new ProductoDAO();
    }

    public int importarProductosDesdeApi(int limite) throws SQLException {

        List<ProductoApi> productosApi =
                apiClient.obtenerProductos(limite);

        int importados = 0;

        productoDAO.crearTabla();

        for (ProductoApi productoApi : productosApi) {

            Producto producto = productoApi.aProducto();

            if (!productoDAO.existe(producto.getId())) {
                productoDAO.insertar(producto);
                importados++;
            }
        }

        return importados;
    }
}