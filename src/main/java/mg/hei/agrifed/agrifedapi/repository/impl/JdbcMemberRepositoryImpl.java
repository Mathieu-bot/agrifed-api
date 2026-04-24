package mg.hei.agrifed.agrifedapi.repository.impl;

import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.entity.Member;
import mg.hei.agrifed.agrifedapi.exception.DatabaseException;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepositoryImpl implements MemberRepository {

    private final DataSource dataSource;

    public JdbcMemberRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        if (member.getId() == null || member.getId().isBlank()) {
            member.setId("mem-" + java.util.UUID.randomUUID().toString().substring(0, 8));
        }

        String sql = "INSERT INTO member (id, lastname, firstname, birth_date, gender, address, occupation, phone, email, membership_date, registration_fee_paid, membership_dues_paid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, member.getId());
            stmt.setString(2, member.getLastName());
            stmt.setDate(3, member.getBirthDate() != null ? Date.valueOf(member.getBirthDate()) : null);
            stmt.setString(4, member.getGender());
            stmt.setString(5, member.getAddress());
            stmt.setString(6, member.getOccupation());
            stmt.setString(7, member.getPhone());
            stmt.setString(8, member.getEmail());
            stmt.setDate(9, member.getMembershipDate() != null ? Date.valueOf(member.getMembershipDate()) : new Date(System.currentTimeMillis()));
            stmt.setBoolean(10, member.getRegistrationFeePaid() != null ? member.getRegistrationFeePaid() : false);
            stmt.setBoolean(11, member.getMembershipDuesPaid() != null ? member.getMembershipDuesPaid() : false);
            stmt.setBoolean(12, member.getRegistrationFeePaid() != null ? member.getRegistrationFeePaid() : false);

            stmt.executeUpdate();
            return member;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to save member", e);
        }
    }

    @Override
    public Optional<Member> findById(String id) {
        String sql = "SELECT id, lastname, firstname, birth_date, gender, address, occupation, phone, email, membership_date, registration_fee_paid, membership_dues_paid FROM member WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToMember(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find member by id: " + id, e);
        }
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT id, lastname, firstname, birth_date, gender, address, occupation, phone, email, membership_date, registration_fee_paid, membership_dues_paid FROM member WHERE email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToMember(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find member by email: " + email, e);
        }
    }

    @Override
    public List<Member> findAll() {
        String sql = "SELECT id, lastname, firstname, birth_date, gender, address, occupation, phone, email, membership_date, registration_fee_paid, membership_dues_paid FROM member";

        List<Member> members = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                members.add(mapRowToMember(rs));
            }
            return members;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all members", e);
        }
    }

    @Override
    public List<Member> findByIdIn(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder sql = new StringBuilder("SELECT id, lastname, firstname, birth_date, gender, address, occupation, phone, email, membership_date, registration_fee_paid, membership_dues_paid FROM member WHERE id IN (");

        for (int i = 0; i < ids.size(); i++) {
            sql.append(i > 0 ? ",?" : "?");
        }
        sql.append(")");

        List<Member> members = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < ids.size(); i++) {
                stmt.setString(i + 1, ids.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(mapRowToMember(rs));
            }
            return members;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find members by ids", e);
        }
    }

    @Override
    public Member update(Member member) {
        String sql = "UPDATE member SET lastname = ?, firstname = ?, birth_date = ?, gender = ?, address = ?, occupation = ?, phone = ?, email = ?, membership_date = ?, registration_fee_paid = ?, membership_dues_paid = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, member.getLastName());
            stmt.setString(2, member.getFirstName());
            stmt.setDate(3, member.getBirthDate() != null ? Date.valueOf(member.getBirthDate()) : null);
            stmt.setString(4, member.getGender());
            stmt.setString(5, member.getAddress());
            stmt.setString(6, member.getOccupation());
            stmt.setString(7, member.getPhone());
            stmt.setString(8, member.getEmail());
            stmt.setDate(9, member.getMembershipDate() != null ? Date.valueOf(member.getMembershipDate()) : null);
            stmt.setBoolean(10, member.getRegistrationFeePaid() != null ? member.getRegistrationFeePaid() : false);
            stmt.setBoolean(11, member.getMembershipDuesPaid() != null ? member.getMembershipDuesPaid() : false);
            stmt.setString(12, member.getId());

            stmt.executeUpdate();
            return member;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update member", e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM member WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete member", e);
        }
    }

    @Override
    public List<Member> findByCollectivityId(String collectivityId) {
        String sql = "SELECT m.id, m.lastname, m.firstname, m.birth_date, m.gender, m.address, m.occupation, m.phone, m.email, m.membership_date, m.registration_fee_paid, m.membership_dues_paid FROM member m INNER JOIN membership_history mh ON m.id = mh.member_id WHERE mh.collectivity_id = ?";

        List<Member> members = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collectivityId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                members.add(mapRowToMember(rs));
            }
            return members;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find members by collectivity id", e);
        }
    }

    private Member mapRowToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getString("id"));
        member.setLastName(rs.getString("lastname"));
        member.setFirstName(rs.getString("firstname"));

        Date birthDate = rs.getDate("birth_date");
        if (birthDate != null) {
            member.setBirthDate(birthDate.toLocalDate());
        }

        String gender = rs.getString("gender");
        member.setGender(gender);

        member.setAddress(rs.getString("address"));
        member.setOccupation(rs.getString("occupation"));
        member.setPhone(rs.getString("phone"));
        member.setEmail(rs.getString("email"));

        Date membershipDate = rs.getDate("membership_date");
        if (membershipDate != null) {
            member.setMembershipDate(membershipDate.toLocalDate());
        }

        member.setRegistrationFeePaid(rs.getBoolean("registration_fee_paid"));
        member.setMembershipDuesPaid(rs.getBoolean("membership_dues_paid"));

        return member;
    }
}