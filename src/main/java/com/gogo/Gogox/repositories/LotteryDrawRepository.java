package com.gogo.Gogox.repositories;

import com.gogo.Gogox.models.lottery.LotteryDraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LotteryDrawRepository extends JpaRepository<LotteryDraw, Integer> {
    @Query("SELECT MAX(id) FROM LotteryDraw")
    Integer findLargestLotteryDraw();
}
