package mg.hei.agrifed.agrifedapi.repository.impl;

import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.entity.Sponsorship;
import mg.hei.agrifed.agrifedapi.exception.DatabaseException;
import mg.hei.agrifed.agrifedapi.repository.SponsorshipRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcSponsorshipRepositoryImpl implements SponsorshipRepository {

    private final DataSource dataSource;

    public JdbcSponsorshipRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Sponsorship> findBySponsoredMemberId(String memberId) {
        String sql = "SELECT id, sponsorship_date, sponsor_member_id, sponsored_member_id FROM sponsorship WHERE sponsored_member_id = ?";
        List<Sponsorship> sponsorships = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sponsorships.add(mapRow(rs));
            }
            return sponsorships;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find sponsorships", e);
        }
    }

    @Override
    public List<Sponsorship> findBySponsorMemberId(String memberId) {
        String sql = "SELECT id, sponsorship_date, sponsor_member_id, sponsored_member_id FROM sponsorship WHERE sponsor_member_id = ?";
        List<Sponsorship> sponsorships = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sponsorships.add(mapRow(rs));
            }
            return sponsorships;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find sponsorships", e);
        }
    }

    private Sponsorship mapRow(ResultSet rs) throws SQLException {
        Sponsorship s = new Sponsorship();
        s.setId(rs.getString("id"));
        Date date = rs.getDate("sponsorship_date");
        s.setSponsorshipDate(date != null ? date.toLocalDate() : null);
        s.setSponsorMemberId(rs.getString("sponsor_member_id"));
        s.setSponsoredMemberId(rs.getString("sponsored_member_id"));
        return s;
    }
}