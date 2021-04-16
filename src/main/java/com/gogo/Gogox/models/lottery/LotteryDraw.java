package com.gogo.Gogox.models.lottery;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "lottery_draws")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
public class LotteryDraw {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Boolean drawn = false;

    private Long createdAt;

    private Long lastModifiedAt;

}
