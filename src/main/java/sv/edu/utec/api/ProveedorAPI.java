package sv.edu.utec.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import sv.edu.utec.modelo.Producto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ProveedorAPI {

    private final HttpClient cliente;
    private final ObjectMapper objectMapper;

    public ProveedorAPI() {
        this.cliente = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<Producto> obtenerProductos(int limite)
            throws IOException, InterruptedException {

        String url = "https://dummyjson.com/products?limit="
                + limite
                + "&select=title,stock";

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> respuesta = cliente.send(
                solicitud,
                HttpResponse.BodyHandlers.ofString()
        );

        int codigo = respuesta.statusCode();

        if (codigo != 200) {
            throw new IOException(
                    "Error HTTP al consultar el proveedor. Código: " + codigo
            );
        }

        RespuestaProductos respuestaProductos =
                objectMapper.readValue(
                        respuesta.body(),
                        RespuestaProductos.class
                );

        List<Producto> productos = new ArrayList<>();

        for (ProductoApi productoApi : respuestaProductos.getProducts()) {
            productos.add(productoApi.aProducto());
        }

        return productos;
    }
}