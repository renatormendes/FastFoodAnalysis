import java.sql.*;


public class Main {

	private static final String DB_URL = "jdbc:sqlite:vendas.db";

	public static void main(String[] args) {

		try (Connection conn = DriverManager.getConnection(DB_URL)) {

			if (conn != null) {

				setupDatabase(conn);
				insertMockData(conn);
				generateReport(conn);

			}

		} catch (SQLException e) {

			System.out.println("Erro: " + e.getMessage());

		}
		
	}

	private static void setupDatabase(Connection conn) throws SQLException {

		String sql ="CREATE TABLE IF NOT EXISTS vendas (" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"produto TEXT NOT NULL," +
					"categoria TEXT NOT NULL, " +
					"valor REAL NOT NULL, " +
					"data DATE DEFAULT CURRENT_DATE);";
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
		System.out.println("[OK] Banco de dados iniciado.");

	}

	private static void insertMockData(Connection conn) throws SQLException {

		String check = "SELECT count(*) FROM vendas";
		ResultSet rs = conn.createStatement().executeQuery(check);

		if (rs.next() && rs.getInt(1) == 0) {
			String sql = "INSERT INTO vendas (produto, categoria, valor) VALUES " +
						 "('Big Burger', 'Lanches', 29.90), " +
						 "('Batata G', 'Acompanhamentos', 12.00), " +
						 "('Combo Master', 'Combos', 45.00), " +
						 "('Refrigerante 500ml', 'Bebidas', 8.50);";
			conn.createStatement().execute(sql);
			System.out.println("[OK] Dados de teste inseridos.");

		}

	}

	private static void generateReport(Connection conn) throws SQLException {

		System.out.println("\n--- RELATÓRIO DE VENDAS POR CATEGORIA ---");
		String sql = "SELECT categoria, SUM(valor) as total FROM vendas GROUP BY categoria";
		ResultSet rs = conn.createStatement().executeQuery(sql);

		while (rs.next()) {

			System.out.printf("Categoria: %-14s | Total: R$ %.2f%n", rs.getString("categoria"), rs.getDouble("total"));
			
		}

	}

}