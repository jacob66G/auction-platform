package com.example.auction_api.service.Impl;

import com.example.auction_api.service.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender sender;
    private final String fromAddress;

    public EmailSenderServiceImpl(JavaMailSender sender, @Value("${MAIL_USERNAME}") String fromAddress) {
        this.sender = sender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendAuctionWonToWinner(String userEmail, String auctionTitle, String winnerName, BigDecimal winnerBidAmount) {
        send(userEmail, "Auction win!", buildWinnerText(auctionTitle, winnerName, winnerBidAmount));
    }

    @Override
    public void sendAuctionEndedToOwner(String userEmail, String auctionTitle, String winnerName, BigDecimal winnerBidAmount) {
        send(userEmail, "Auction finished", buildAuctionFinishText(auctionTitle, winnerName, winnerBidAmount));
    }

    @Override
    public void sendAuctionExpiredToOwner(String userEmail, String auctionTitle) {
        send(userEmail, "Auction expired", "Your auction: " + auctionTitle + " has expired");
    }

    @Override
    public void sendAuctionApprovalToOwner(String userEmail, String auctionTitle) {
        send(userEmail, "Auction approval", "Your auction: " + auctionTitle + " has been approved");
    }

    @Override
    public void sendAuctionRejectToOwner(String userEmail, String auctionTitle, String comment) {
        send(userEmail, "Auction rejected", buildDecisionText(auctionTitle, comment, "save", "reject"));
    }

    @Override
    public void sendAuctionCancelApprovalToOwner(String userEmail, String auctionTitle, String comment) {
        send(userEmail, "Auction cancelled", buildDecisionText(auctionTitle, comment, "cancel", "approve"));
    }

    @Override
    public void sendAuctionCancelRejectionToOwner(String userEmail, String auctionTitle, String comment) {
        send(userEmail, "Auction cancelled", buildDecisionText(auctionTitle, comment, "cancel", "reject"));
    }

    @Override
    public void sendUserRegistrationInfo(String userEmail, String username) {
        send(userEmail, "Registration", "Hi " + username + "! " + "Your registration was successful.");
    }

    @Override
    public void sendRefundInfoToBidder(String userEmail, String auctionTitle, BigDecimal amount) {
        send(userEmail, "Refund", buildRefundText(amount, auctionTitle));
    }

    private void send(String to, String subject, String text) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);

            log.info("✉ Sending email to={} subject='{}'", to, subject);
            sender.send(msg);
            log.info("✅ Email sent to={}", to);

        } catch (Exception e) {
            log.error("Failed to send email to={} subject='{}': {}", to, subject, e.getMessage(), e);
        }
    }

    private String buildAuctionFinishText(String auctionTitle,String winnerName, BigDecimal winnerBidAmount) {
        return String.format("Your auction: %s has been auctioned off by %s for %s.",
                auctionTitle, winnerName, winnerBidAmount);
    }

    private String buildWinnerText(String auctionTitle, String winnerName, BigDecimal winnerBidAmount) {
        return String.format("Congrats %s! You won '%s' with bid %s.",
                winnerName, auctionTitle, winnerBidAmount);
    }

    private String buildRefundText(BigDecimal amount, String auctionTitle) {
        return String.format("Auction: " + auctionTitle + " was cancelled. You received a return of funds from your bids. Refund: " + amount);
    }

    private String buildDecisionText(String auctionTitle, String comment, String action, String decision) {
        String actionText = switch (action) {
            case "cancel" -> "Your request to cancel the auction";
            case "save" -> "Your auction submission";
            default -> "Your auction request";
        };

        String decisionText = switch (decision) {
            case "approve" -> "has been approved.";
            case "reject" -> "has been rejected.";
            default -> "has been processed.";
        };

        String baseMessage = String.format("%s \"%s\" %s", actionText, auctionTitle, decisionText);

        if (comment != null && !comment.isBlank()) {
            baseMessage += " Moderator's comment: " + comment;
        }

        return baseMessage;
    }
}
