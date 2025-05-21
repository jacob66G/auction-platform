package com.example.auction_api.dao.impl;

import com.example.auction_api.dao.BidDao;
import com.example.auction_api.entity.Bid;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BidDaoImpl implements BidDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Bid> findByAuctionId(Long auctionId) {
        String query = "SELECT b FROM Bid b WHERE b.auction.id = :auctionId";

        return em.createQuery(query, Bid.class)
                .setParameter("auctionId", auctionId).getResultList();
    }

    @Override
    public List<Bid> findByUserId(Long userId) {
        String query = "SELECT b FROM Bid b WHERE b.user.id = :userId";

        return em.createQuery(query, Bid.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public Optional<Bid> findById(Long id) {
        return Optional.ofNullable(em.find(Bid.class, id));
    }

    @Override
    public Bid save(Bid bid) {
        em.persist(bid);
        return bid;
    }

    @Override
    public Bid update(Bid bid) {
        return em.merge(bid);
    }

    @Override
    public void deleteById(Long id) {
        em.remove(em.find(Bid.class, id));
    }
}
