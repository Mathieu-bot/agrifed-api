package mg.hei.agrifed.agrifedapi.repository.impl;

import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.entity.CollectivityStructureEntity;
import mg.hei.agrifed.agrifedapi.exception.DatabaseException;
import mg.hei.agrifed.agrifedapi.repository.CollectivityStructureRepository;

import java.sql.*;
import java.util.Optional;

public class JdbcCollectivityStructureRepositoryImpl implements CollectivityStructureRepository {

    private final DataSource dataSource;

    public JdbcCollectivityStructureRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

@Override
    public CollectivityStructureEntity save(CollectivityStructureEntity structure) {
        if (structure.getId() == null || structure.getId().isBlank()) {
            throw new IllegalArgumentException("CollectivityStructure ID is required");
        }

        String sql = "INSERT INTO collectivity_structure (id, collectivity_id, president_id, vice_president_id, treasurer_id, secretary_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, structure.getId());
            stmt.setString(2, structure.getCollectivityId());

            if (structure.getPresidentId() != null) {
                stmt.setString(3, structure.getPresidentId());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            if (structure.getVicePresidentId() != null) {
                stmt.setString(4, structure.getVicePresidentId());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            if (structure.getTreasurerId() != null) {
                stmt.setString(5, structure.getTreasurerId());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }
            if (structure.getSecretaryId() != null) {
                stmt.setString(6, structure.getSecretaryId());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }

            stmt.executeUpdate();
            return structure;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to save collectivity structure", e);
        }
    }

    @Override
    public Optional<CollectivityStructureEntity> findByCollectivityId(String collectivityId) {
        String sql = "SELECT id, collectivity_id, president_id, vice_president_id, treasurer_id, secretary_id FROM collectivity_structure WHERE collectivity_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collectivityId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToStructure(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find collectivity structure", e);
        }
    }

    @Override
    public void deleteByCollectivityId(String collectivityId) {
        String sql = "DELETE FROM collectivity_structure WHERE collectivity_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collectivityId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete collectivity structure", e);
        }
    }

    @Override
    public CollectivityStructureEntity update(CollectivityStructureEntity structure) {
        String sql = "UPDATE collectivity_structure SET president_id = ?, vice_president_id = ?, treasurer_id = ?, secretary_id = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (structure.getPresidentId() != null) {
                stmt.setString(1, structure.getPresidentId());
            } else {
                stmt.setNull(1, Types.VARCHAR);
            }
            if (structure.getVicePresidentId() != null) {
                stmt.setString(2, structure.getVicePresidentId());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            if (structure.getTreasurerId() != null) {
                stmt.setString(3, structure.getTreasurerId());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            if (structure.getSecretaryId() != null) {
                stmt.setString(4, structure.getSecretaryId());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            stmt.setString(5, structure.getId());

            stmt.executeUpdate();
            return structure;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update collectivity structure", e);
        }
    }

    private CollectivityStructureEntity mapRowToStructure(ResultSet rs) throws SQLException {
        CollectivityStructureEntity structure = new CollectivityStructureEntity();
        structure.setId(rs.getString("id"));
        structure.setCollectivityId(rs.getString("collectivity_id"));

        String presidentId = rs.getString("president_id");
        if (presidentId != null) {
            structure.setPresidentId(presidentId);
        }
        String vicePresidentId = rs.getString("vice_president_id");
        if (vicePresidentId != null) {
            structure.setVicePresidentId(vicePresidentId);
        }
        String treasurerId = rs.getString("treasurer_id");
        if (treasurerId != null) {
            structure.setTreasurerId(treasurerId);
        }
        String secretaryId = rs.getString("secretary_id");
        if (secretaryId != null) {
            structure.setSecretaryId(secretaryId);
        }

        return structure;
    }
}