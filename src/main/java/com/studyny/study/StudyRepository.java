package com.studyny.study;

import com.studyny.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {

    boolean existsByPath(String path);

    // EntityGraph 로 명시한것은 eagar모드로, 나머지는 기본 fetch 타입. ( one으로 끝나는건 eagar, many로 끝나는건 lazy)
    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);
}
