package mg.hei.agrifed.agrifedapi.entity;

import lombok.*;

/**
 * Entity representing a Federation (top-level organization).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Federation {
    private Integer id;
    private String name;
}