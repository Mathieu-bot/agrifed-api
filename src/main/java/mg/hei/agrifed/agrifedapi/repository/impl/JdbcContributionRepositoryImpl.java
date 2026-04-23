package mg.hei.agrifed.agrifedapi.repository.impl;

import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.entity.Contribution;
import mg.hei.agrifed.agrifedapi.exception.DatabaseException;
import mg.hei.agrifed.agrifedapi.repository.ContributionRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcContributionRepositoryImpl implements ContributionRepository {

    private final DataSource dataSource;

    public JdbcContributionRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Contribution save(Contribution contribution) {
        String sql = """
            INSERT INTO contribution (amount, collection_date, payment_method, type,
                federation_percentage, member_id, collectivity_id, membership_fee_id,
                account_credited_id, creation_date, label)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
            """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, contribution.getAmount());
            stmt.setDate(2, contribution.getCollectionDate() != null
                ? Date.valueOf(contribution.getCollectionDate()) : Date.valueOf(LocalDate.now()));
            stmt.setString(3, contribution.getPaymentMethod());
            stmt.setString(4, contribution.getType());
            stmt.setBigDecimal(5, contribution.getFederationPercentage() != null
                ? contribution.getFederationPercentage() : BigDecimal.ZERO);
            stmt.setInt(6, contribution.getMemberId());
            stmt.setInt(7, contribution.getCollectivityId());
            if (contribution.getMembershipFeeId() != null) {
                stmt.setInt(8, contribution.getMembershipFeeId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            if (contribution.getAccountCreditedId() != null) {
                stmt.setInt(9, contribution.getAccountCreditedId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setDate(10, contribution.getCreationDate() != null
                ? Date.valueOf(contribution.getCreationDate()) : Date.valueOf(LocalDate.now()));
            stmt.setString(11, contribution.getLabel());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                contribution.setId(rs.getInt("id"));
            }
            return contribution;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save contribution", e);
        }
    }

    @Override
    public List<Contribution> findByMemberId(Integer memberId) {
        String sql = """
            SELECT id, amount, collection_date, payment_method, type, federation_percentage,
                   member_id, collectivity_id, membership_fee_id, account_credited_id, creation_date, label
            FROM contribution WHERE member_id = ? ORDER BY creation_date DESC
            """;
        List<Contribution> contributions = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                contributions.add(mapRow(rs));
            }
            return contributions;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find contributions by member id", e);
        }
    }

    private Contribution mapRow(ResultSet rs) throws SQLException {
        Contribution c = new Contribution();
        c.setId(rs.getInt("id"));
        c.setAmount(rs.getBigDecimal("amount"));
        Date collectionDate = rs.getDate("collection_date");
        if (collectionDate != null) c.setCollectionDate(collectionDate.toLocalDate());
        c.setPaymentMethod(rs.getString("payment_method"));
        c.setType(rs.getString("type"));
        c.setFederationPercentage(rs.getBigDecimal("federation_percentage"));
        c.setMemberId(rs.getInt("member_id"));
        c.setCollectivityId(rs.getObject("collectivity_id") != null ? rs.getInt("collectivity_id") : null);
        c.setMembershipFeeId(rs.getObject("membership_fee_id") != null ? rs.getInt("membership_fee_id") : null);
        c.setAccountCreditedId(rs.getObject("account_credited_id") != null ? rs.getInt("account_credited_id") : null);
        Date creationDate = rs.getDate("creation_date");
        if (creationDate != null) c.setCreationDate(creationDate.toLocalDate());
        c.setLabel(rs.getString("label"));
        return c;
    }
}