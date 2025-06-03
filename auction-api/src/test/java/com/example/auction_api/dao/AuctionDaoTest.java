package com.example.auction_api.dao;

import com.example.auction_api.dao.impl.AuctionDaoImpl;
import com.example.auction_api.dao.impl.CategoryDaoImpl;
import com.example.auction_api.dao.impl.UserDaoImpl;
import com.example.auction_api.enums.AuctionStatus;
import com.example.auction_api.dto.request.AuctionSearchCriteria;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.Category;
import com.example.auction_api.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import({AuctionDaoImpl.class, UserDaoImpl.class, CategoryDaoImpl.class})
@ActiveProfiles("test")
public class AuctionDaoTest {

    @Autowired
    private AuctionDao auctionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CategoryDao categoryDao;


    @Test
    void findByCriteria_ShouldFindByTitleReturnMatchingAuctions() {
        //given
        String searchedTitle = "Leather";
        String titleInDb = "Leather Jacket";

        AuctionSearchCriteria criteria = AuctionSearchCriteria.builder()
                .title(searchedTitle)
                .sortBy("startTime")
                .ascending(false)
                .page(0)
                .size(10)
                .build();
        //when
        List<Auction> result = auctionDao.findByCriteria(criteria);

        result.forEach(auction -> {
            System.out.println(auction.getId());
        });

        //then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo(titleInDb);
    }

    @Test
    void findByCriteria_ShouldFilterByCategoryAndSortingByPriceDSCReturnMatchingAuctions() {
        //given
        List<Integer> categoriesId = Arrays.asList(1, 2);

        Auction auction1 = auctionDao.findById(1L).get();
        Auction auction2 = auctionDao.findById(2L).get();
        Auction auction3 = auctionDao.findById(3L).get();
        Auction auction4 = auctionDao.findById(4L).get();

        List<Auction> expectedAuctions = Arrays.asList(auction4, auction3, auction2, auction1);
        AuctionSearchCriteria criteria = AuctionSearchCriteria.builder()
                .categoryIds(categoriesId)
                .sortBy("actualPrice")
                .ascending(false)
                .page(0)
                .size(10)
                .build();

        //when
        List<Auction> result = auctionDao.findByCriteria(criteria);

        //then
        assertThat(result).hasSize(4);
        assertEquals(expectedAuctions, result);
    }

    @Test
    void findByUserId_ShouldApplyCriteriaAndFilterByUserIdAndCriteria() {
        //given
        Long userId = 2L;
        Auction auction1 = auctionDao.findById(1L).get();
        Auction auction2 = auctionDao.findById(4L).get();
        List<Auction> expectedAuctions = Arrays.asList(auction1, auction2);

        AuctionSearchCriteria criteria = AuctionSearchCriteria.builder()
                .sortBy("startTime")
                .ascending(true)
                .page(0)
                .size(10)
                .build();

        //when
        List<Auction> result = auctionDao.findByUserIdAndCriteria(userId, criteria);

        //then
        assertThat(result).hasSize(2);
        assertEquals(expectedAuctions, result);
    }

    @Test
    void findByTitle_ShouldReturnMatchingAuctions() {
        // given
        String searchedTitle = "Leather Jacket";

        // when
        List<Auction> result = auctionDao.findByTitle(searchedTitle, null);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(searchedTitle);
    }

    @Test
    void findByTitle_ShouldExcludeSpecificAuctionId() {
        // given
        String searchedTitle = "iPhone 14";
        Long excludedAuctionId = 1L;

        // when
        List<Auction> result = auctionDao.findByTitle(searchedTitle, excludedAuctionId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void findByTitle_ShouldReturnEmptyList_WhenNoMatches() {
        // given
        String searchedTitle = "Non-existent Item";

        // when
        List<Auction> result = auctionDao.findByTitle(searchedTitle, null);

        // then
        assertThat(result).isEmpty();
    }


    @Test
    void findByCategoryId_ShouldReturnElectronicsAuctions() {
        // given
        Long electronicsCategory = 1L;

        // when
        List<Auction> result = auctionDao.findByCategoryId(electronicsCategory);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Auction::getTitle)
                .containsExactlyInAnyOrder("iPhone 14", "Samsung Galaxy S22", "MacBook Pro 13\"");
    }

    @Test
    void findByCategoryId_ShouldReturnEmptyList_WhenNoCategoryExists() {
        // given
        Long nonExistentCategory = 999L;

        // when
        List<Auction> result = auctionDao.findByCategoryId(nonExistentCategory);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void findById_ShouldReturnAuction_WhenExists() {
        // given
        Long auctionId = 1L;

        // when
        Optional<Auction> result = auctionDao.findById(auctionId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("iPhone 14");
        assertThat(result.get().getStartingPrice()).isEqualByComparingTo(BigDecimal.valueOf(300.00));
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        //given
        Long auctionId = 999L;

        //when
        Optional<Auction> result = auctionDao.findById(auctionId);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void save_ShouldPersistAuction() {
        //given
        User user = userDao.findById(1L).get();
        Category category = categoryDao.findById(1L).get();

        Auction auction = new Auction();
        auction.setTitle("New Auction");
        auction.setDescription("This is a new auction");
        auction.setStartingPrice(BigDecimal.valueOf(100));
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(LocalDateTime.now().plusDays(7));
        auction.setAuctionStatus(AuctionStatus.PENDING_APPROVAL);
        auction.setActualPrice(BigDecimal.valueOf(100));
        auction.setUser(user);
        auction.setCategory(category);

        //when
        Auction savedAuction = auctionDao.save(auction);

        //then
        assertThat(savedAuction).isNotNull();
        assertThat(savedAuction.getId()).isNotNull();
    }

    @Test
    void update_ShouldMergeAuction() {
        //given
        Auction auction = auctionDao.findById(1L).get();
        auction.setTitle("Updated Title");
        auction.setDescription("Updated Description");

        //when
        Auction updatedAuction = auctionDao.update(auction);

        //then
        assertThat(updatedAuction).isNotNull();
        assertThat(updatedAuction.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedAuction.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void deleteById_ShouldRemoveAuction() {
        //given
        Long auctionId = 1L;

        //when
        auctionDao.deleteById(auctionId);
        Optional<Auction> result = auctionDao.findById(auctionId);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void findByEndTimeBefore_ShouldReturnActiveAuctionsEndingBefore() {
        // given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(20);

        // when
        List<Auction> result = auctionDao.findByEndTimeBefore(futureTime);

        // then
        assertThat(result).hasSize(7);
        assertThat(result).allMatch(auction -> auction.getAuctionStatus() == AuctionStatus.ACTIVE);
        assertThat(result).allMatch(auction -> auction.getEndTime().isBefore(futureTime));
    }

    @Test
    void findByEndTimeBefore_ShouldReturnEmptyList_WhenNoAuctionsEndBefore() {
        // given
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);

        // when
        List<Auction> result = auctionDao.findByEndTimeBefore(pastTime);

        // then
        assertThat(result).isEmpty();
    }
}
