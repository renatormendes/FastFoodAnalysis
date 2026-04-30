/**
 * PROJETO: Sistema de Análise de Vendas Fast-Food
 * AUTOR: [Seu Nome Aqui]
 * DATA: 2024
 * 
 * TECNOLOGIAS UTILIZADAS:
 * - Java Swing (Interface Gráfica)
 * - SQLite JDBC (Persistência SQL)
 * - Java IO / Serialization (Cache Binário para Performance)
 * - Java Streams API (Processamento de Dados e Relatórios)
 * - Linux Lite 7.8 (Ambiente de Desenvolvimento)
 * 
 * FUNCIONALIDADES:
 * - CRUD completo com validação de entrada de dados.
 * - Sincronização automática entre cache binário (.bin) e banco (.db).
 * - Relatórios estatísticos com opção de impressão física ou PDF.
 * - Exportação de dados para formato CSV.
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.text.MessageFormat;


// Modelo de Dados com Suporte a Serialização
class Venda implements Serializable {
    private static final long serialVersionUID = 1L;
    int id;
    String produto;
    String categoria;
    double valor;

    Venda(int id, String p, String c, double v) {
        this.id = id; this.produto = p; this.categoria = c; this.valor = v;
    }
}

public class MainUI extends JFrame {
    private List<Venda> cacheVendas = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable table;
    private final String DB_URL = "jdbc:sqlite:vendas.db";
    private final String BIN_FILE = "cache_vendas.bin";

    public MainUI() {
        setTitle("Fast-Food Analytics - Portfólio Java");
        setSize(900, 600);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Listener de segurança para o botão fechar (X)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSaida();
            }
        });

        initDB();
        loadFromBinOrDB();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Produto", "Categoria", "Valor (R$)"}, 0);
        table = new JTable(tableModel);
        
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAdd = new JButton("Inserir");
        JButton btnEdit = new JButton("Alterar");
        JButton btnDel = new JButton("Excluir");
        JButton btnReport = new JButton("Relatórios");
        JButton btnCSV = new JButton("Exportar CSV");
        JButton btnExit = new JButton("Sair e Salvar");

        pnlButtons.add(btnAdd); pnlButtons.add(btnEdit); 
        pnlButtons.add(btnDel); pnlButtons.add(btnReport);
        pnlButtons.add(btnCSV); pnlButtons.add(btnExit);
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

        // Ações do CRUD
        btnAdd.addActionListener(e -> {
            try {
                String p = JOptionPane.showInputDialog(this, "Nome do Produto:");
                if (p == null || p.trim().isEmpty()) return;
                String c = JOptionPane.showInputDialog(this, "Categoria:");
                if (c == null || c.trim().isEmpty()) return;
                String vStr = JOptionPane.showInputDialog(this, "Valor (ex: 29.90):");
                if (vStr == null) return;
                double v = Double.parseDouble(vStr.replace(",", "."));

                int nextId = cacheVendas.isEmpty() ? 1 : cacheVendas.get(cacheVendas.size()-1).id + 1;
                cacheVendas.add(new Venda(nextId, p, c, v));
                saveToBin();
                updateTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, insira um valor numérico válido.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String p = JOptionPane.showInputDialog(this, "Novo nome para " + cacheVendas.get(row).produto + ":", cacheVendas.get(row).produto);
                if (p != null && !p.trim().isEmpty()) {
                    cacheVendas.get(row).produto = p;
                    saveToBin();
                    updateTable();
                }
            }
        });

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1 && JOptionPane.showConfirmDialog(this, "Confirmar exclusão?") == JOptionPane.YES_OPTION) {
                cacheVendas.remove(row);
                saveToBin();
                updateTable();
            }
        });

        btnReport.addActionListener(e -> mostrarRelatorio());
        btnCSV.addActionListener(e -> exportToCSV());
        btnExit.addActionListener(e -> confirmarSaida());

        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Venda v : cacheVendas) tableModel.addRow(new Object[]{v.id, v.produto, v.categoria, v.valor});
    }

    private void mostrarRelatorio() {
        JTextArea area = new JTextArea(20, 45);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("       RELATÓRIO TÉCNICO DE VENDAS       \n");
        sb.append("==========================================\n\n");
        
        if(cacheVendas.isEmpty()) {
            sb.append("Base de dados vazia.");
        } else {
            // Itens Frequentes
            Map<String, Long> freq = cacheVendas.stream()
                .collect(Collectors.groupingBy(v -> v.produto, Collectors.counting()));
            sb.append("--- RANKING DE PEDIDOS ---\n");
            freq.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> sb.append(String.format("%-20s: %d unidades\n", entry.getKey(), entry.getValue())));

            // Faturamento por Categoria
            Map<String, Double> porCat = cacheVendas.stream()
                .collect(Collectors.groupingBy(v -> v.categoria, Collectors.summingDouble(v -> v.valor)));
            sb.append("\n--- FATURAMENTO POR CATEGORIA ---\n");
            porCat.forEach((k, v) -> sb.append(String.format("%-20s: R$ %.2f\n", k, v)));

            sb.append("\n--- LOG DE REGISTROS ---\n");
            for(Venda v : cacheVendas) sb.append(String.format("ID %-3d | %-15s | R$ %.2f\n", v.id, v.produto, v.valor));
        }
        
        area.setText(sb.toString());
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.add(new JScrollPane(area), BorderLayout.CENTER);
        
        JButton btnPrint = new JButton("Imprimir Relatório");
        btnPrint.addActionListener(e -> {
            try { 

            	// Define as propriedades da impressora
            	boolean complete = area.print(
            		new MessageFormat("Relatório de Vendas Fas-Food"), 	// Cabeça
            		new MessageFormat("Página {0}"),					// Rodapé
            		true,												// Mostrar diálogo de impressão
            		null,												// Serviço de impressão (null = padrão)
            		null,												// Atributos
            		true 												// Forçar diálogo interativo
            	);

            	if (complete) {

            		JOptionPane.showMessageDialog(this, "Impressão finalizada com sucesso!", "Status", JOptionPane.INFORMATION_MESSAGE);

            	} else {

            		JOptionPane.showMessageDialog(this, "A impressão foi cancelada.", "Status", JOptionPane.WARNING_MESSAGE);

            	}


            } catch (PrinterException ex) {

            	JOptionPane.showMessageDialog(this, "Erro de hardware/software na impressão: " + ex.getMessage());

            }
            
        });
        pnl.add(btnPrint, BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(this, pnl, "Relatório Geral", JOptionPane.PLAIN_MESSAGE);
    }

    private void confirmarSaida() {
        int op = JOptionPane.showConfirmDialog(this, "Deseja salvar as alterações no SQLite antes de encerrar?", "Confirmar Saída", JOptionPane.YES_NO_CANCEL_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            saveToDatabase();
            System.exit(0);
        } else if (op == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    private void saveToBin() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BIN_FILE))) {
            oos.writeObject(cacheVendas);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadFromBinOrDB() {
        File f = new File(BIN_FILE);
        if (f.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                cacheVendas = (List<Venda>) ois.readObject();
            } catch (Exception e) { loadFromDatabase(); }
        } else { loadFromDatabase(); }
    }

    private void initDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS vendas (id INTEGER, produto TEXT, categoria TEXT, valor REAL)");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM vendas");
            while (rs.next()) cacheVendas.add(new Venda(rs.getInt("id"), rs.getString("produto"), rs.getString("categoria"), rs.getDouble("valor")));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void saveToDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            conn.createStatement().execute("DELETE FROM vendas");
            PreparedStatement ps = conn.prepareStatement("INSERT INTO vendas (id, produto, categoria, valor) VALUES (?,?,?,?)");
            for (Venda v : cacheVendas) {
                ps.setInt(1, v.id); ps.setString(2, v.produto);
                ps.setString(3, v.categoria); ps.setDouble(4, v.valor);
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            new File(BIN_FILE).delete(); // Cache limpo após sincronia bem sucedida
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void exportToCSV() {
        try (PrintWriter pw = new PrintWriter(new File("relatorio_vendas.csv"))) {
            pw.println("ID;Produto;Categoria;Valor");
            for (Venda v : cacheVendas) pw.printf("%d;%s;%s;%.2f%n", v.id, v.produto, v.categoria, v.valor);
            JOptionPane.showMessageDialog(this, "Arquivo relatorio_vendas.csv gerado!");
        } catch (IOException e) { JOptionPane.showMessageDialog(this, "Falha ao gerar arquivo."); }
    }

    public static void main(String[] args) {
        // Look and Feel para se adaptar melhor ao Linux Lite
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MainUI().setVisible(true));
    }

}















