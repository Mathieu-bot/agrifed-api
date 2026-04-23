package mg.hei.agrifed.agrifedapi.repository.impl;

import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.entity.Collectivity;
import mg.hei.agrifed.agrifedapi.exception.DatabaseException;
import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCollectivityRepositoryImpl implements CollectivityRepository {

    private final DataSource dataSource;

    public JdbcCollectivityRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Collectivity save(Collectivity collectivity) {
        String sql = "INSERT INTO collectivity (number, name, specialty, city, creation_date, federation_id, status, location, federation_approval, authorized_by, authorization_date, rejection_reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collectivity.getNumber());
            stmt.setString(2, collectivity.getName());
            stmt.setString(3, collectivity.getSpecialty());
            stmt.setString(4, collectivity.getCity());
            stmt.setDate(5, collectivity.getCreationDate() != null ? Date.valueOf(collectivity.getCreationDate()) : new Date(System.currentTimeMillis()));
            stmt.setString(6, collectivity.getFederationId() != null ? collectivity.getFederationId() : "1");
            stmt.setString(7, collectivity.getStatus() != null ? collectivity.getStatus() : "pending");
            stmt.setString(8, collectivity.getLocation());
            stmt.setBoolean(9, collectivity.getFederationApproval() != null ? collectivity.getFederationApproval() : false);

            if (collectivity.getAuthorizedBy() != null) {
                stmt.setString(10, collectivity.getAuthorizedBy());
            } else {
                stmt.setNull(10, Types.VARCHAR);
            }
            stmt.setDate(11, collectivity.getAuthorizationDate() != null ? Date.valueOf(collectivity.getAuthorizationDate()) : null);
            stmt.setString(12, collectivity.getRejectionReason());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                collectivity.setId(rs.getString("id"));
            }
            return collectivity;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to save collectivity", e);
        }
    }

    @Override
    public Optional<Collectivity> findById(String id) {
        String sql = "SELECT id, number, name, specialty, city, creation_date, federation_id, status, location, federation_approval, authorized_by, authorization_date, rejection_reason FROM collectivity WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToCollectivity(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find collectivity by id: " + id, e);
        }
    }

    @Override
    public Optional<Collectivity> findByNumber(String number) {
        String sql = "SELECT id, number, name, specialty, city, creation_date, federation_id, status, location, federation_approval, authorized_by, authorization_date, rejection_reason FROM collectivity WHERE number = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, number);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToCollectivity(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find collectivity by number: " + number, e);
        }
    }

    @Override
    public Optional<Collectivity> findByName(String name) {
        String sql = "SELECT id, number, name, specialty, city, creation_date, federation_id, status, location, federation_approval, authorized_by, authorization_date, rejection_reason FROM collectivity WHERE name = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToCollectivity(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find collectivity by name: " + name, e);
        }
    }

    @Override
    public List<Collectivity> findAll() {
        String sql = "SELECT id, number, name, specialty, city, creation_date, federation_id, status, location, federation_approval, authorized_by, authorization_date, rejection_reason FROM collectivity";

        List<Collectivity> collectivities = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                collectivities.add(mapRowToCollectivity(rs));
            }
            return collectivities;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all collectivities", e);
        }
    }

    @Override
    public List<Collectivity> findByStatus(String status) {
        String sql = "SELECT id, number, name, specialty, city, creation_date, federation_id, status, location, federation_approval, authorized_by, authorization_date, rejection_reason FROM collectivity WHERE status = ?";

        List<Collectivity> collectivities = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                collectivities.add(mapRowToCollectivity(rs));
            }
            return collectivities;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find collectivities by status", e);
        }
    }

    @Override
    public List<Collectivity> findByIdIn(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder sql = new StringBuilder("SELECT id, number, name, specialty, city, creation_date, federation_id, status, location, federation_approval, authorized_by, authorization_date, rejection_reason FROM collectivity WHERE id IN (");

        for (int i = 0; i < ids.size(); i++) {
            sql.append(i > 0 ? ",?" : "?");
        }
        sql.append(")");

        List<Collectivity> collectivities = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < ids.size(); i++) {
                stmt.setString(i + 1, ids.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                collectivities.add(mapRowToCollectivity(rs));
            }
            return collectivities;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find collectivities by ids", e);
        }
    }

@Override
    public Collectivity update(Collectivity collectivity) {
        String sql = "UPDATE collectivity SET number = ?, name = ?, specialty = ?, city = ?, creation_date = ?, federation_id = ?, status = ?, location = ?, federation_approval = ?, authorized_by = ?, authorization_date = ?, rejection_reason = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collectivity.getNumber());
            stmt.setString(2, collectivity.getName());
            stmt.setString(3, collectivity.getSpecialty());
            stmt.setString(4, collectivity.getCity());
            stmt.setDate(5, collectivity.getCreationDate() != null ? Date.valueOf(collectivity.getCreationDate()) : null);
            stmt.setString(6, collectivity.getFederationId());
            stmt.setString(7, collectivity.getStatus());
            stmt.setString(8, collectivity.getLocation());
            stmt.setBoolean(9, collectivity.getFederationApproval());

            if (collectivity.getAuthorizedBy() != null) {
                stmt.setString(10, collectivity.getAuthorizedBy());
            } else {
                stmt.setNull(10, Types.VARCHAR);
            }
            stmt.setDate(11, collectivity.getAuthorizationDate() != null ? Date.valueOf(collectivity.getAuthorizationDate()) : null);
            stmt.setString(12, collectivity.getRejectionReason());
            stmt.setString(13, collectivity.getId());

            stmt.executeUpdate();
            return collectivity;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update collectivity", e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM collectivity WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete collectivity", e);
        }
    }

    @Override
    public List<Collectivity> findByFederationId(String federationId) {
        String sql = "SELECT id, number, name, specialty, city, creation_date, federation_id, status, location, federation_approval, authorized_by, authorization_date, rejection_reason FROM collectivity WHERE federation_id = ?";

        List<Collectivity> collectivities = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, federationId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                collectivities.add(mapRowToCollectivity(rs));
            }
            return collectivities;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find collectivities by federation id", e);
        }
    }

    private Collectivity mapRowToCollectivity(ResultSet rs) throws SQLException {
        Collectivity c = new Collectivity();
        c.setId(rs.getString("id"));
        c.setNumber(rs.getString("number"));
        c.setName(rs.getString("name"));
        c.setSpecialty(rs.getString("specialty"));
        c.setCity(rs.getString("city"));

        Date creationDate = rs.getDate("creation_date");
        if (creationDate != null) {
            c.setCreationDate(creationDate.toLocalDate());
        }

        c.setFederationId(rs.getString("federation_id"));
        c.setStatus(rs.getString("status"));
        c.setLocation(rs.getString("location"));
        c.setFederationApproval(rs.getBoolean("federation_approval"));

        String authorizedBy = rs.getString("authorized_by");
        if (authorizedBy != null) {
            c.setAuthorizedBy(authorizedBy);
        }

        Date authorizationDate = rs.getDate("authorization_date");
        if (authorizationDate != null) {
            c.setAuthorizationDate(authorizationDate.toLocalDate());
        }

        c.setRejectionReason(rs.getString("rejection_reason"));

        return c;
    }
}