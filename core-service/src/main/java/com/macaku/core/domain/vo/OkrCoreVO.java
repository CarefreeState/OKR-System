package com.macaku.core.domain.vo;

import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.domain.po.quadrant.vo.FirstQuadrantVO;
import com.macaku.core.domain.po.quadrant.vo.FourthQuadrantVO;
import com.macaku.core.domain.po.quadrant.vo.SecondQuadrantVO;
import com.macaku.core.domain.po.quadrant.vo.ThirdQuadrantVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:38
 */
@ApiModel("OKR 内核详细数据")
@Data
public class OkrCoreVO extends OkrCore {

    @ApiModelProperty("第一象限详细信息")
    private FirstQuadrantVO firstQuadrantVO;

    @ApiModelProperty("第二象限详细信息")
    private SecondQuadrantVO secondQuadrantVO;

    @ApiModelProperty("第三象限详细信息")
    private ThirdQuadrantVO thirdQuadrantVO;

    @ApiModelProperty("第四象限详细信息")
    private FourthQuadrantVO fourthQuadrantVO;

}
