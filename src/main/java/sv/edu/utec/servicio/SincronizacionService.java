package sv.edu.utec.servicio;

import sv.edu.utec.api.ProveedorAPI;
import sv.edu.utec.datos.ProductoDAO;
import sv.edu.utec.modelo.Producto;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SincronizacionService {

    private final ProveedorAPI proveedorAPI;
    private final ProductoDAO dao;

    public SincronizacionService(ProveedorAPI proveedorAPI, ProductoDAO dao) {
        this.proveedorAPI = proveedorAPI;
        this.dao = dao;
    }

    public int[] sincronizar(int limite)
            throws IOException, InterruptedException, SQLException {

        List<Producto> productos =
                proveedorAPI.obtenerProductos(limite);

        int insertados = 0;
        int actualizados = 0;

        for (Producto producto : productos) {

            if (dao.existe(producto.getId())) {
                if (dao.actualizar(producto)) {
                    actualizados++;
                }
            } else {
                dao.insertar(producto);
                insertados++;
            }
        }

        return new int[]{insertados, actualizados};
    }
}