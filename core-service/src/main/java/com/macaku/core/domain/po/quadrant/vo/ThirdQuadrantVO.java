package com.macaku.core.domain.po.quadrant.vo;

import com.macaku.core.domain.po.inner.Action;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:32
 */
@Data
public class ThirdQuadrantVO extends ThirdQuadrant {

    private List<Action> actions;

}
