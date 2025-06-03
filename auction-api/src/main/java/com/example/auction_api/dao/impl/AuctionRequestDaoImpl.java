package com.example.auction_api.dao.impl;

import com.example.auction_api.dao.AuctionRequestDao;
import com.example.auction_api.dto.request.AuctionRequestCriteria;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.AuctionRequest;
import com.example.auction_api.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AuctionRequestDaoImpl implements AuctionRequestDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<AuctionRequest> findByCriteria(AuctionRequestCriteria criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AuctionRequest> cq = cb.createQuery(AuctionRequest.class);
        Root<AuctionRequest> root = cq.from(AuctionRequest.class);
        root.fetch("auction", JoinType.LEFT);
        root.fetch("requestedBy", JoinType.LEFT);
        root.fetch("moderatedBy", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if(criteria.getTypes() != null && !criteria.getTypes().isEmpty()) {
            predicates.add(cb.lower(root.get("requestType")).in(
                    criteria.getTypes().stream().map(String::toLowerCase).collect(Collectors.toList())
            ));
        }
        if(criteria.getStatuses() != null && !criteria.getStatuses().isEmpty()) {
            predicates.add(cb.lower(root.get("requestStatus")).in(
                    criteria.getStatuses().stream().map(String::toLowerCase).collect(Collectors.toList())
            ));
        }
        if(criteria.getRequestDate() != null) {
            LocalDate targetDate = criteria.getRequestDate().toLocalDate();

            LocalDateTime startOfDay = targetDate.atStartOfDay();
            LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

            predicates.add(cb.between(root.get("requestDate"), startOfDay, endOfDay));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        Path<?> sortPath = root.get(criteria.getSortBy());
        cq.orderBy(criteria.isAscending() ? cb.asc(sortPath) : cb.desc(sortPath));

        TypedQuery<AuctionRequest> query = em.createQuery(cq);

        int page = criteria.getPage();
        int size = criteria.getSize();
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public Optional<AuctionRequest> findById(Long id) {
        return Optional.ofNullable(em.find(AuctionRequest.class, id));
    }

    @Override
    public AuctionRequest save(AuctionRequest request) {
        em.persist(request);
        return request;
    }

    @Override
    public void delete(AuctionRequest auctionRequest) {
        em.remove(auctionRequest);
    }
}
