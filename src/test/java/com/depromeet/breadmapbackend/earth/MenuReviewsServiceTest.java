package com.depromeet.breadmapbackend.earth;

import com.depromeet.breadmapbackend.auth.service.AuthService;
import com.depromeet.breadmapbackend.bakeries.domain.Bakeries;
import com.depromeet.breadmapbackend.bakeries.domain.BreadCategories;
import com.depromeet.breadmapbackend.bakeries.domain.Menus;
import com.depromeet.breadmapbackend.bakeries.repository.BakeriesRepository;
import com.depromeet.breadmapbackend.bakeries.repository.BreadCategoriesQuerydslRepository;
import com.depromeet.breadmapbackend.bakeries.repository.MenusQuerydslRepository;
import com.depromeet.breadmapbackend.bakeries.repository.MenusRepository;
import com.depromeet.breadmapbackend.common.enumerate.BreadCategoryType;
import com.depromeet.breadmapbackend.members.domain.Members;
import com.depromeet.breadmapbackend.members.repository.MemberRepository;
import com.depromeet.breadmapbackend.reviews.domain.MenuReviews;
import com.depromeet.breadmapbackend.reviews.dto.CreateMenuReviewsRequest;
import com.depromeet.breadmapbackend.reviews.dto.MenuReviewResponse;
import com.depromeet.breadmapbackend.reviews.repository.MenuReviewQuerydslRepository;
import com.depromeet.breadmapbackend.reviews.repository.MenuReviewRepository;
import com.depromeet.breadmapbackend.reviews.service.MenuReviewsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuReviewsServiceTest {

    @InjectMocks
    private MenuReviewsService menuReviewsService;

    @Mock
    private Pageable pageable;

    @Mock
    private MenuReviewRepository menuReviewRepository;

    @Mock
    private MenuReviewQuerydslRepository menuReviewQuerydslRepository;

    @Mock
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MenusRepository menusRepository;

    @Mock
    private BakeriesRepository bakeriesRepository;

    @Mock
    private MenusQuerydslRepository menusQuerydslRepository;

    @Mock
    private BreadCategoriesQuerydslRepository breadCategoriesQuerydslRepository;

    private static final String TOKEN = "tokentokentoken";
    private static final Long BAKERY_ID = 1L;
    private static final Long MEMBER_ID = 1L;
    private static final Long OTHER_MEMBER_ID = 0L;
    private static final Long MENU_REVIEW_ID = 1L;

    @Test
    @DisplayName("본인이 작성한 리뷰를 삭제합니다.")
    void deleteMenuReview() {
        // given
        Members member = Members.builder()
                .id(MEMBER_ID)
                .build();

        MenuReviews menuReview = new MenuReviews(MENU_REVIEW_ID, new Menus(), member, new Bakeries(), "", 3L, new ArrayList<>());

        given(authService.getMemberId(any(String.class))).willReturn(MEMBER_ID);
        given(memberRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(member));
        given(menuReviewQuerydslRepository.findByMenuReviewIdAndBakeryId(any(Long.class), any(Long.class))).willReturn(menuReview);
        // when
        menuReviewsService.deleteMenuReview(TOKEN, BAKERY_ID, MENU_REVIEW_ID);
        // then
        then(menuReviewRepository).should().delete(refEq(menuReview));
    }

    @Test
    @DisplayName("본인이 작성하지 않은 리뷰를 삭제하려 하였을 때, 예외가 발생합니다.")
    public void deleteMenuReviewIfMemberIsNotAuthor() {
        // given
        String expectedException = "해당 리뷰 작성자가 아닙니다.";
        Members member = Members.builder()
                .id(MEMBER_ID)
                .build();
        Members otherMember = Members.builder()
                .id(OTHER_MEMBER_ID)
                .build();
        MenuReviews menuReview = new MenuReviews(MENU_REVIEW_ID, new Menus(), otherMember, new Bakeries(), "", 3L, new ArrayList<>());

        given(authService.getMemberId(any(String.class))).willReturn(OTHER_MEMBER_ID);
        given(memberRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(member));
        given(menuReviewQuerydslRepository.findByMenuReviewIdAndBakeryId(any(Long.class), any(Long.class))).willReturn(menuReview);

        // when
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> {
            menuReviewsService.deleteMenuReview(TOKEN, BAKERY_ID, MENU_REVIEW_ID);
        });
        // then
        assertEquals(responseStatusException.getReason(), expectedException);
    }

    @Test
    @DisplayName("리뷰 삭제 시 해당하는 리뷰가 존재하지 않을 경우 예외가 발생합니다.")
    public void deleteMenuReviewIfNotExist() {
        // given
        String expectedException = "리뷰가 존재하지 않습니다.";

        Members member = Members.builder()
                .id(MEMBER_ID)
                .build();

        given(authService.getMemberId(any(String.class))).willReturn(MEMBER_ID);
        given(memberRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(member));
        given(menuReviewQuerydslRepository.findByMenuReviewIdAndBakeryId(any(Long.class), any(Long.class))).willReturn(null);
        // when
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> {
            menuReviewsService.deleteMenuReview(TOKEN, BAKERY_ID, MENU_REVIEW_ID);
        });
        // then
        assertEquals(responseStatusException.getReason(), expectedException);
    }

    @Test
    @DisplayName("리뷰를 작성하고자 하는 메뉴가 빵집에 존재하지 않을 경우, 신규 메뉴를 생성하여 리뷰를 작성합니다.")
    void createMenuReviewListIfNotExistMenu() {
        // given
        List<CreateMenuReviewsRequest> menuReviewsRequestList = new ArrayList<>();
        CreateMenuReviewsRequest createMenuReviewsRequest = new CreateMenuReviewsRequest("과자류", "MENU_NAME", 1000, 4L, "CONTENT", new ArrayList<>());
        menuReviewsRequestList.add(createMenuReviewsRequest);

        Members member = Members.builder()
                .id(MEMBER_ID)
                .build();
        Bakeries bakery = Bakeries.builder()
                .id(BAKERY_ID)
                .menusList(new ArrayList<>())
                .menuReviewsList(new ArrayList<>())
                .build();
        BreadCategories breadCategories = new BreadCategories(1L, BreadCategoryType.과자류, new ArrayList<>(), new ArrayList<>());

        for(CreateMenuReviewsRequest menuReviewsRequest: menuReviewsRequestList) {
            given(authService.getMemberId(TOKEN)).willReturn(MEMBER_ID);

            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.ofNullable(member));
            given(bakeriesRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(bakery));

            when(menusQuerydslRepository.findByMenuNameBakeryId(menuReviewsRequest.getMenuName(), BAKERY_ID)).thenReturn(null); // menu == null

            when(breadCategoriesQuerydslRepository.findByBreadCategoryName(menuReviewsRequest.getCategoryName().replaceAll("[ /]", ""))).thenReturn(breadCategories);

            menuReviewsService.createMenuReviewList(TOKEN, BAKERY_ID, menuReviewsRequestList);

            verify(menusRepository).save(any());
            verify(menuReviewRepository).save(any());
        }
    }

    @Test
    @DisplayName("리뷰를 작성하고자 하는 메뉴가 빵집에 있고, 해당 메뉴 이미지가 없는데 신규 리뷰에 사진 있다면 메뉴 사진을 업데이트 합니다.")
    void createMenuReviewListAndUpdateMenuImage() {
        // given
        List<CreateMenuReviewsRequest> menuReviewsRequestList = new ArrayList<>();
        List<String> imgPathList = new ArrayList<>();
        imgPathList.add("http://img.cdn.com");
        imgPathList.add("http://img2.cdn.com");
        CreateMenuReviewsRequest createMenuReviewsRequest = new CreateMenuReviewsRequest("과자류", "MENU_NAME", 1000, 4L, "CONTENT", imgPathList);
        menuReviewsRequestList.add(createMenuReviewsRequest);

        Members member = Members.builder()
                .id(MEMBER_ID)
                .build();
        Bakeries bakery = Bakeries.builder()
                .id(BAKERY_ID)
                .menusList(new ArrayList<>())
                .menuReviewsList(new ArrayList<>())
                .build();
        BreadCategories breadCategories = new BreadCategories(1L, BreadCategoryType.과자류, new ArrayList<>(), new ArrayList<>());

        for(CreateMenuReviewsRequest menuReviewsRequest: menuReviewsRequestList) {
            given(authService.getMemberId(TOKEN)).willReturn(MEMBER_ID);

            given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.ofNullable(member));
            given(bakeriesRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(bakery));

            Menus menus = new Menus(1L, menuReviewsRequest.getMenuName(), bakery, menuReviewsRequest.getPrice(), breadCategories, "");

            when(menusQuerydslRepository.findByMenuNameBakeryId(menuReviewsRequest.getMenuName(), BAKERY_ID)).thenReturn(menus);

            menuReviewsService.createMenuReviewList(TOKEN, BAKERY_ID, menuReviewsRequestList);

            assertEquals(menus.getImgPath(), menuReviewsRequest.getImgPathList().get(0));
            verify(menuReviewRepository).save(any());
        }
    }

    @ParameterizedTest
    @DisplayName("잘못된 page 값이 들어간 경우 예외를 발생합니다.")
    @ValueSource(ints = {0, -1})
    public void setWrongPageValueWhenGetMenuReviewList(int page) {
        // given
        String expectedException = "잘못된 page 값입니다(시작 page: 1).";
        Integer limit = 10;

        // when
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> {
            menuReviewsService.getMenuReviewList(BAKERY_ID, page, limit);
        });

        // then
        assertEquals(responseStatusException.getReason(), expectedException);
        verify(menuReviewQuerydslRepository, never()).findMenuReviewPageableByBakeryId(BAKERY_ID, pageable);
    }

    @ParameterizedTest
    @DisplayName("page 값이 1 이상으로 들어올 경우 메뉴 리뷰 리스트를 반환합니다.")
    @ValueSource(ints = {1, 2})
    public void setPageValueMoreThan0WhenGetMenuReviewList(int page) {
        // given
        Integer limit = 10;
        Pageable pageable = PageRequest.of(page - 1, limit);
        // 하기 구문은 JAVA 9부터 사용이 가능합니다.
        //List<MenuReviewResponse> content = List.of(new MenuReviewResponse(), new MenuReviewResponse());
        List<MenuReviewResponse> content = Arrays.asList(new MenuReviewResponse(), new MenuReviewResponse());

        Slice<MenuReviewResponse> menuReviewResponseSlice = new SliceImpl<>(content, pageable, true);

        // when
        when(menuReviewQuerydslRepository.findMenuReviewPageableByBakeryId(BAKERY_ID, pageable)).thenReturn(menuReviewResponseSlice);

        menuReviewsService.getMenuReviewList(BAKERY_ID, page, limit);

        // then
        then(menuReviewQuerydslRepository).should().findMenuReviewPageableByBakeryId(BAKERY_ID, pageable);
    }
}
