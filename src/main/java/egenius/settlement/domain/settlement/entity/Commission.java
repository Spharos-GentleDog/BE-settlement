package egenius.settlement.domain.settlement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "tinyint", length = 30) // 수수료 종류는 30개로 충분할 것 같음
    private Integer id;

    @Column(name = "commission_percentage", columnDefinition = "tinyint", length = 50) // 수수료가 50% 넘길일은 없다고 생각
    private Integer commission_percentage;


}
