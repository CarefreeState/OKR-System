package com.macaku.medal.domain.entry;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 12:02
 */
@Getter
@ToString
@Builder
public class StandOutCrowd {

    private Long userId;

    private Integer degree;

    private Date deadline;

}
