package mg.hei.agrifed.agrifedapi.mapper;

import mg.hei.agrifed.agrifedapi.dto.ActivityStatus;
import mg.hei.agrifed.agrifedapi.dto.Frequency;
import mg.hei.agrifed.agrifedapi.dto.MembershipFeeDto;
import mg.hei.agrifed.agrifedapi.entity.MembershipFee;

public class MembershipFeeMapper {

    public MembershipFeeDto toDto(MembershipFee fee) {
        MembershipFeeDto dto = new MembershipFeeDto();
        dto.setId(fee.getId());
        dto.setEligibleFrom(fee.getEligibleFrom() != null ? fee.getEligibleFrom().toString() : null);
        dto.setFrequency(toFrequencyDto(fee.getFrequency()));
        dto.setAmount(fee.getAmount());
        dto.setLabel(fee.getLabel());
        dto.setStatus(toStatusDto(fee.getStatus()));
        return dto;
    }

    private Frequency toFrequencyDto(String frequency) {
        if (frequency == null || frequency.isBlank()) return null;
        try {
            return Frequency.valueOf(frequency.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private ActivityStatus toStatusDto(String status) {
        if (status == null || status.isBlank()) return null;
        try {
            return ActivityStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}