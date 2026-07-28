package com.bank.beijing.beijingmgr.web.controller.dwr;

import com.bank.beijing.beijingmgr.core.iservice.IBeijingService;
import com.bank.common.module.ResultBean;
import com.bank.common.result.Result;
import com.bank.common.util.WebUtilWork;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 北京业务 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/beijing-mgr")
public class DwrBeijingService {

    @Autowired
    private IBeijingService beijingService;

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResultBean health() {
        return WebUtilWork.WebObjectPack(true, "北京业务模块运行正常", null);
    }
}
