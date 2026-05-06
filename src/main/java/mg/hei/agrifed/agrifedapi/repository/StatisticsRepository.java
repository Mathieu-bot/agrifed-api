package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.dto.CollectivityLocalStatisticsDto;
import mg.hei.agrifed.agrifedapi.dto.CollectivityOverallStatisticsDto;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsRepository {
    List<CollectivityLocalStatisticsDto> findLocalStatistics(String collectivityId, LocalDate from, LocalDate to);
    List<CollectivityOverallStatisticsDto> findOverallStatistics(LocalDate from, LocalDate to);
}