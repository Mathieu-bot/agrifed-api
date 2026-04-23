package mg.hei.agrifed.agrifedapi.repository.impl;

import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.entity.MembershipFee;
import mg.hei.agrifed.agrifedapi.exception.DatabaseException;
import mg.hei.agrifed.agrifedapi.repository.MembershipFeeRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMembershipFeeRepositoryImpl implements MembershipFeeRepository {

    private final DataSource dataSource;

    public JdbcMembershipFeeRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<MembershipFee> findByCollectivityId(String collectivityId) {
        String sql = "SELECT id, eligible_from, frequency, amount, label, status, collectivity_id " +
                "FROM membership_fee WHERE collectivity_id = ?";
        List<MembershipFee> fees = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectivityId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                fees.add(mapRow(rs));
            }
            return fees;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find membership fees", e);
        }
    }

    @Override
    public MembershipFee save(MembershipFee fee) {
        String sql = "INSERT INTO membership_fee (eligible_from, frequency, amount, label, status, collectivity_id) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, fee.getEligibleFrom() != null ? Date.valueOf(fee.getEligibleFrom()) : null);
            stmt.setString(2, fee.getFrequency());
            stmt.setBigDecimal(3, fee.getAmount());
            stmt.setString(4, fee.getLabel());
            stmt.setString(5, fee.getStatus() != null ? fee.getStatus() : "ACTIVE");
            stmt.setString(6, fee.getCollectivityId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                fee.setId(rs.getString("id"));
            }
            return fee;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save membership fee", e);
        }
    }

    @Override
    public Optional<MembershipFee> findById(String id) {
        String sql = "SELECT id, eligible_from, frequency, amount, label, status, collectivity_id " +
                "FROM membership_fee WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find membership fee by id", e);
        }
    }

    @Override
    public MembershipFee update(MembershipFee fee) {
        String sql = "UPDATE membership_fee SET eligible_from = ?, frequency = ?, amount = ?, label = ?, status = ?, collectivity_id = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, fee.getEligibleFrom() != null ? Date.valueOf(fee.getEligibleFrom()) : null);
            stmt.setString(2, fee.getFrequency());
            stmt.setBigDecimal(3, fee.getAmount());
            stmt.setString(4, fee.getLabel());
            stmt.setString(5, fee.getStatus());
            stmt.setString(6, fee.getCollectivityId());
            stmt.setString(7, fee.getId());
            stmt.executeUpdate();
            return fee;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update membership fee", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM membership_fee WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete membership fee", e);
        }
    }

    private MembershipFee mapRow(ResultSet rs) throws SQLException {
        MembershipFee f = new MembershipFee();
        f.setId(rs.getString("id"));
        Date eligibleDate = rs.getDate("eligible_from");
        f.setEligibleFrom(eligibleDate != null ? eligibleDate.toLocalDate() : null);
        f.setFrequency(rs.getString("frequency"));
        f.setAmount(rs.getBigDecimal("amount"));
        f.setLabel(rs.getString("label"));
        f.setStatus(rs.getString("status"));
        f.setCollectivityId(rs.getString("collectivity_id"));
        return f;
    }
}