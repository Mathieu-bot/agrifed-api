package mg.hei.agrifed.agrifedapi.service;

import mg.hei.agrifed.agrifedapi.dto.CollectivityLocalStatisticsDto;
import mg.hei.agrifed.agrifedapi.dto.CollectivityOverallStatisticsDto;
import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    List<CollectivityLocalStatisticsDto> getLocalStatistics(String collectivityId, LocalDate from, LocalDate to);
    List<CollectivityOverallStatisticsDto> getOverallStatistics(LocalDate from, LocalDate to);
}