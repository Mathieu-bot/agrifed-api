package mg.hei.agrifed.agrifedapi.service.impl;

import mg.hei.agrifed.agrifedapi.dto.*;
import mg.hei.agrifed.agrifedapi.entity.MembershipFee;
import mg.hei.agrifed.agrifedapi.exception.BadRequestException;
import mg.hei.agrifed.agrifedapi.exception.NotFoundException;
import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;
import mg.hei.agrifed.agrifedapi.repository.MembershipFeeRepository;
import mg.hei.agrifed.agrifedapi.service.MembershipFeeService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MembershipFeeServiceImpl implements MembershipFeeService {

    private final MembershipFeeRepository feeRepository;
    private final CollectivityRepository collectivityRepository;

    public MembershipFeeServiceImpl(MembershipFeeRepository feeRepository,
                                    CollectivityRepository collectivityRepository) {
        this.feeRepository = feeRepository;
        this.collectivityRepository = collectivityRepository;
    }

    @Override
    public List<MembershipFeeDto> getByCollectivity(Integer collectivityId) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity not found: " + collectivityId));

        List<MembershipFee> fees = feeRepository.findByCollectivityId(collectivityId);
        List<MembershipFeeDto> result = new ArrayList<>();
        for (MembershipFee f : fees) result.add(toDto(f));
        return result;
    }

    @Override
    public List<MembershipFeeDto> createForCollectivity(Integer collectivityId, List<CreateMembershipFeeDto> dtos) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity not found: " + collectivityId));

        List<MembershipFeeDto> result = new ArrayList<>();
        for (CreateMembershipFeeDto dto : dtos) {
            if (dto.getFrequency() == null) {
                throw new BadRequestException("Frequency is required and must be one of WEEKLY, MONTHLY, ANNUALLY, PUNCTUALLY");
            }
            if (dto.getAmount() == null || dto.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Amount must be greater than 0");
            }

            MembershipFee fee = new MembershipFee();
            fee.setEligibleFrom(dto.getEligibleFrom() != null
                    ? LocalDate.parse(dto.getEligibleFrom()) : LocalDate.now());
            fee.setFrequency(dto.getFrequency().name());
            fee.setAmount(dto.getAmount());
            fee.setLabel(dto.getLabel());
            fee.setStatus(ActivityStatus.ACTIVE.name());
            fee.setCollectivityId(collectivityId);

            MembershipFee saved = feeRepository.save(fee);
            result.add(toDto(saved));
        }
        return result;
    }

    private MembershipFeeDto toDto(MembershipFee f) {
        MembershipFeeDto dto = new MembershipFeeDto();
        dto.setId(String.valueOf(f.getId()));
        dto.setEligibleFrom(f.getEligibleFrom() != null ? f.getEligibleFrom().toString() : null);
        dto.setFrequency(f.getFrequency() != null ? Frequency.valueOf(f.getFrequency()) : null);
        dto.setAmount(f.getAmount());
        dto.setLabel(f.getLabel());
        dto.setStatus(f.getStatus() != null ? ActivityStatus.valueOf(f.getStatus()) : null);
        return dto;
    }
}