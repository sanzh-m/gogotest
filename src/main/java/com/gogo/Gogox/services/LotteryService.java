package com.gogo.Gogox.services;

import com.gogo.Gogox.models.lottery.LotteryDraw;
import com.gogo.Gogox.models.lottery.LotteryTicket;
import com.gogo.Gogox.repositories.LotteryDrawRepository;
import com.gogo.Gogox.repositories.LotteryTicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LotteryService {

    private final LotteryDrawRepository lotteryDrawRepository;
    private final LotteryTicketRepository lotteryTicketRepository;
    private final SubscriptionService subscriptionService;
    Logger logger = LoggerFactory.getLogger(LotteryService.class);

    @Autowired
    public LotteryService(LotteryDrawRepository lotteryDrawRepository, LotteryTicketRepository lotteryTicketRepository, SubscriptionService subscriptionService) {
        this.lotteryDrawRepository = lotteryDrawRepository;
        this.lotteryTicketRepository = lotteryTicketRepository;
        this.subscriptionService = subscriptionService;
    }

    @Transactional(rollbackFor = Exception.class)
    public LotteryDraw getDrawById(Integer id) {
        return this.lotteryDrawRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lottery draw was not found"));
    }

    @Transactional(rollbackFor = Exception.class)
    public List<LotteryDraw> getAllDraws() {
        return this.lotteryDrawRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public LotteryTicket getTicketById(UUID id) {
        logger.info(id.toString());
        return this.lotteryTicketRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lottery ticket was not found"));
    }

    @Transactional(rollbackFor = Exception.class)
    public List<LotteryTicket> getAllTickets() {
        return this.lotteryTicketRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public LotteryTicket saveTicket(UUID clientId) {
        Integer latestLotteryDraw = this.lotteryDrawRepository.findLargestLotteryDraw();
        LotteryTicket ticket = new LotteryTicket();
        ticket.setDrawId(latestLotteryDraw);
        ticket.setClientId(clientId);
        ticket.setCreatedAt(System.currentTimeMillis());
        ticket.setLastModifiedAt(System.currentTimeMillis());
        return this.lotteryTicketRepository.save(ticket);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer getLatestDrawId() {
        return this.lotteryDrawRepository.findLargestLotteryDraw();
    }

    @Async
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void getResults() throws IOException {
        Integer id = this.lotteryDrawRepository.findLargestLotteryDraw();
        Long lotteryTicketsNumber = this.lotteryTicketRepository.countByDrawId(id);
        if (lotteryTicketsNumber != 0) {
            Long winnerPosition = ThreadLocalRandom.current().nextLong(lotteryTicketsNumber);
            LotteryTicket winnerTicket = this.lotteryTicketRepository.findLotteryTicketByDrawIdGetAtPosition(id, winnerPosition);
            this.lotteryTicketRepository.updateLosers(id, winnerTicket.getId());
            this.lotteryTicketRepository.updateWinner(winnerTicket.getId());
            subscriptionService.notifyEmitters(id, winnerTicket.getId());
            subscriptionService.removeDrawEmitters(id);
        }
        LotteryDraw newDraw = new LotteryDraw();
        newDraw.setCreatedAt(System.currentTimeMillis());
        newDraw.setLastModifiedAt(System.currentTimeMillis());
        this.lotteryDrawRepository.save(newDraw);
    }

}
