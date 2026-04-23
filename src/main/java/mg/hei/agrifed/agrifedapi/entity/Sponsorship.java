package mg.hei.agrifed.agrifedapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sponsorship {
    private String id;
    private LocalDate sponsorshipDate;
    private String sponsorMemberId;
    private String sponsoredMemberId;
}