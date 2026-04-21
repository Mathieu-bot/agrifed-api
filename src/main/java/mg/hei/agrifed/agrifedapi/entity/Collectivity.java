package mg.hei.agrifed.agrifedapi.entity;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class Collectivity {
    private Integer id;
    private String number;
    private String collectivityName;
    private String specialty;
    private String city;
    private Date creationDate;
}
