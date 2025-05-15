package com.example.auction_api.dao.impl;

import com.example.auction_api.dao.AuctionDao;
import com.example.auction_api.dto.request.AuctionSearchCriteria;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.Category;
import com.example.auction_api.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AuctionDaoImpl implements AuctionDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Auction> findAll() {
        return em.createQuery("SELECT a FROM Auction a", Auction.class)
                .getResultList();
    }

    @Override
    public List<Auction> findByCriteria(AuctionSearchCriteria criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Auction> cq = cb.createQuery(Auction.class);
        Root<Auction> root = cq.from(Auction.class);
        Join<Auction, User> userJoin = root.join("user", JoinType.LEFT);
        Join<Auction, Category> categoryJoin = root.join("category", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if(criteria.title() != null) {
            predicates.add(cb.like(cb.lower(root.get("title")), "%"+criteria.title().toLowerCase()+"%"));
        }
        if(criteria.username() != null) {
            predicates.add(cb.like(cb.lower(userJoin.get("username")), "%"+criteria.username().toLowerCase()+"%"));
        }
        if (criteria.categoryIds() != null && !criteria.categoryIds().isEmpty()) {
            predicates.add(categoryJoin.get("id").in(criteria.categoryIds()));
        }

        if (criteria.statuses() != null && !criteria.statuses().isEmpty()) {
            predicates.add(cb.lower(root.get("auctionStatus")).in(
                    criteria.statuses().stream().map(String::toLowerCase).collect(Collectors.toList())
            ));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        //sorting
        Path<?> sortField = switch (criteria.sortBy()) {
            case "endTime" -> root.get("endTime");
            case "actualPrice" -> root.get("actualPrice");
            case "popularity" -> root.get("watcherCount");
            default -> root.get("startTime");
        };

        cq.orderBy(criteria.ascending() ? cb.asc(sortField) : cb.desc(sortField));

        TypedQuery<Auction> query = em.createQuery(cq);
        query.setFirstResult(criteria.page() * criteria.size());
        query.setMaxResults(criteria.size());

        return query.getResultList();
    }

    @Override
    public List<Auction> findByUserId(Long userId, AuctionSearchCriteria criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Auction> cq = cb.createQuery(Auction.class);
        Root<Auction> root = cq.from(Auction.class);
        root.fetch("bids", JoinType.LEFT);
        Join<Auction, User> userJoin = root.join("user", JoinType.LEFT);
        Join<Auction, Category> categoryJoin = root.join("category", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(userJoin.get("id"), userId));

        if(criteria.title() != null) {
            predicates.add(cb.like(cb.lower(root.get("title")), "%"+criteria.title().toLowerCase()+"%"));
        }
        if (criteria.categoryIds() != null && !criteria.categoryIds().isEmpty()) {
            predicates.add(categoryJoin.get("id").in(criteria.categoryIds()));
        }

        if (criteria.statuses() != null && !criteria.statuses().isEmpty()) {
            predicates.add(cb.lower(root.get("auctionStatus")).in(
                    criteria.statuses().stream().map(String::toLowerCase).collect(Collectors.toList())
            ));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        //sorting
        Path<?> sortField = switch (criteria.sortBy()) {
            case "endTime" -> root.get("endTime");
            case "actualPrice" -> root.get("actualPrice");
            case "popularity" -> root.get("watcherCount");
            default -> root.get("startTime");
        };

        cq.orderBy(criteria.ascending() ? cb.asc(sortField) : cb.desc(sortField));

        cq.distinct(true);

        TypedQuery<Auction> query = em.createQuery(cq);
        query.setFirstResult(criteria.page() * criteria.size());
        query.setMaxResults(criteria.size());

        return query.getResultList();
    }

    @Override
    public List<Auction> findByTitle(String title, Long auctionId) {
        String query = "SELECT a FROM Auction a WHERE LOWER( a.title) = LOWER(:title) AND (:auctionId is null OR a.id != :auctionId)";

        return em.createQuery(query, Auction.class)
            .setParameter("title", title)
            .setParameter("auctionId", auctionId)
            .getResultList();
    }

    @Override
    public Optional<Auction> findAuctionById(Long id) {
        return Optional.ofNullable(em.find(Auction.class, id));
    }

    @Override
    public Auction save(Auction auction) {
        em.persist(auction);
        return auction;
    }

    @Override
    public Auction update(Auction auction) {
        return em.merge(auction);
    }

    @Override
    public void deleteById(Long id) {
        em.remove(em.find(Auction.class, id));
    }
}
