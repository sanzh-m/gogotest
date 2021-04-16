package com.gogo.Gogox.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionService {
    private final HashMap<Integer, List<SseEmitter>> emitters;

    @Autowired
    public SubscriptionService() {
        emitters = new HashMap<>();
    }

    public void addEmitter(Integer drawId, final SseEmitter emitter) {
        List<SseEmitter> drawEmitter = emitters.computeIfAbsent(drawId, k -> new ArrayList<>());
        drawEmitter.add(emitter);
    }

    public void removeEmitter(Integer drawId, final SseEmitter emitter) {
        List<SseEmitter> drawEmitter = emitters.get(drawId);
        if (drawEmitter != null) drawEmitter.remove(emitter);
    }

    public void removeDrawEmitters(Integer drawId) {
        emitters.remove(drawId);
    }

    public void notifyEmitters(Integer drawId, UUID winner) throws IOException {
        List<SseEmitter> drawEmitters = emitters.get(drawId);
        if (drawEmitters != null) drawEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().data(winner));
            } catch (Exception ignored) {
            }
        });
    }

}
