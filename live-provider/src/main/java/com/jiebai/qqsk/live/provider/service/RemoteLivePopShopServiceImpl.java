package com.jiebai.qqsk.live.provider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jiebai.framework.common.cglib.BaseBeanCopier;
import com.jiebai.framework.common.cglib.SimpleBeanCopier;
import com.jiebai.qqsk.goods.dto.TbItemModuleDTO;
import com.jiebai.qqsk.goods.remote.RemoteItemModuleService;
import com.jiebai.qqsk.live.constant.OpenStatusEnum;
import com.jiebai.qqsk.live.dto.*;
import com.jiebai.qqsk.live.enums.PopshopStatusEnum;
import com.jiebai.qqsk.live.enums.PopshopTypeEnum;
import com.jiebai.qqsk.live.enums.UpdateTypeEnum;
import com.jiebai.qqsk.live.exception.RemoteLiveServiceErrorCodeEnum;
import com.jiebai.qqsk.live.exception.RemoteLiveServiceException;
import com.jiebai.qqsk.live.model.LivePopShopManagerQueryDO;
import com.jiebai.qqsk.live.model.TbLivePopShopDO;
import com.jiebai.qqsk.live.model.TbLivePopshopLogDO;
import com.jiebai.qqsk.live.remote.RemoteLivePopShopService;
import com.jiebai.qqsk.live.service.TbLivePopShopService;
import com.jiebai.qqsk.live.service.TbLivePopshopLogService;
import com.jiebai.qqsk.live.service.TbLiveRoomService;
import com.jiebai.qqsk.member.dto.UserDTO;
import com.jiebai.qqsk.member.remote.RemoteUserService;
import com.jiebai.qqsk.order.dto.PopOrderHomeDataDTO;
import com.jiebai.qqsk.order.exception.RemoteOrderServiceException;
import com.jiebai.qqsk.order.remote.RemotePopShopOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.jiebai.qqsk.live.enums.PopshopStatusEnum.THREE;
import static com.jiebai.qqsk.live.enums.PopshopStatusEnum.ZERO;

/**
 * @author cxy
 * 2019/12/17
 */
@Slf4j
@Component
@Service(interfaceClass = RemoteLivePopShopService.class, version = "${provider.live.version}", validation = "false", retries = 0, timeout = 5000)
public class RemoteLivePopShopServiceImpl implements RemoteLivePopShopService {

    private static BaseBeanCopier<LivePopShopDTO, TbLivePopShopDO> LIVEPOPSHOP_DTO2DO_COPIER =
            new SimpleBeanCopier<>(LivePopShopDTO.class, TbLivePopShopDO.class);

    private static BaseBeanCopier<TbLivePopShopDO, LivePopShopDTO> LIVEPOPSHOP_DO2DTO_COPIER =
            new SimpleBeanCopier<>(TbLivePopShopDO.class, LivePopShopDTO.class);

    private static BaseBeanCopier<TbLivePopShopDO, TbLivePopShopManagerDTO> LIVEPOPSHOPDO2MANAGERDTO_COPIER =
            new SimpleBeanCopier<>(TbLivePopShopDO.class, TbLivePopShopManagerDTO.class);

    private static BaseBeanCopier<LivePopShopManagerQueryDTO, LivePopShopManagerQueryDO> MANAGERQUERYDTO2DO_COPIER =
            new SimpleBeanCopier<>(LivePopShopManagerQueryDTO.class, LivePopShopManagerQueryDO.class);

    private static BaseBeanCopier<TbLivePopshopLogDO, LivePopshopLogDTO> LIVEPOPSHOPLOGDO2DTO_COPIER =
            new SimpleBeanCopier<>(TbLivePopshopLogDO.class, LivePopshopLogDTO.class);

    @Reference(version = "${consumer.order.version}", validation = "false")
    private RemotePopShopOrderService remotePopShopOrderService;

    @Reference(version = "${consumer.goods.version}", validation = "false")
    private RemoteItemModuleService remoteItemModuleService;
    @Reference(version = "${consumer.member.version}", validation = "false")
    private RemoteUserService remoteUserService;
    @Resource
    private TbLivePopShopService tbLivePopShopService;

    @Resource
    private TbLivePopshopLogService tbLivePopshopLogService;

    @Resource
    private TbLiveRoomService tbLiveRoomService;


    private final String itemModuleName = "全球U选,情趣生活,开卡商品,虚拟";

    @Override
    public Boolean isOpenLive(Integer userId) {
        return tbLivePopShopService.isOpenLive(userId);
    }

    @Override
    public Boolean isOpenPopShop(Integer userId) {
        return tbLivePopShopService.isOpenPopShop(userId);
    }

    @Override
    public LivePowerDTO getUserPower(Integer userId) {
        LivePowerDTO livePowerDTO = new LivePowerDTO(false, false, false);
        TbLivePopShopDO tbLivePopShopDO = tbLivePopShopService.selectOneByUserId(userId);
        if (Objects.isNull(tbLivePopShopDO)) {
            return livePowerDTO;
        }
        //如果开通过且未关闭则有权限
        livePowerDTO.setIsOpenLive(tbLivePopShopDO.getIsOpenLive() > 0 && tbLivePopShopDO.getIsCloseLive() < 1);
        livePowerDTO.setIsOpenPop(tbLivePopShopDO.getIsOpenPopShop() > 0 && tbLivePopShopDO.getIsClosePopShop() < 1);
        String status = tbLivePopShopDO.getStatus();
        //如果为未审核，审核通过则为true
        if (ZERO.getStatus().equals(status) || THREE.getStatus().equals(status)) {
            livePowerDTO.setIsPassCheck(true);
        }
        return livePowerDTO;
    }

    @Override
    public PopHomePageDTO getPopShopHomePageData(Integer userId) {
        PopHomePageDTO popHomePageDTO =
                new PopHomePageDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, 0, 0, null, null);
        try {
            PopOrderHomeDataDTO popOrderTodayData = remotePopShopOrderService.findTodaySaleData(userId);
            if (Objects.nonNull(popOrderTodayData)) {
                popHomePageDTO.setWaitReceiveMoney(popOrderTodayData.getWaitReceiveMoney());
                popHomePageDTO.setTodaySaleMoney(popOrderTodayData.getTodaySaleMoney());
                popHomePageDTO.setTodayOrderCount(popOrderTodayData.getTodayOrderCount());
            }
        } catch (RemoteOrderServiceException e) {
            log.error("调用order服务出现异常, 异常信息为message = {}", e.getMessage());
        }
        TbLivePopShopDO tbLivePopShopDO = tbLivePopShopService.selectOneByUserId(userId);
        boolean flag = tbLivePopShopDO.getPopshopType() == null || "".equals(tbLivePopShopDO.getPopshopType()) ||
                tbLivePopShopDO.getPopshopCategory() == null || "".equals(tbLivePopShopDO.getPopshopCategory()) ||
                tbLivePopShopDO.getIdCardFrontPhoto() == null || "".equals(tbLivePopShopDO.getIdCardFrontPhoto()) ||
                tbLivePopShopDO.getIdCardBackPhoto() == null || "".equals(tbLivePopShopDO.getIdCardBackPhoto()) ||
                tbLivePopShopDO.getIdCardHandPhoto() == null || "".equals(tbLivePopShopDO.getIdCardHandPhoto()) ||
                (!PopshopTypeEnum.PERSONAL.getType().equals(tbLivePopShopDO.getPopshopType()) &&
                        (tbLivePopShopDO.getBusinessLicense() == null || "".equals(tbLivePopShopDO.getBusinessLicense())));
        if (flag) {
            //信息不全弹出提示
            popHomePageDTO.setAddInformation(1);
        }
        popHomePageDTO.setStatus(tbLivePopShopDO.getStatus());
        if (PopshopStatusEnum.TWO.getStatus().equals(tbLivePopShopDO.getStatus())) {
            //审核不通过弹出提示
            popHomePageDTO.setAuditHints(1);
            TbLivePopshopLogDO markByUserId = tbLivePopshopLogService.getMarkByUserId(userId);
            if (markByUserId != null) {
                popHomePageDTO.setReason(markByUserId.getRemark());
            }
        }
        Optional.ofNullable(tbLivePopShopDO).ifPresent(s -> {
            //账户余额
            popHomePageDTO.setAccountRemain(s.getAccountRemain());
            //保证金
            popHomePageDTO.setPromiseMoney(s.getPromiseMoney());
        });

        return popHomePageDTO;
    }

    @Override
    public int manageOpenLive(Integer userId) {
        TbLivePopShopDO tbLivePopShopDO = tbLivePopShopService.selectOneByUserId(userId);
        //为空直接插入
        if (Objects.isNull(tbLivePopShopDO)) {
            return tbLivePopShopService.manageOpenLive(userId);
        }
        //为未开通，更新
        if (OpenStatusEnum.NOT_OPEN.getStatus().equals(tbLivePopShopDO.getIsOpenLive())) {
            return tbLivePopShopService.updateByPrimarySelective(tbLivePopShopDO.getId());
        } else {
            //已开通抛个错
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_IS_OPEN);
        }
    }

    @Override
    public List<Map<String, String>> getPopshopType() {
        List<Map<String, String>> list = new ArrayList<>();
        PopshopTypeEnum[] values = PopshopTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("key", values[i].getType());
            map.put("value", values[i].getMsg());
            list.add(map);
        }
        return list;
    }

    @Override
    public List<Map<String, String>> getPopshopCatgory() {
        List<Map<String, String>> list = new ArrayList<>();
        List<TbItemModuleDTO> tbItemModuleDTOS = remoteItemModuleService.queryAllOneLevelItem();
        if(tbItemModuleDTOS!=null && tbItemModuleDTOS.size()>0){
            for(int i=0;i<tbItemModuleDTOS.size();i++){
                if(itemModuleName.contains(tbItemModuleDTOS.get(i).getItemModuleName())){
                    continue;
                }
                Map<String,String> map = new HashMap<>();
                map.put("key",String.valueOf(tbItemModuleDTOS.get(i).getItemModuleId()));
                map.put("value",tbItemModuleDTOS.get(i).getItemModuleName());
                list.add(map);
            }

        }
        return list;
    }

    @Override
    public Integer updateLivePopShop(LivePopShopDTO livePopShopDTO) {
        Integer userId = livePopShopDTO.getUserId();
        TbLivePopShopDO tbLivePopShopDO = tbLivePopShopService.selectOneByUserId(userId);
        UserDTO userDTO = remoteUserService.getById(userId);
        if (tbLivePopShopDO == null) {
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_IS_NOT_OPEN);
        }
        if (UpdateTypeEnum.NORMAL.getType().equals(livePopShopDTO.getType())) {
            livePopShopDTO.setStatus(ZERO.getStatus());
        } else if (UpdateTypeEnum.VERIFY.getType().equals(livePopShopDTO.getType())) {
            livePopShopDTO.setStatus(PopshopStatusEnum.ONE.getStatus());
            //提交审核需要插入日志记录
            TbLivePopshopLogDO tbLivePopshopLogDO = new TbLivePopshopLogDO();
            tbLivePopshopLogDO.setGmtCreate(new Date());
            tbLivePopshopLogDO.setOperator(String.valueOf(livePopShopDTO.getUserId()));
            tbLivePopshopLogDO.setPopshopId(tbLivePopShopDO.getId());
            tbLivePopshopLogDO.setOperateName(userDTO.getNickname());
            tbLivePopshopLogDO.setRemark("提交再次审核");
            tbLivePopshopLogDO.setStatus(Integer.parseInt(PopshopStatusEnum.ONE.getStatus()));
            tbLivePopshopLogService.save(tbLivePopshopLogDO);
        }
        livePopShopDTO.setGmtModified(new Date());
        TbLivePopShopDO livePopShopDO = LIVEPOPSHOP_DTO2DO_COPIER.copy(livePopShopDTO);
        if (livePopShopDTO.getPopshopCategory() != null && !"".equals(livePopShopDTO.getPopshopCategory())) {
            livePopShopDO.setPopshopCategory(Integer.parseInt(livePopShopDTO.getPopshopCategory()));
        }
        livePopShopDO.setId(tbLivePopShopDO.getId());
        int result = tbLivePopShopService.updateByPrimarySelective(livePopShopDO);
        return result;
    }

    @Override
    public LivePopShopDTO getLivePopShopInfo(Integer userId) {
        TbLivePopShopDO tbLivePopShopDO = tbLivePopShopService.selectOneByUserId(userId);
        LivePopShopDTO livePopShopDTO = LIVEPOPSHOP_DO2DTO_COPIER.copy(tbLivePopShopDO);
        if(tbLivePopShopDO.getPopshopCategory()!=null && !"".equals(tbLivePopShopDO.getPopshopCategory())){
            livePopShopDTO.setPopshopCategory(remoteItemModuleService.getById(tbLivePopShopDO.getPopshopCategory()).getItemModuleName());
        }
        livePopShopDTO.setPopshopType(PopshopTypeEnum.getMsgByType(tbLivePopShopDO.getPopshopType()));
        return livePopShopDTO;
    }

    @Override
    public int checkPopShop(LivePopshopLogDTO livePopshopLogDTO) {
        return tbLivePopShopService.checkPopShop(livePopshopLogDTO);
    }

    @Override
    public PageInfo<TbLivePopShopManagerDTO> getListForManager(LivePopShopManagerQueryDTO queryDTO) {
        if (Objects.isNull(queryDTO.getPageSize()) || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(Integer.valueOf(30));
        }
        if (Objects.isNull(queryDTO.getPageNum()) || queryDTO.getPageNum() < 0) {
            queryDTO.setPageNum(Integer.valueOf(1));
        }
        Page<TbLivePopShopDO> page = PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        LivePopShopManagerQueryDO query = MANAGERQUERYDTO2DO_COPIER.apply(queryDTO);
        tbLivePopShopService.getListForManager(query);
        ArrayList<TbLivePopShopManagerDTO> list = Lists.newArrayList();
        for (TbLivePopShopDO tbLivePopShopDO : page) {
            TbLivePopShopManagerDTO dto = LIVEPOPSHOPDO2MANAGERDTO_COPIER.copy(tbLivePopShopDO);
            if(tbLivePopShopDO.getPopshopCategory()!=null && !"".equals(tbLivePopShopDO.getPopshopCategory())){
                dto.setPopshopCategory(remoteItemModuleService.getById(tbLivePopShopDO.getPopshopCategory()).getItemModuleName());
            }
            //dto.setPopshopType(PopshopTypeEnum.getMsgByType(tbLivePopShopDO.getPopshopType()));
            dto.setOpenLives(tbLiveRoomService.getOpenLivesByUserId(tbLivePopShopDO.getUserId()));
            list.add(dto);
        }
        PageInfo<TbLivePopShopManagerDTO> pageInfo = new PageInfo<>(list);
        pageInfo.setHasNextPage(page.getPages() > page.getPageNum());
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }

    @Override
    public TbLivePopShopManagerDTO getLivePopShopManagerInfor(Integer popShopId, Boolean IfShowLog) {
        TbLivePopShopDO tbLivePopShopDO = tbLivePopShopService.getLivePopShopInfor(popShopId);
        if (Objects.isNull(tbLivePopShopDO)){
            throw new RemoteLiveServiceException(RemoteLiveServiceErrorCodeEnum.LIVE_USER_ENTITY_NOT_NULL);
        }
        TbLivePopShopManagerDTO dto = LIVEPOPSHOPDO2MANAGERDTO_COPIER.copy(tbLivePopShopDO);
        if(tbLivePopShopDO.getPopshopCategory()!=null && !"".equals(tbLivePopShopDO.getPopshopCategory())){
            dto.setPopshopCategory(remoteItemModuleService.getById(tbLivePopShopDO.getPopshopCategory()).getItemModuleName());
        }
        //dto.setPopshopType(PopshopTypeEnum.getMsgByType(tbLivePopShopDO.getPopshopType()));
        dto.setOpenLives(tbLiveRoomService.getOpenLivesByUserId(tbLivePopShopDO.getUserId()));
        if(IfShowLog){
            List<LivePopshopLogDTO> dtos = new ArrayList<>();
            List<TbLivePopshopLogDO> list = tbLivePopshopLogService.getLogsByPopShopId(popShopId);
            if (list.size() > 0){
                for (TbLivePopshopLogDO item: list ) {
                    LivePopshopLogDTO logDTO =  LIVEPOPSHOPLOGDO2DTO_COPIER.copy(item);
                    dtos.add(logDTO);
                }
                dto.setLogList(dtos);
            }
        }
        return dto;
    }
}
