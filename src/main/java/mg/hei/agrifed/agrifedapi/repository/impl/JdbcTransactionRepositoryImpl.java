package mg.hei.agrifed.agrifedapi.repository.impl;

import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.entity.Transaction;
import mg.hei.agrifed.agrifedapi.exception.DatabaseException;
import mg.hei.agrifed.agrifedapi.repository.TransactionRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcTransactionRepositoryImpl implements TransactionRepository {

    private final DataSource dataSource;

    public JdbcTransactionRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null || transaction.getId().isBlank()) {
            throw new IllegalArgumentException("Transaction ID is required");
        }

        String sql = "INSERT INTO \"transaction\" (id, account_id, amount, transaction_date, description, member_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transaction.getId());
            stmt.setString(2, transaction.getAccountId());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setDate(4, Date.valueOf(
                    transaction.getTransactionDate() != null ? transaction.getTransactionDate() : LocalDate.now()));
            stmt.setString(5, transaction.getDescription());
            if (transaction.getMemberId() != null) {
                stmt.setString(6, transaction.getMemberId());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }
            stmt.executeUpdate();
            return transaction;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save transaction", e);
        }
    }

    @Override
    public List<Transaction> findByCollectivityIdAndDateBetween(String collectivityId, LocalDate from, LocalDate to) {
        String sql = """
            SELECT t.id, t.account_id, t.amount, t.transaction_date, t.description, t.member_id
            FROM "transaction" t
            INNER JOIN account a ON a.id = t.account_id
            WHERE a.collectivity_id = ?
              AND t.transaction_date BETWEEN ? AND ?
            ORDER BY t.transaction_date DESC
            """;
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectivityId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) transactions.add(mapRow(rs));
            return transactions;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find transactions", e);
        }
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getString("id"));
        t.setAccountId(rs.getString("account_id"));
        t.setAmount(rs.getBigDecimal("amount"));
        Date d = rs.getDate("transaction_date");
        if (d != null) t.setTransactionDate(d.toLocalDate());
        t.setDescription(rs.getString("description"));
        String memberId = rs.getString("member_id");
        if (memberId != null) t.setMemberId(memberId);
        return t;
    }
}