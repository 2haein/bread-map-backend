package com.depromeet.breadmapbackend.bakeries.controller;

import com.depromeet.breadmapbackend.bakeries.dto.BakeryListResponse;
import com.depromeet.breadmapbackend.bakeries.dto.BakeryMenusListResponse;
import com.depromeet.breadmapbackend.reviews.dto.ReviewsListResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/bakery")
@RequiredArgsConstructor
public class BakeriesController {

    /**
     * 빵집 리스트 조회
     * @return ResponseEntity<BakeryListResponse>
     */
    @ApiOperation(value = "빵집 리스트", notes = "빵집 리스트 조회")
    @GetMapping(value = "")
    public ResponseEntity<BakeryListResponse> getBakeryList(@RequestParam Double latitude, @RequestParam Double longitude, @RequestParam Long range){ // TODO range 소숫점 있는지 체크 필요
        return null;
    }

    /**
     * 단일 빵집 리뷰 조회
     * @return ResponseEntity<BakeryReviewListResponse>
     */
    @ApiOperation(value = "단일 빵집 리뷰 리스트", notes = "단일 빵집 리뷰 리스트 조회")
    @GetMapping(value = "/{bakeryId}/review")
    public ResponseEntity<ReviewsListResponse> getBakeryReviewList(@PathVariable Integer bakeryId){
        return null;
    }

    /**
     * 단일 빵집 메뉴 조회
     * @return ResponseEntity<BakeryMenusListResponse>
     */
    @ApiOperation(value = "단일 빵집 메뉴 리스트", notes = "단일 빵집 메뉴 리스트 조회")
    @GetMapping(value = "/{bakeryId}/menus")
    public ResponseEntity<BakeryMenusListResponse> getBakeryMenusList(@PathVariable Integer bakeryId){
        return null;
    }

}
