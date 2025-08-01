package com.demo.ecommerce_backend.category;

import com.demo.ecommerce_backend.thirdparty.ThirdParty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    List<Category> findByParentIsNull(); // Top-level categories
    List<Category> findByParentId(Integer parentId); // Subcategories of a category
    Optional<Category> findByNameIgnoreCase(String name);
    List<Category> findByTypeAndExternalCategoryIdIsNotNull(CategoryType type);
}
