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
        String sql = "SELECT id, status, is_external, member_id, activity_id FROM attendance WHERE activity_id = ?";
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
        String sql = "SELECT id, status, is_external, member_id, activity_id FROM attendance WHERE activity_id = ? AND member_id = ?";
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
        String sql = "SELECT id, status, is_external, member_id, activity_id FROM attendance WHERE activity_id = ? AND is_external = ?";
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
    public ActivityMemberAttendance save(ActivityMemberAttendance attendance) {
        if (attendance.getId() == null || attendance.getId().isBlank()) {
            attendance.setId("att-" + java.util.UUID.randomUUID().toString().substring(0, 8));
        }
        String sql = "INSERT INTO attendance (id, status, is_external, member_id, activity_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, attendance.getId());
            stmt.setString(2, attendance.getStatus());
            stmt.setBoolean(3, attendance.getIsExternal() != null && attendance.getIsExternal());
            stmt.setString(4, attendance.getMemberId());
            stmt.setString(5, attendance.getActivityId());
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
    public boolean existsByActivityIdAndMemberId(String activityId, String memberId) {
        String sql = "SELECT COUNT(id) FROM attendance WHERE activity_id = ? AND member_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, activityId);
            stmt.setString(2, memberId);
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
        a.setStatus(rs.getString("status"));
        a.setIsExternal(rs.getBoolean("is_external"));
        a.setMemberId(rs.getString("member_id"));
        a.setActivityId(rs.getString("activity_id"));
        return a;
    }
}
