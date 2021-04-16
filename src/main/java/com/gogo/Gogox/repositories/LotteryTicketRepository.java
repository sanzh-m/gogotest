package com.gogo.Gogox.repositories;

import com.gogo.Gogox.models.lottery.LotteryTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LotteryTicketRepository extends JpaRepository<LotteryTicket, UUID> {
    Long countByDrawId(Integer drawId);

    @Query(value = "SELECT * FROM lottery_tickets WHERE draw_id = :drawId LIMIT 1 OFFSET :winnerPosition", nativeQuery = true)
    LotteryTicket findLotteryTicketByDrawIdGetAtPosition(@Param("drawId") Integer drawId, @Param("winnerPosition") Long winnerPosition);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE lottery_tickets SET result = FALSE WHERE draw_id = :drawId AND id != :winnerId", nativeQuery = true)
    void updateLosers(@Param("drawId") Integer drawId, @Param("winnerId") UUID winnerId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE lottery_tickets SET result = TRUE WHERE id = :winnerId", nativeQuery = true)
    void updateWinner(@Param("winnerId") UUID winnerId);
}
