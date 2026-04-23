package mg.hei.agrifed.agrifedapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountFull {
    private Integer id;
    private String type;
    private Integer collectivityId;
    private Integer federationId;
    private BigDecimal balance;

    private String holderName;
    private String bankName;
    private String bankCode;
    private String bankBranchCode;
    private String accountNumber;
    private String ribKey;

    private String serviceName;
    private String phoneNumber;
}