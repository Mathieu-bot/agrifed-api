package mg.hei.agrifed.agrifedapi.repository.impl;

import lombok.AllArgsConstructor;
import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.entity.AccountFull;
import mg.hei.agrifed.agrifedapi.exception.DatabaseException;
import mg.hei.agrifed.agrifedapi.repository.AccountRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class JdbcAccountRepositoryImpl implements AccountRepository {

    private final DataSource dataSource;

    @Override
    public Optional<AccountFull> findById(Integer id) {
        String sql = """
            SELECT a.id, a.type, a.collectivity_id, a.federation_id, a.balance,
                   ae.holder_name   AS bank_holder,  ae.bank_name, ae.account_number, ae.rib_key,
                   ae.bank_code, ae.branch_code,
                   am.holder_name   AS mob_holder,   am.service_name,  am.phone_number
            FROM account a
            LEFT JOIN account_extended ae ON ae.account_id = a.id
            LEFT JOIN account_mobile   am ON am.account_id = a.id
            WHERE a.id = ?
            """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find account by id", e);
        }
    }

    @Override
    public List<AccountFull> findByCollectivityId(Integer collectivityId) {
        String sql = """
            SELECT a.id, a.type, a.collectivity_id, a.federation_id, a.balance,
                   ae.holder_name   AS bank_holder,  ae.bank_name, ae.account_number, ae.rib_key,
                   ae.bank_code, ae.branch_code,
                   am.holder_name   AS mob_holder,   am.service_name,  am.phone_number
            FROM account a
            LEFT JOIN account_extended ae ON ae.account_id = a.id
            LEFT JOIN account_mobile   am ON am.account_id = a.id
            WHERE a.collectivity_id = ?
            """;
        List<AccountFull> accounts = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, collectivityId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                accounts.add(mapRow(rs));
            }
            return accounts;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find accounts by collectivity id", e);
        }
    }

    @Override
    public void updateBalance(Integer accountId, BigDecimal delta) {
        String sql = "UPDATE account SET balance = balance + ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, delta);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update account balance", e);
        }
    }

    private AccountFull mapRow(ResultSet rs) throws SQLException {
        AccountFull a = new AccountFull();
        a.setId(rs.getInt("id"));
        a.setType(rs.getString("type"));
        a.setCollectivityId(rs.getObject("collectivity_id") != null ? rs.getInt("collectivity_id") : null);
        a.setFederationId(rs.getObject("federation_id") != null ? rs.getInt("federation_id") : null);
        a.setBalance(rs.getBigDecimal("balance") != null ? rs.getBigDecimal("balance") : BigDecimal.ZERO);

        String type = rs.getString("type");
        if ("bank".equals(type)) {
            a.setHolderName(rs.getString("bank_holder"));
            a.setBankName(rs.getString("bank_name"));
            a.setBankCode(rs.getString("bank_code"));
            a.setBankBranchCode(rs.getString("branch_code"));
            a.setAccountNumber(rs.getString("account_number"));
            a.setRibKey(rs.getString("rib_key"));
        } else if ("mobile_money".equals(type)) {
            a.setHolderName(rs.getString("mob_holder"));
            a.setServiceName(rs.getString("service_name"));
            a.setPhoneNumber(rs.getString("phone_number"));
        }
        return a;
    }
}