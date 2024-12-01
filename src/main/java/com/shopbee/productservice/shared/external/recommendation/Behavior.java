package com.shopbee.productservice.shared.external.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Behavior {
    private List<Long> favourites;
    private List<Long> inCart;
    private List<Long> recentVisits;
}
