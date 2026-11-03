package br.com.curso.listadetarefas.desktop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Modelo de dados que representa uma Tarefa no cliente Desktop Swing.
 * Compatível com o payload JSON da API REST Spring Boot (/api/tarefas).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tarefa {

    private Long id;
    private String descricao;
    private boolean concluida;

    public Tarefa() {
    }

    public Tarefa(Long id, String descricao, boolean concluida) {
        this.id = id;
        this.descricao = descricao;
        this.concluida = concluida;
    }

    public Tarefa(String descricao) {
        this(null, descricao, false);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    @Override
    public String toString() {
        return String.format("[%s] #%d - %s", (concluida ? "X" : " "), id != null ? id : 0, descricao);
    }
}
