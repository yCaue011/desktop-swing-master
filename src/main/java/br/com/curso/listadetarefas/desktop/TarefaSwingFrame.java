package br.com.curso.listadetarefas.desktop;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface Gráfica Principal do Cliente Desktop desenvolvida em Java Swing puro.
 * Conecta-se à API REST Spring Boot na porta 8080 e sincroniza operações CRUD.
 */
public class TarefaSwingFrame extends JFrame {

    private final TarefaApiService apiService;
    private final List<Tarefa> tarefasList = new ArrayList<>();

    // Componentes de UI
    private JTextField txtNovaTarefa;
    private JButton btnAdicionar;
    private JTable tabelaTarefas;
    private DefaultTableModel tableModel;
    private JLabel lblStatusConexao;
    private JButton btnAlternar;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnAtualizar;

    public TarefaSwingFrame() {
        this.apiService = new TarefaApiService();
        initComponents();
        carregarTarefasAssincrono();
    }

    private void initComponents() {
        setTitle("Lista de Tarefas — Cliente Desktop Swing");
        setSize(750, 520);
        setMinimumSize(new Dimension(650, 420));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 1. Cabeçalho (Topo)
        JPanel painelTopo = new JPanel(new BorderLayout(5, 5));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(12, 16, 5, 16));

        JLabel lblTitulo = new JLabel("📝 Minhas Tarefas (Desktop Swing)");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(25, 42, 86));

        lblStatusConexao = new JLabel("🔄 Conectando à API (http://localhost:8080)...");
        lblStatusConexao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatusConexao.setForeground(Color.GRAY);

        painelTopo.add(lblTitulo, BorderLayout.NORTH);
        painelTopo.add(lblStatusConexao, BorderLayout.SOUTH);

        // 2. Formulário de Cadastro (Acima da Tabela)
        JPanel painelForm = new JPanel(new BorderLayout(8, 8));
        painelForm.setBorder(BorderFactory.createEmptyBorder(5, 16, 10, 16));

        txtNovaTarefa = new JTextField();
        txtNovaTarefa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNovaTarefa.setToolTipText("Digite o que você precisa fazer e pressione Enter...");

        btnAdicionar = new JButton("➕ Adicionar");
        btnAdicionar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdicionar.setBackground(new Color(46, 204, 113));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.setFocusPainted(false);
        btnAdicionar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        painelForm.add(new JLabel("Nova Tarefa:"), BorderLayout.WEST);
        painelForm.add(txtNovaTarefa, BorderLayout.CENTER);
        painelForm.add(btnAdicionar, BorderLayout.EAST);

        JPanel containerNorte = new JPanel(new BorderLayout());
        containerNorte.add(painelTopo, BorderLayout.NORTH);
        containerNorte.add(painelForm, BorderLayout.SOUTH);
        add(containerNorte, BorderLayout.NORTH);

        // 3. Tabela Central com Lista de Tarefas
        String[] colunas = {"ID", "Descrição da Tarefa", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaTarefas = new JTable(tableModel);
        tabelaTarefas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelaTarefas.setRowHeight(28);
        tabelaTarefas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaTarefas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabelaTarefas.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabelaTarefas.getColumnModel().getColumn(0).setMaxWidth(80);
        tabelaTarefas.getColumnModel().getColumn(1).setPreferredWidth(450);
        tabelaTarefas.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabelaTarefas.getColumnModel().getColumn(2).setMaxWidth(140);

        // Renderizador customizado para status
        tabelaTarefas.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                String status = value != null ? value.toString() : "";
                if ("Concluída".equals(status)) {
                    setForeground(new Color(39, 174, 96));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(new Color(230, 126, 34));
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaTarefas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        add(scrollPane, BorderLayout.CENTER);

        // 4. Rodapé com Botões de Ação
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        painelRodape.setBorder(BorderFactory.createEmptyBorder(5, 16, 10, 16));

        btnAlternar = new JButton("✔️ Concluir / Reabrir");
        btnAlternar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnAlternar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEditar = new JButton("✏️ Editar");
        btnEditar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnExcluir = new JButton("🗑️ Excluir");
        btnExcluir.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnExcluir.setForeground(new Color(192, 57, 43));
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAtualizar = new JButton("🔄 Atualizar");
        btnAtualizar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        painelRodape.add(btnAlternar);
        painelRodape.add(btnEditar);
        painelRodape.add(btnExcluir);
        painelRodape.add(btnAtualizar);
        add(painelRodape, BorderLayout.SOUTH);

        // 5. Registro de Eventos
        btnAdicionar.addActionListener(e -> acaoAdicionar());
        txtNovaTarefa.addActionListener(e -> acaoAdicionar());
        btnAlternar.addActionListener(e -> acaoAlternarStatus());
        btnEditar.addActionListener(e -> acaoEditar());
        btnExcluir.addActionListener(e -> acaoExcluir());
        btnAtualizar.addActionListener(e -> carregarTarefasAssincrono());

        tabelaTarefas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    acaoEditar();
                }
            }
        });
    }

    public void carregarTarefasAssincrono() {
        lblStatusConexao.setText("🔄 Sincronizando com a API...");
        lblStatusConexao.setForeground(Color.BLUE);

        CompletableFuture.supplyAsync(() -> {
            try {
                return apiService.listarTarefas();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).thenAccept(tarefas -> SwingUtilities.invokeLater(() -> {
            tarefasList.clear();
            tarefasList.addAll(tarefas);
            tableModel.setRowCount(0);
            for (Tarefa t : tarefas) {
                tableModel.addRow(new Object[]{t.getId(), t.getDescricao(), t.isConcluida() ? "Concluída" : "Pendente"});
            }
            lblStatusConexao.setText("🟢 Conectado à API (http://localhost:8080) — " + tarefas.size() + " tarefas sincronizadas");
            lblStatusConexao.setForeground(new Color(39, 174, 96));
        })).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                lblStatusConexao.setText("🔴 Falha ao conectar na API Spring Boot (Porta 8080)");
                lblStatusConexao.setForeground(new Color(192, 57, 43));
            });
            return null;
        });
    }

    private void acaoAdicionar() {
        String descricao = txtNovaTarefa.getText().trim();
        if (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, digite a descrição da tarefa!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnAdicionar.setEnabled(false);
        CompletableFuture.runAsync(() -> {
            try {
                apiService.criarTarefa(descricao);
                SwingUtilities.invokeLater(() -> {
                    txtNovaTarefa.setText("");
                    carregarTarefasAssincrono();
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Erro ao criar tarefa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE));
            } finally {
                SwingUtilities.invokeLater(() -> btnAdicionar.setEnabled(true));
            }
        });
    }

    private void acaoAlternarStatus() {
        int selectedRow = tabelaTarefas.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na tabela para alterar o status!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Tarefa tarefa = tarefasList.get(selectedRow);
        CompletableFuture.runAsync(() -> {
            try {
                apiService.alternarStatus(tarefa);
                SwingUtilities.invokeLater(this::carregarTarefasAssincrono);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Erro ao alterar status: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE));
            }
        });
    }

    private void acaoEditar() {
        int selectedRow = tabelaTarefas.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na tabela para editar!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Tarefa tarefa = tarefasList.get(selectedRow);
        String novaDescricao = JOptionPane.showInputDialog(this, "Editar descrição da tarefa #" + tarefa.getId() + ":", tarefa.getDescricao());

        if (novaDescricao != null && !novaDescricao.trim().isEmpty()) {
            CompletableFuture.runAsync(() -> {
                try {
                    apiService.atualizarDescricao(tarefa.getId(), novaDescricao.trim(), tarefa.isConcluida());
                    SwingUtilities.invokeLater(this::carregarTarefasAssincrono);
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Erro ao editar tarefa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE));
                }
            });
        }
    }

    private void acaoExcluir() {
        int selectedRow = tabelaTarefas.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na tabela para excluir!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Tarefa tarefa = tarefasList.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir a tarefa #" + tarefa.getId() + " (" + tarefa.getDescricao() + ")?", "Confirmação", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            CompletableFuture.runAsync(() -> {
                try {
                    apiService.excluirTarefa(tarefa.getId());
                    SwingUtilities.invokeLater(this::carregarTarefasAssincrono);
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Erro ao excluir tarefa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE));
                }
            });
        }
    }
}
