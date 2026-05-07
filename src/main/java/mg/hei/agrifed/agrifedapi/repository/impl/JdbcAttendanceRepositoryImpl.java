package mg.hei.agrifed.agrifedapi.repository.impl;

import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.entity.ActivityMemberAttendance;
import mg.hei.agrifed.agrifedapi.exception.DatabaseException;
import mg.hei.agrifed.agrifedapi.repository.AttendanceRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAttendanceRepositoryImpl implements AttendanceRepository {

    private final DataSource dataSource;

    public JdbcAttendanceRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<ActivityMemberAttendance> findAllByActivityId(String activityId) {
        String sql = "SELECT id, occurrence_date, status, is_external, member_id, activity_id FROM attendance WHERE activity_id = ?";
        List<ActivityMemberAttendance> attendances = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, activityId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                attendances.add(mapRow(rs));
            }
            return attendances;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find attendances by activity id", e);
        }
    }

    @Override
    public Optional<ActivityMemberAttendance> findByActivityIdAndMemberId(String activityId, String memberId) {
        String sql = "SELECT id, occurrence_date, status, is_external, member_id, activity_id FROM attendance WHERE activity_id = ? AND member_id = ? ORDER BY occurrence_date DESC LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, activityId);
            stmt.setString(2, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find attendance by activity and member id", e);
        }
    }

    @Override
    public List<ActivityMemberAttendance> findAllByActivityIdAndIsExternal(String activityId, boolean isExternal) {
        String sql = "SELECT id, occurrence_date, status, is_external, member_id, activity_id FROM attendance WHERE activity_id = ? AND is_external = ?";
        List<ActivityMemberAttendance> attendances = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, activityId);
            stmt.setBoolean(2, isExternal);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                attendances.add(mapRow(rs));
            }
            return attendances;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find attendances by activity and external status", e);
        }
    }

    @Override
    public List<ActivityMemberAttendance> findAllByCollectivityId(String collectivityId) {
        String sql = "SELECT at.id, at.occurrence_date, at.status, at.is_external, at.member_id, at.activity_id " +
                "FROM attendance at " +
                "INNER JOIN activity a ON at.activity_id = a.id " +
                "WHERE a.collectivity_id = ?";
        List<ActivityMemberAttendance> attendances = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectivityId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                attendances.add(mapRow(rs));
            }
            return attendances;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find attendances by collectivity id", e);
        }
    }

    @Override
    public ActivityMemberAttendance save(ActivityMemberAttendance attendance) {
        if (attendance.getId() == null || attendance.getId().isBlank()) {
            attendance.setId("att-" + java.util.UUID.randomUUID().toString().substring(0, 8));
        }
        String sql = "INSERT INTO attendance (id, occurrence_date, status, is_external, member_id, activity_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, attendance.getId());
            stmt.setDate(2, attendance.getOccurrenceDate() != null ? Date.valueOf(attendance.getOccurrenceDate()) : null);
            stmt.setString(3, attendance.getStatus());
            stmt.setBoolean(4, attendance.getIsExternal() != null && attendance.getIsExternal());
            stmt.setString(5, attendance.getMemberId());
            stmt.setString(6, attendance.getActivityId());
            stmt.executeUpdate();
            return attendance;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save attendance", e);
        }
    }

    @Override
    public List<ActivityMemberAttendance> saveAll(List<ActivityMemberAttendance> attendances) {
        for (ActivityMemberAttendance a : attendances) {
            save(a);
        }
        return attendances;
    }

    @Override
    public boolean existsByActivityIdAndMemberIdAndStatusIn(String activityId, String memberId, List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) return false;
        StringBuilder sql = new StringBuilder("SELECT COUNT(id) FROM attendance WHERE activity_id = ? AND member_id = ? AND status IN (");
        for (int i = 0; i < statuses.size(); i++) {
            sql.append(i > 0 ? ",?" : "?");
        }
        sql.append(")");
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setString(1, activityId);
            stmt.setString(2, memberId);
            for (int i = 0; i < statuses.size(); i++) {
                stmt.setString(i + 3, statuses.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check attendance existence", e);
        }
    }

    private ActivityMemberAttendance mapRow(ResultSet rs) throws SQLException {
        ActivityMemberAttendance a = new ActivityMemberAttendance();
        a.setId(rs.getString("id"));
        Date occDate = rs.getDate("occurrence_date");
        a.setOccurrenceDate(occDate != null ? occDate.toLocalDate() : null);
        a.setStatus(rs.getString("status"));
        a.setIsExternal(rs.getBoolean("is_external"));
        a.setMemberId(rs.getString("member_id"));
        a.setActivityId(rs.getString("activity_id"));
        return a;
    }
}
