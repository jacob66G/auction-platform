package com.example.auction_api.service.Impl;

import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.Bid;
import com.example.auction_api.entity.User;
import com.example.auction_api.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender sender;
    private final String fromAddress;

    public EmailSenderServiceImpl(JavaMailSender sender, @Value("${spring.mail.username}") String fromAddress) {
        this.sender = sender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendAuctionWonToWinner(Auction auction, Bid winnerBid) {
        User winner = winnerBid.getUser();
        send(winner.getEmail(), "Auction win!", buildWinnerText(auction, winner, winnerBid));
    }

    @Override
    public void sendAuctionEndedToOwner(Auction auction, Bid winnerBid) {
        User owner = auction.getUser();
        send(owner.getEmail(), "Auction finished", buildAuctionFinishText(auction, winnerBid));
    }

    @Override
    public void sendAuctionExpiredToOwner(Auction auction) {
        User owner = auction.getUser();
        send(owner.getEmail(), "Auction expired", "Your auction: " + auction.getTitle() + " has expired");
    }

    @Override
    public void sendAuctionApprovalToOwner(Auction auction) {
        User owner = auction.getUser();
        send(owner.getEmail(), "Auction approval", "Your auction: " + auction.getTitle() + " has been approved");
    }

    @Override
    public void sendAuctionRejectionToOwner(Auction auction) {
        User owner = auction.getUser();
        send(owner.getEmail(), "Auction rejected", "Your auction: " + auction.getTitle() + " has been rejected");
    }

    @Override
    public void sendAuctionCancellationApprovalToOwner(Auction auction) {
        User owner = auction.getUser();
        send(owner.getEmail(), "Auction cancelled", "Your request to cancel the auction: " + auction.getTitle() + " has been approved");
    }

    @Override
    public void sendAuctionCancellationRejectionToOwner(Auction auction) {
        User owner = auction.getUser();
        send(owner.getEmail(), "Auction cancelled", "Your request to cancel the auction: " + auction.getTitle() + " has been rejected");
    }

    @Override
    public void sendAuctionCancellationToBidders(Auction auction) {
        Set<User> bidders = new HashSet<>();

        for(Bid bid : auction.getBids()) {
            bidders.add(bid.getUser());
        }

        for(User user : bidders) {
            String email = user.getEmail();
            if (email != null && !email.isBlank()) {
                send(email, "Auction cancelled", buildCancellationText(auction));
            }
        }
    }

    @Override
    public void sendUserRegistrationInfo(User user) {
        send(user.getEmail(), "Registration", "Hi " + user.getUsername() + "! " + "Your registration was successful.");
    }

    private void send(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        sender.send(msg);
    }

    private String buildAuctionFinishText(Auction auction, Bid winnerBid) {
        return String.format("Your auction: %s has been auctioned off by %s for %s.",
                auction.getTitle(), winnerBid.getUser().getUsername(), winnerBid.getAmount());
    }

    private String buildWinnerText(Auction auction, User winner, Bid winnerBid) {
        return String.format("Congrats %s! You won '%s' with bid %s.",
                winner.getUsername(), auction.getTitle(), winnerBid.getAmount());
    }

    private String buildCancellationText(Auction auction) {
        return String.format("The auction you participated in: %s has been canceled. Money will be returned to your balance",
                auction.getTitle());
    }
}
