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
        String sql = "INSERT INTO collectivity_structure (collectivity_id, president_id, vice_president_id, treasurer_id, secretary_id) VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, structure.getCollectivityId());

            if (structure.getPresidentId() != null) {
                stmt.setInt(2, structure.getPresidentId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            if (structure.getVicePresidentId() != null) {
                stmt.setInt(3, structure.getVicePresidentId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            if (structure.getTreasurerId() != null) {
                stmt.setInt(4, structure.getTreasurerId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            if (structure.getSecretaryId() != null) {
                stmt.setInt(5, structure.getSecretaryId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                structure.setId(rs.getInt("id"));
            }
            return structure;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to save collectivity structure", e);
        }
    }

    @Override
    public Optional<CollectivityStructureEntity> findByCollectivityId(Integer collectivityId) {
        String sql = "SELECT id, collectivity_id, president_id, vice_president_id, treasurer_id, secretary_id FROM collectivity_structure WHERE collectivity_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, collectivityId);
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
    public void deleteByCollectivityId(Integer collectivityId) {
        String sql = "DELETE FROM collectivity_structure WHERE collectivity_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, collectivityId);
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
                stmt.setInt(1, structure.getPresidentId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            if (structure.getVicePresidentId() != null) {
                stmt.setInt(2, structure.getVicePresidentId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            if (structure.getTreasurerId() != null) {
                stmt.setInt(3, structure.getTreasurerId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            if (structure.getSecretaryId() != null) {
                stmt.setInt(4, structure.getSecretaryId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setInt(5, structure.getId());

            stmt.executeUpdate();
            return structure;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update collectivity structure", e);
        }
    }

    private CollectivityStructureEntity mapRowToStructure(ResultSet rs) throws SQLException {
        CollectivityStructureEntity structure = new CollectivityStructureEntity();
        structure.setId(rs.getInt("id"));
        structure.setCollectivityId(rs.getInt("collectivity_id"));

        int presidentId = rs.getInt("president_id");
        if (!rs.wasNull()) {
            structure.setPresidentId(presidentId);
        }
        int vicePresidentId = rs.getInt("vice_president_id");
        if (!rs.wasNull()) {
            structure.setVicePresidentId(vicePresidentId);
        }
        int treasurerId = rs.getInt("treasurer_id");
        if (!rs.wasNull()) {
            structure.setTreasurerId(treasurerId);
        }
        int secretaryId = rs.getInt("secretary_id");
        if (!rs.wasNull()) {
            structure.setSecretaryId(secretaryId);
        }

        return structure;
    }
}