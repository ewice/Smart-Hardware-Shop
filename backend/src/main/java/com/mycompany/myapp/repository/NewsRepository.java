package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.News;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the News entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {}
