package mg.hei.agrifed.agrifedapi.entity;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Collectivity {
    private String id;
    private String number;
    private String name;
    private String specialty;
    private String city;
    private String location;
    private LocalDate creationDate;
    private String federationId;
    private String status;
    private String authorizedBy;
    private LocalDate authorizationDate;
    private String rejectionReason;
    private Boolean federationApproval;
}
