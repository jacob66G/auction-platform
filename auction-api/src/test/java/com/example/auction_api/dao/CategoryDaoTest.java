package com.example.auction_api.dao;

import com.example.auction_api.dao.impl.CategoryDaoImpl;
import com.example.auction_api.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CategoryDaoImpl.class)
@ActiveProfiles("test")
public class CategoryDaoTest {

    @Autowired
    private CategoryDao categoryDao;

    @Test
    void findAll_ShouldReturnAllCategories() {
        // when
        List<Category> result = categoryDao.findAll();

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void findByName_ShouldReturnMatchingCategoriesExcludingId() {
        // given
        String name = "Electronics"; // zakładamy, że istnieje taka kategoria
        Long excludedId = 1L;

        // when
        List<Category> result = categoryDao.findByName(name, excludedId);

        // then
        assertThat(result).allMatch(c -> c.getName().equalsIgnoreCase(name) && !c.getId().equals(excludedId));
    }

    @Test
    void findById_ShouldReturnCorrectCategory() {
        // given
        Long id = 1L;

        // when
        Optional<Category> result = categoryDao.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    void save_ShouldPersistCategory() {
        // given
        Category category = new Category();
        category.setName("NewCategory");

        // when
        Category saved = categoryDao.save(category);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("NewCategory");
    }

    @Test
    void update_ShouldModifyCategoryName() {
        // given
        Category category = categoryDao.findById(1L).orElseThrow();
        String newName = "UpdatedName";
        category.setName(newName);

        // when
        Category updated = categoryDao.update(category);

        // then
        assertThat(updated.getName()).isEqualTo(newName);
    }

    @Test
    void deleteById_ShouldRemoveCategory() {
        // given
        Long id = 1L;
        assertThat(categoryDao.findById(id)).isPresent();

        // when
        categoryDao.deleteById(id);

        // then
        assertThat(categoryDao.findById(id)).isNotPresent();
    }
}
