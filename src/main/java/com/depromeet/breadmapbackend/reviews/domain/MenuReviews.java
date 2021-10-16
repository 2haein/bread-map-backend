package com.depromeet.breadmapbackend.reviews.domain;

import com.depromeet.breadmapbackend.bakeries.domain.Menus;
import com.depromeet.breadmapbackend.common.domain.BaseEntity;
import com.depromeet.breadmapbackend.common.domain.Images;
import com.depromeet.breadmapbackend.members.domain.Members;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MenuReviews extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "menu_review_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "menu_id")
    private Menus menus;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Members members;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private Integer rating;

    @OneToMany(mappedBy = "menuReviews")
    private List<Images> imagePathList = new ArrayList<>();

}