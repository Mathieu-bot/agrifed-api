package mg.hei.agrifed.agrifedapi.repository.impl;

import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.dto.*;
import mg.hei.agrifed.agrifedapi.exception.DatabaseException;
import mg.hei.agrifed.agrifedapi.repository.StatisticsRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcStatisticsRepositoryImpl implements StatisticsRepository {

    private final DataSource dataSource;

    public JdbcStatisticsRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<CollectivityLocalStatisticsDto> findLocalStatistics(
            String collectivityId, LocalDate from, LocalDate to) {

        String sql = """
        SELECT
            m.id                                        AS member_id,
            m.firstname                                 AS first_name,
            m.lastname                                  AS last_name,
            m.email                                     AS email,
            p.label                                     AS occupation,
            COALESCE(SUM(c.amount), 0)                  AS earned_amount,
            COALESCE(
                (
                    SELECT SUM(mf.amount)
                    FROM membership_fee mf
                    WHERE mf.collectivity_id = ?
                      AND mf.status          = 'ACTIVE'
                      AND (mf.eligible_from IS NULL OR mf.eligible_from <= ?)
                      AND NOT EXISTS (
                          SELECT 1
                          FROM contribution c2
                          WHERE c2.member_id         = m.id
                            AND c2.membership_fee_id = mf.id
                            AND c2.creation_date     BETWEEN ? AND ?
                      )
                ), 0
            ) AS unpaid_amount
        FROM member m
        INNER JOIN membership_history mh
            ON  mh.member_id       = m.id
            AND mh.collectivity_id = ?
            AND mh.end_date        IS NULL
        LEFT JOIN (
            SELECT DISTINCT ON (ct.member_id)
                ct.member_id,
                p2.label
            FROM collectivity_term ct
            JOIN position p2 ON p2.id = ct.position_id
            WHERE ct.collectivity_id = ?
            ORDER BY ct.member_id, ct.year DESC
        ) p ON p.member_id = m.id
        LEFT JOIN contribution c
            ON  c.member_id       = m.id
            AND c.collectivity_id = ?
            AND c.creation_date   BETWEEN ? AND ?

        GROUP BY m.id, m.firstname, m.lastname, m.email, p.label
        ORDER BY m.lastname, m.firstname
        """;

        List<CollectivityLocalStatisticsDto> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collectivityId);
            stmt.setDate(2, Date.valueOf(to));
            stmt.setDate(3, Date.valueOf(from));
            stmt.setDate(4, Date.valueOf(to));

            stmt.setString(5, collectivityId);
            stmt.setString(6, collectivityId);
            stmt.setString(7, collectivityId);
            stmt.setDate(8, Date.valueOf(from));
            stmt.setDate(9, Date.valueOf(to));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MemberDescriptionDto memberDesc = new MemberDescriptionDto(
                        rs.getString("member_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("occupation")
                );
                result.add(new CollectivityLocalStatisticsDto(
                        memberDesc,
                        rs.getBigDecimal("earned_amount"),
                        rs.getBigDecimal("unpaid_amount")
                ));
            }
            return result;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to compute local statistics", e);
        }
    }

    @Override
    public List<CollectivityOverallStatisticsDto> findOverallStatistics(LocalDate from, LocalDate to) {

        String sql = """
        SELECT
            c.name AS col_name,
            c.number AS col_number,

            COUNT(DISTINCT CASE
                WHEN m.membership_date BETWEEN ? AND ?
                THEN m.id
            END)                                            AS new_members_number,

            COUNT(DISTINCT mh.member_id)                    AS total_members,

            COUNT(DISTINCT CASE
                WHEN NOT EXISTS (
                    SELECT 1
                    FROM membership_fee mf
                    WHERE mf.collectivity_id = c.id
                      AND mf.status          = 'ACTIVE'
                      AND (mf.eligible_from IS NULL OR mf.eligible_from <= ?)
                      AND NOT EXISTS (
                          SELECT 1
                          FROM contribution con
                          WHERE con.member_id         = mh.member_id
                            AND con.membership_fee_id = mf.id
                            AND con.creation_date     BETWEEN ? AND ?
                      )
                )
                THEN mh.member_id
            END)                                            AS members_current

        FROM collectivity c
        INNER JOIN membership_history mh
            ON  mh.collectivity_id = c.id
            AND mh.end_date        IS NULL
        INNER JOIN member m
            ON  m.id = mh.member_id

        GROUP BY c.id, c.name, c.number
        ORDER BY c.name
        """;

        List<CollectivityOverallStatisticsDto> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(from)); // membership_date BETWEEN from
            stmt.setDate(2, Date.valueOf(to));   // membership_date AND to
            stmt.setDate(3, Date.valueOf(to));   // mf.eligible_from <= to
            stmt.setDate(4, Date.valueOf(from)); // con.creation_date BETWEEN from
            stmt.setDate(5, Date.valueOf(to));   // con.creation_date AND to

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int total   = rs.getInt("total_members");
                int current = rs.getInt("members_current");

                BigDecimal percentage = total == 0
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(current * 100.0 / total)
                          .setScale(2, java.math.RoundingMode.HALF_UP);

                result.add(new CollectivityOverallStatisticsDto(
                        new CollectivityInformationShortDto(
                                rs.getString("col_name"),
                                rs.getString("col_number")
                        ),
                        rs.getInt("new_members_number"),
                        percentage
                ));
            }
            return result;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to compute overall statistics", e);
        }
    }
}