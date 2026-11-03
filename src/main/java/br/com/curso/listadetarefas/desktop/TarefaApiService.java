package br.com.curso.listadetarefas.desktop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Camada de serviço de rede responsável por consumir a API REST Spring Boot.
 * Utiliza o java.net.http.HttpClient nativo do Java 11+ e Jackson para JSON.
 */
public class TarefaApiService {

    private final String apiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TarefaApiService() {
        this("http://localhost:8080/api/tarefas");
    }

    public TarefaApiService(String apiUrl) {
        this.apiUrl = apiUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // RF-D01: Listar todas as tarefas cadastradas (GET /api/tarefas)
    public List<Tarefa> listarTarefas() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<Tarefa>>() {});
        } else {
            throw new IOException("Falha ao listar tarefas. Código HTTP: " + response.statusCode());
        }
    }

    // RF-D02: Cadastrar uma nova tarefa (POST /api/tarefas)
    public Tarefa criarTarefa(String descricao) throws IOException, InterruptedException {
        Tarefa nova = new Tarefa(descricao);
        String jsonBody = objectMapper.writeValueAsString(nova);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Tarefa.class);
        } else {
            throw new IOException("Falha ao criar tarefa. Código HTTP: " + response.statusCode());
        }
    }

    // RF-D03: Alternar status de conclusão (PUT /api/tarefas/{id})
    public Tarefa alternarStatus(Tarefa tarefa) throws IOException, InterruptedException {
        tarefa.setConcluida(!tarefa.isConcluida());
        String jsonBody = objectMapper.writeValueAsString(tarefa);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/" + tarefa.getId()))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(5))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Tarefa.class);
        } else {
            throw new IOException("Falha ao alternar status da tarefa #" + tarefa.getId() + ". Código: " + response.statusCode());
        }
    }

    // RF-D04: Atualizar a descrição da tarefa (PUT /api/tarefas/{id})
    public Tarefa atualizarDescricao(Long id, String novaDescricao, boolean concluida) throws IOException, InterruptedException {
        Tarefa atualizada = new Tarefa(id, novaDescricao, concluida);
        String jsonBody = objectMapper.writeValueAsString(atualizada);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/" + id))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(5))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Tarefa.class);
        } else {
            throw new IOException("Falha ao atualizar tarefa #" + id + ". Código: " + response.statusCode());
        }
    }

    // RF-D05: Excluir uma tarefa por ID (DELETE /api/tarefas/{id})
    public void excluirTarefa(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/" + id))
                .timeout(Duration.ofSeconds(5))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw new IOException("Falha ao excluir tarefa #" + id + ". Código HTTP: " + response.statusCode());
        }
    }
}
