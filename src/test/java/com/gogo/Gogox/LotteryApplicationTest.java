package com.gogo.Gogox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogo.Gogox.models.client.Client;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasLength;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LotteryApplicationTest {


    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Participate in lottery")));

    }

    @Test
    public void shouldReturnListOfDraws() throws Exception {
        this.mockMvc.perform(get("/draws")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnSaveTicket() throws Exception {
        Client client = new Client();
        client.setId(UUID.fromString("ebea277d-28c2-4c4a-b8b2-8f9ebf69fb49"));
        this.mockMvc.perform(post("/add-ticket").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(client))).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnLotteryTicketStatus() throws Exception {
        this.mockMvc.perform(get("/get-status").param("uuid", "a6df5699-6990-4d84-b360-ced444b170d1")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")));
    }

    @Test
    public void shouldReturnClientUUID() throws Exception {
        this.mockMvc.perform(get("/get-client-id")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(hasLength(38)));
    }

}
