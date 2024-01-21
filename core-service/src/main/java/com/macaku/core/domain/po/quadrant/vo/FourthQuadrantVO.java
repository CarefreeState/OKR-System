package com.macaku.core.domain.po.quadrant.vo;

import com.macaku.core.domain.po.inner.StatusFlag;
import com.macaku.core.domain.po.quadrant.FourthQuadrant;
import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:31
 */
@Data
public class FourthQuadrantVO extends FourthQuadrant {

    private List<StatusFlag> statusFlags;

}
