package com.example.auction_api.event.user;

import com.example.auction_api.service.EmailSenderService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private final EmailSenderService senderService;

    public UserEventListener(EmailSenderService senderService) {
        this.senderService = senderService;
    }

    @EventListener
    public void handleUserRegistrationEvent(UserRegistrationEvent event) {
        senderService.sendUserRegistrationInfo(event.getUserEmail(), event.getUsername());
    }
}
