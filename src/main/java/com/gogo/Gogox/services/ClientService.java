package com.gogo.Gogox.services;

import com.gogo.Gogox.models.client.Client;
import com.gogo.Gogox.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Client createClient() {
        return this.clientRepository.save(new Client());
    }

}
