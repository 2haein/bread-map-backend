package com.depromeet.breadmapbackend.flags.domain;

import com.depromeet.breadmapbackend.bakeries.domain.Bakeries;
import com.depromeet.breadmapbackend.common.domain.BaseEntity;
import com.depromeet.breadmapbackend.common.enumerate.FlagType;
import com.depromeet.breadmapbackend.members.domain.Members;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flags extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Members members;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakeries bakeries;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private FlagType flagType;
}
