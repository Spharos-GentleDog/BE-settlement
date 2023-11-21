package egenius.settlement.domain.paysettlement.entity;

import egenius.settlement.domain.paysettlement.entity.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "before_settlement")
public class BeforeSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime paidAt;

    private PaymentMethod paymentMethod;

    private String vendorEmail;

    private String productName;

    private String productCode;

    private String productMainImageUrl;

    private Integer productAmount;

    private Integer count;
}
