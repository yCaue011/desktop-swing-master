package br.com.curso.listadetarefas.desktop;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada (Main) do Cliente Desktop Java Swing.
 * Configura o Look and Feel nativo do sistema operacional e inicializa a janela na Event Dispatch Thread (EDT).
 */
public class    MainSwingApp {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            TarefaSwingFrame frame = new TarefaSwingFrame();
            frame.setVisible(true);
        });
    }
}
