package com.shopbee.productservice.shared.external.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatistic {
    private double averageRating;
    private long totalReviews;
    private long totalOneStars;
    private long totalTwoStars;
    private long totalThreeStars;
    private long totalFourStars;
    private long totalFiveStars;
}
