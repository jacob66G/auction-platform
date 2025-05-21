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
    public List<Auction> findByCriteria(AuctionSearchCriteria criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Auction> cq = cb.createQuery(Auction.class);
        Root<Auction> root = cq.from(Auction.class);
        Join<Auction, User> userJoin = root.join("user", JoinType.LEFT);
        Join<Auction, Category> categoryJoin = root.join("category", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if(criteria.getTitle() != null) {
            predicates.add(cb.like(cb.lower(root.get("title")), "%"+criteria.getTitle().toLowerCase()+"%"));
        }
        if(criteria.getUsername() != null) {
            predicates.add(cb.like(cb.lower(userJoin.get("username")), "%"+criteria.getUsername().toLowerCase()+"%"));
        }
        if (criteria.getCategoryIds() != null && !criteria.getCategoryIds().isEmpty()) {
            predicates.add(categoryJoin.get("id").in(criteria.getCategoryIds()));
        }

        if (criteria.getStatuses() != null && !criteria.getStatuses().isEmpty()) {
            predicates.add(cb.lower(root.get("auctionStatus")).in(
                    criteria.getStatuses().stream().map(String::toLowerCase).collect(Collectors.toList())
            ));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        //sorting
        Path<?> sortField = switch (criteria.getSortBy()) {
            case "endTime" -> root.get("endTime");
            case "actualPrice" -> root.get("actualPrice");
            default -> root.get("startTime");
        };

        cq.orderBy(criteria.isAscending() ? cb.asc(sortField) : cb.desc(sortField));

        TypedQuery<Auction> query = em.createQuery(cq);
        query.setFirstResult(criteria.getPage() * criteria.getSize());
        query.setMaxResults(criteria.getSize());

        return query.getResultList();
    }

    @Override
    public List<Auction> findByUserIdAndCriteria(Long userId, AuctionSearchCriteria criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Auction> cq = cb.createQuery(Auction.class);
        Root<Auction> root = cq.from(Auction.class);
        root.fetch("bids", JoinType.LEFT);
        Join<Auction, User> userJoin = root.join("user", JoinType.LEFT);
        Join<Auction, Category> categoryJoin = root.join("category", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(userJoin.get("id"), userId));

        if(criteria.getTitle() != null) {
            predicates.add(cb.like(cb.lower(root.get("title")), "%"+criteria.getTitle().toLowerCase()+"%"));
        }
        if (criteria.getCategoryIds() != null && !criteria.getCategoryIds().isEmpty()) {
            predicates.add(categoryJoin.get("id").in(criteria.getCategoryIds()));
        }
        if (criteria.getStatuses() != null && !criteria.getStatuses().isEmpty()) {
            predicates.add(cb.lower(root.get("auctionStatus")).in(
                    criteria.getStatuses().stream().map(String::toLowerCase).collect(Collectors.toList())
            ));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        //sorting
        Path<?> sortField = switch (criteria.getSortBy()) {
            case "endTime" -> root.get("endTime");
            case "actualPrice" -> root.get("actualPrice");
            default -> root.get("startTime");
        };

        cq.orderBy(criteria.isAscending() ? cb.asc(sortField) : cb.desc(sortField));

        cq.distinct(true);

        TypedQuery<Auction> query = em.createQuery(cq);
        query.setFirstResult(criteria.getPage() * criteria.getSize());
        query.setMaxResults(criteria.getSize());

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
    public List<Auction> findByCategoryId(Long categoryId) {
        String query = "SELECT a FROM Auction a WHERE a.category.id = :categoryId";

        return em.createQuery(query, Auction.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public Optional<Auction> findById(Long id) {
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
