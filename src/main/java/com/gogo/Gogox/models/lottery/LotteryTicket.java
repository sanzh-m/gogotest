package com.gogo.Gogox.models.lottery;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "lottery_tickets")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
public class LotteryTicket {
    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    private Boolean result;

    private Integer drawId;

    private UUID clientId;

    private Long createdAt;

    private Long lastModifiedAt;
}
