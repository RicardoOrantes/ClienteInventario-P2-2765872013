package sv.edu.utec.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<ProductoApi> obtenerProductos(int limite) {
        try {
            String url = "https://dummyjson.com/products?limit=" + limite;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(
                        "Error HTTP: " + response.statusCode()
                );
            }

            RespuestaProductos respuesta =
                    objectMapper.readValue(
                            response.body(),
                            RespuestaProductos.class
                    );

            return respuesta.getProducts();

        } catch (Exception e) {
            throw new RuntimeException("Error al consumir la API", e);
        }
    }
}
