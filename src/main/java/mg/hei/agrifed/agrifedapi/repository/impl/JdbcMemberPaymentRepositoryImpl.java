package mg.hei.agrifed.agrifedapi.repository.impl;

import lombok.AllArgsConstructor;
import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.entity.MemberPayment;
import mg.hei.agrifed.agrifedapi.exception.DatabaseException;
import mg.hei.agrifed.agrifedapi.repository.MemberPaymentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class JdbcMemberPaymentRepositoryImpl implements MemberPaymentRepository {
    private final DataSource dataSource;

    @Override
    public MemberPayment save(MemberPayment payment){
        String sql= """
                INSERT INTO member_payment (amount, payment_mode, member_id, membership_fee_id, account_credited_id, creation_date)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING ID
                """;
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);){
                stmt.setBigDecimal(1, payment.getAmount());
                stmt.setString(2, payment.getPaymentMode());
                stmt.setInt(3, payment.getMemberId());
                stmt.setInt(4, payment.getMembershipFeeId());
                stmt.setInt(5, payment.getAccountCreditedId());
                stmt.setDate(6, payment.getCreationDate() != null
                        ? Date.valueOf(payment.getCreationDate())
                        : new Date(System.currentTimeMillis()));
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                payment.setId(rs.getInt("id"));
            }
            return payment;
        } catch (SQLException e){
            throw new DatabaseException("Failed to save member payment", e);
        }
    }

    @Override
    public List<MemberPayment> findByMemberId(Integer memberId) {
        String sql = "SELECT id, amount, payment_mode, member_id, membership_fee_id, account_credited_id, creation_date " +
                "FROM member_payment WHERE member_id = ?";
        List<MemberPayment> payments = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) payments.add(mapRow(rs));
            return payments;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find payments for member", e);
        }
    }

    private MemberPayment mapRow(ResultSet rs) throws SQLException {
        MemberPayment p = new MemberPayment();
        p.setId(rs.getInt("id"));
        p.setAmount(rs.getBigDecimal("amount"));
        p.setPaymentMode(rs.getString("payment_mode"));
        p.setMemberId(rs.getInt("member_id"));
        p.setMembershipFeeId(rs.getInt("membership_fee_id"));
        p.setAccountCreditedId(rs.getInt("account_credited_id"));
        Date d = rs.getDate("creation_date");
        if (d != null) p.setCreationDate(d.toLocalDate());
        return p;
    }
}
