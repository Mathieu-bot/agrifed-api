package mg.hei.agrifed.agrifedapi.service.impl;

import lombok.AllArgsConstructor;
import mg.hei.agrifed.agrifedapi.dto.CollectivityLocalStatisticsDto;
import mg.hei.agrifed.agrifedapi.dto.CollectivityOverallStatisticsDto;
import mg.hei.agrifed.agrifedapi.exception.BadRequestException;
import mg.hei.agrifed.agrifedapi.exception.NotFoundException;
import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;
import mg.hei.agrifed.agrifedapi.repository.StatisticsRepository;
import mg.hei.agrifed.agrifedapi.service.StatisticsService;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final CollectivityRepository collectivityRepository;

    @Override
    public List<CollectivityLocalStatisticsDto> getLocalStatistics(
            String collectivityId, LocalDate from, LocalDate to) {

        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity not found: " + collectivityId));

        if (from == null || to == null) throw new BadRequestException("'from' and 'to' are required");
        if (from.isAfter(to)) throw new BadRequestException("'from' must be before 'to'");

        return statisticsRepository.findLocalStatistics(collectivityId, from, to);
    }

    @Override
    public List<CollectivityOverallStatisticsDto> getOverallStatistics(LocalDate from, LocalDate to) {
        if (from == null || to == null) throw new BadRequestException("'from' and 'to' are required");
        if (from.isAfter(to)) throw new BadRequestException("'from' must be before 'to'");

        return statisticsRepository.findOverallStatistics(from, to);
    }
}