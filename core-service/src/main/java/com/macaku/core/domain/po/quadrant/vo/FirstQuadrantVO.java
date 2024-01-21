package com.macaku.core.domain.po.quadrant.vo;

import com.macaku.core.domain.po.inner.KeyResult;
import com.macaku.core.domain.po.quadrant.FirstQuadrant;
import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 23:09
 */
@Data
public class FirstQuadrantVO extends FirstQuadrant {

    private List<KeyResult> keyResults;

}
