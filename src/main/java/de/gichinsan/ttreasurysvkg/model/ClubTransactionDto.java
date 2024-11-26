package de.gichinsan.ttreasurysvkg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class ClubTransactionDto {

    @Id
    @Column(name = "trans_id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @NotNull
    private String amount;

    @NotNull
    private String date;

    private String description;

}
