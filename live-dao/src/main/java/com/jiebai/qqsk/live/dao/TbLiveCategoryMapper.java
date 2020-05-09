package com.jiebai.qqsk.live.dao;

import com.jiebai.framework.service.Mapper;
import com.jiebai.qqsk.live.model.TbLiveCategoryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbLiveCategoryMapper extends Mapper<TbLiveCategoryDO> {
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
    List<TbLiveCategoryDO> getLiveCategory(@Param("id") Integer id);

    /**
     * 根据name查询
     * @param name
     * @return
     */
    TbLiveCategoryDO getByName(@Param("name") String name);
}