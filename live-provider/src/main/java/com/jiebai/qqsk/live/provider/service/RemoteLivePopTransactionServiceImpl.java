package com.jiebai.qqsk.live.provider.service;

import com.jiebai.qqsk.live.remote.RemoteLivePopTransactionService;
import com.jiebai.qqsk.live.service.TbLivePopTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author lichenguang
 * @date 2019/12/23
 */
@Slf4j
@Component
@Service(interfaceClass = RemoteLivePopTransactionService.class, version = "${provider.live.version}", validation =
    "false", retries = 0, timeout = 5000)
public class RemoteLivePopTransactionServiceImpl implements RemoteLivePopTransactionService {

    @Resource
    private TbLivePopTransactionService tbLivePopTransactionService;

}
