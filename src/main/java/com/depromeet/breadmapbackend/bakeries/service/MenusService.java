package com.depromeet.breadmapbackend.bakeries.service;

import com.depromeet.breadmapbackend.bakeries.domain.BreadCategories;
import com.depromeet.breadmapbackend.bakeries.dto.MenuListResponse;
import com.depromeet.breadmapbackend.bakeries.repository.BreadCategoriesQuerydslRepository;
import com.depromeet.breadmapbackend.bakeries.repository.MenusQuerydslRepository;
import com.depromeet.breadmapbackend.common.enumerate.BreadCategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenusService {

    private final BreadCategoriesQuerydslRepository breadCategoriesQuerydslRepository;
    private final MenusQuerydslRepository menusQuerydslRepository;

    @Transactional(readOnly = true)
    public MenuListResponse getMenuList(Long bakeryId, String category) {
        BreadCategoryType breadCategoryType = Optional.ofNullable(BreadCategoryType.fromString(category))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 빵 카테고리입니다."));

        BreadCategories breadCategories = breadCategoriesQuerydslRepository.findByBreadCategoryName(breadCategoryType.toString().replaceAll("[/]", ""));
        Long categoryId = breadCategories.getId();
        List<String> menuList = menusQuerydslRepository.findByBreadCategoryIdBakeryId(categoryId, bakeryId);

        return MenuListResponse.builder()
                .menuList(menuList != null ? menuList : Collections.emptyList())
                .build();
    }
}
