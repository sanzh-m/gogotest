package com.gogo.Gogox.controllers;

import com.gogo.Gogox.models.client.Client;
import com.gogo.Gogox.models.lottery.LotteryDraw;
import com.gogo.Gogox.models.lottery.LotteryTicket;
import com.gogo.Gogox.services.ClientService;
import com.gogo.Gogox.services.LotteryService;
import com.gogo.Gogox.services.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
public class LotteryController {
    private final LotteryService lotteryService;
    private final ClientService clientService;
    private final SubscriptionService subscriptionService;
    Logger logger = LoggerFactory.getLogger(LotteryController.class);

    @Autowired
    public LotteryController(LotteryService lotteryService, ClientService clientService, SubscriptionService subscriptionService) {
        this.lotteryService = lotteryService;
        this.clientService = clientService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("")
    public String greeting() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <title>Spring SSE Push Notifications</title>\n" +
                "    <script>\n" +
                "        function initialize() {\n" +
                "            if (localStorage.getItem('clientId') == null) {\n" +
                "                function reqListener () {\n" +
                "                    localStorage.setItem('clientId', JSON.parse(this.responseText));\n" +
                "                }\n" +
                "\n" +
                "                const clientIdReq = new XMLHttpRequest();\n" +
                "                clientIdReq.addEventListener(\"load\", reqListener);\n" +
                "                clientIdReq.open(\"GET\", \"/get-client-id\");\n" +
                "                clientIdReq.send();\n" +
                "            }\n" +
                "        }\n" +
                "        window.onload = initialize;\n" +
                "    </script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<button id=\"button\">Participate in lottery</button>\n" +
                "<div id=\"winnerOrNot\"></div>\n" +
                "<script>\n" +
                "    document.getElementById(\"button\").addEventListener('click', () => {\n" +
                "        function reqListener () {\n" +
                "            localStorage.setItem('currentTicket', this.responseText);\n" +
                "            console.log(this.responseText);\n" +
                "        }\n" +
                "\n" +
                "        const getTicket = new XMLHttpRequest();\n" +
                "        getTicket.addEventListener(\"load\", reqListener);\n" +
                "        getTicket.open(\"POST\", \"/add-ticket\", true);\n" +
                "        getTicket.setRequestHeader(\"Content-Type\", \"application/json;charset=UTF-8\");\n" +
                "        getTicket.send(JSON.stringify({ \"id\": localStorage.getItem('clientId') }));\n" +
                "        const eventSource = new EventSource('/subscribeToResults');\n" +
                "        eventSource.onmessage = e => {\n" +
                "            alert(JSON.parse(e.data) === JSON.parse(localStorage.getItem('currentTicket')).id);\n" +
                "            document.getElementById(\"winnerOrNot\").innerHTML = JSON.parse(e.data) === JSON.parse(localStorage.getItem('currentTicket')).id ? 'Winner' : 'Better luck next time :(';\n" +
                "            eventSource.close();\n" +
                "        };\n" +
                "        eventSource.onopen = e => console.log('open');\n" +
                "        eventSource.onerror = e => {\n" +
                "            if (e.readyState === EventSource.CLOSED) {\n" +
                "                console.log('close');\n" +
                "            }\n" +
                "            else {\n" +
                "                console.log(e);\n" +
                "            }\n" +
                "        };\n" +
                "        eventSource.addEventListener('second', function(e) {\n" +
                "            console.log('second', e.data);\n" +
                "        }, false);\n" +
                "    });\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>\n";
    }

    @GetMapping("/draws")
    public ResponseEntity<List<LotteryDraw>> getAllDraws() {
        return new ResponseEntity<>(this.lotteryService.getAllDraws(), HttpStatus.OK);
    }

    @PostMapping("/add-ticket")
    public ResponseEntity<LotteryTicket> saveTicket(@RequestBody Client client) {
        return new ResponseEntity<>(this.lotteryService.saveTicket(client.getId()), HttpStatus.OK);
    }

    @GetMapping("/get-status")
    public ResponseEntity<String> getTicketStatus(@RequestParam UUID uuid) {
        LotteryTicket lotteryTicket = this.lotteryService.getTicketById(uuid);
        String result;
        if (lotteryTicket.getResult() == null) result = "The lottery wasn't drawn yet";
        else if (lotteryTicket.getResult()) result = "You are the winner!";
        else result = "You lost this time. Good luck next time!";
        logger.info(result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/get-client-id")
    public ResponseEntity<UUID> getClientId() {
        return new ResponseEntity<>(this.clientService.createClient().getId(), HttpStatus.OK);
    }

    @GetMapping(path = "/subscribeToResults", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe() throws IOException {
        final SseEmitter emitter = new SseEmitter(120_000L);
        Integer drawId = this.lotteryService.getLatestDrawId();
        logger.info(drawId.toString());
        subscriptionService.addEmitter(drawId, emitter);
        emitter.onCompletion(() -> subscriptionService.removeEmitter(drawId, emitter));
        emitter.onTimeout(() -> subscriptionService.removeEmitter(drawId, emitter));
        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }
}
