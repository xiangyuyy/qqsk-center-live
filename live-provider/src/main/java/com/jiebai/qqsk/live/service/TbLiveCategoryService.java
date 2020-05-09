package com.jiebai.qqsk.live.service;

import com.jiebai.framework.service.Service;
import com.jiebai.qqsk.live.model.TbLiveCategoryDO;

import java.util.List;


/**
 * @author lilin
 * @date 2020/02/09 16:22:47
 */
public interface TbLiveCategoryService extends Service<TbLiveCategoryDO> {
    /**
     * 获取所有未被禁用的标签
     * @return
     */
    List<TbLiveCategoryDO> getAllEnableMark();


    /**
     * 获取所有的标签
     * @return
     */
    List<TbLiveCategoryDO> getAllMark();



    /**
     * 获取某些标签
     * @return
     */
    List<TbLiveCategoryDO> getLiveCategory(Integer id);

    /**
     * 根据name查询
     * @param name
     * @return
     */
    TbLiveCategoryDO getByName(String name);

}
