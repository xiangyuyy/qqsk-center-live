package com.jiebai.qqsk.live.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cxy
 * 上下屏展示信息 最多2个
 */
@Data
public class UpAndDownGoodDTO implements Serializable {

    private static final long serialVersionUID = 9066775323335370662L;

    /**
     * -1 没有找到数据 0 失败 2成功
     */
    private Integer result;

    /**
     * 展示的上屏商品信息
     */
    private List<LiveHomeGoodDTO> showGoodsList;
}
