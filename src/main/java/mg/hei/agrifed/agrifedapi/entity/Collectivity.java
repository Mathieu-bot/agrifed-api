package mg.hei.agrifed.agrifedapi.entity;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Collectivity {
    private Integer id;
    private String number;
    private String name;
    private String specialty;
    private String city;
    private String location;
    private LocalDate creationDate;
    private Integer federationId;
    private String status;
    private Integer authorizedBy;
    private LocalDate authorizationDate;
    private String rejectionReason;
    private Boolean federationApproval;
}
