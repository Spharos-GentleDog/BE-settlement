package egenius.settlement.domain.paysettlement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Getter
@Table(name = "commission")
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "tinyint", length = 30) // 수수료 종류는 30개로 충분할 것 같음
    private Integer id;

    @Column(name = "type")
    private String Type; // 일반, VIP, VVIP 등등

    @Column(name = "commission_percentage", columnDefinition = "tinyint", length = 50) // 수수료가 50% 넘길일은 없다고 생각
    private Integer commission_percentage;


}
