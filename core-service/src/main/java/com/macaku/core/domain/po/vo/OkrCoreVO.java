package com.macaku.core.domain.po.vo;

import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.domain.po.quadrant.vo.FirstQuadrantVO;
import com.macaku.core.domain.po.quadrant.vo.FourthQuadrantVO;
import com.macaku.core.domain.po.quadrant.vo.SecondQuadrantVO;
import com.macaku.core.domain.po.quadrant.vo.ThirdQuadrantVO;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:38
 */
@Data
public class OkrCoreVO extends OkrCore {

    private FirstQuadrantVO firstQuadrantVO;

    private SecondQuadrantVO secondQuadrantVO;

    private ThirdQuadrantVO thirdQuadrantVO;

    private FourthQuadrantVO fourthQuadrantVO;

}
