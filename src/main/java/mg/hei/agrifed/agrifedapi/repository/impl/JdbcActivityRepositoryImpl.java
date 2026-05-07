package mg.hei.agrifed.agrifedapi.repository.impl;

import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.entity.CollectivityActivity;
import mg.hei.agrifed.agrifedapi.exception.DatabaseException;
import mg.hei.agrifed.agrifedapi.repository.ActivityRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcActivityRepositoryImpl implements ActivityRepository {

    private final DataSource dataSource;

    public JdbcActivityRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<CollectivityActivity> findAllByCollectivityId(String collectivityId) {
        String sql = "SELECT id, label, activity_type, executive_date, recurrence_week_ordinal, " +
                "recurrence_day_of_week, collectivity_id, federation_id FROM activity WHERE collectivity_id = ?";
        List<CollectivityActivity> activities = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectivityId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                activities.add(mapRow(rs));
            }
            for (CollectivityActivity a : activities) {
                a.setMemberOccupationConcerned(findOccupationsByActivityId(a.getId()));
            }
            return activities;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find activities by collectivity id", e);
        }
    }

    @Override
    public Optional<CollectivityActivity> findById(String id) {
        String sql = "SELECT id, label, activity_type, executive_date, recurrence_week_ordinal, " +
                "recurrence_day_of_week, collectivity_id, federation_id FROM activity WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                CollectivityActivity a = mapRow(rs);
                a.setMemberOccupationConcerned(findOccupationsByActivityId(a.getId()));
                return Optional.of(a);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find activity by id", e);
        }
    }

    @Override
    public Optional<CollectivityActivity> findByIdAndCollectivityId(String id, String collectivityId) {
        String sql = "SELECT id, label, activity_type, executive_date, recurrence_week_ordinal, " +
                "recurrence_day_of_week, collectivity_id, federation_id FROM activity WHERE id = ? AND collectivity_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, collectivityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                CollectivityActivity a = mapRow(rs);
                a.setMemberOccupationConcerned(findOccupationsByActivityId(a.getId()));
                return Optional.of(a);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find activity by id and collectivity id", e);
        }
    }

    @Override
    public CollectivityActivity save(CollectivityActivity activity) {
        if (activity.getId() == null || activity.getId().isBlank()) {
            activity.setId("act-" + java.util.UUID.randomUUID().toString().substring(0, 8));
        }
        String sql = "INSERT INTO activity (id, label, activity_type, executive_date, " +
                "recurrence_week_ordinal, recurrence_day_of_week, collectivity_id, federation_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, activity.getId());
            stmt.setString(2, activity.getLabel());
            stmt.setString(3, activity.getActivityType());
            stmt.setDate(4, activity.getExecutiveDate() != null ? Date.valueOf(activity.getExecutiveDate()) : null);
            stmt.setObject(5, activity.getRecurrenceWeekOrdinal(), Types.INTEGER);
            stmt.setString(6, activity.getRecurrenceDayOfWeek());
            stmt.setString(7, activity.getCollectivityId());
            stmt.setString(8, activity.getFederationId());
            stmt.executeUpdate();
            return activity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save activity", e);
        }
    }

    @Override
    public List<CollectivityActivity> saveAll(List<CollectivityActivity> activities) {
        for (CollectivityActivity a : activities) {
            save(a);
        }
        return activities;
    }

    @Override
    public void saveOccupations(String activityId, List<String> occupations) {
        if (occupations == null || occupations.isEmpty()) return;
        String sql = "INSERT INTO activity_occupation (activity_id, occupation) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String occ : occupations) {
                stmt.setString(1, activityId);
                stmt.setString(2, occ);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save activity occupations", e);
        }
    }

    @Override
    public List<String> findOccupationsByActivityId(String activityId) {
        String sql = "SELECT occupation FROM activity_occupation WHERE activity_id = ?";
        List<String> occupations = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, activityId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                occupations.add(rs.getString("occupation"));
            }
            return occupations;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find activity occupations", e);
        }
    }

    private CollectivityActivity mapRow(ResultSet rs) throws SQLException {
        CollectivityActivity a = new CollectivityActivity();
        a.setId(rs.getString("id"));
        a.setLabel(rs.getString("label"));
        a.setActivityType(rs.getString("activity_type"));
        Date execDate = rs.getDate("executive_date");
        a.setExecutiveDate(execDate != null ? execDate.toLocalDate() : null);
        a.setRecurrenceWeekOrdinal(rs.getObject("recurrence_week_ordinal") != null ? rs.getInt("recurrence_week_ordinal") : null);
        a.setRecurrenceDayOfWeek(rs.getString("recurrence_day_of_week"));
        a.setCollectivityId(rs.getString("collectivity_id"));
        a.setFederationId(rs.getString("federation_id"));
        return a;
    }
}
