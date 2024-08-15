package com.vocmi.daijia.driver.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vocmi.daijia.common.constant.SystemConstant;
import com.vocmi.daijia.driver.mapper.DriverAccountMapper;
import com.vocmi.daijia.driver.mapper.DriverInfoMapper;
import com.vocmi.daijia.driver.mapper.DriverLoginLogMapper;
import com.vocmi.daijia.driver.mapper.DriverSetMapper;
import com.vocmi.daijia.driver.service.DriverInfoService;
import com.vocmi.daijia.model.entity.driver.DriverAccount;
import com.vocmi.daijia.model.entity.driver.DriverInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vocmi.daijia.model.entity.driver.DriverLoginLog;
import com.vocmi.daijia.model.entity.driver.DriverSet;
import com.vocmi.daijia.model.vo.driver.DriverInfoVo;
import com.vocmi.daijia.model.vo.driver.DriverLoginVo;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverInfoServiceImpl extends ServiceImpl<DriverInfoMapper, DriverInfo> implements DriverInfoService {

    @Resource
    private WxMaService wxMaService;

    @Resource
    private DriverInfoMapper driverInfoMapper;

    @Resource
    private DriverSetMapper driverSetMapper;

    @Resource
    private DriverAccountMapper driverAccountMapper;

    @Resource
    private DriverLoginLogMapper driverLoginLogMapper;

    @Override
    public Long login(String code) {
        String openId = null;

        try {
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            openId = sessionInfo.getOpenid();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

        LambdaQueryWrapper<DriverInfo> driverInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        DriverInfo driverInfo = driverInfoMapper.selectOne(driverInfoLambdaQueryWrapper.eq(DriverInfo::getWxOpenId, openId));

        if (BeanUtil.isEmpty(driverInfo)) {
            driverInfo = new DriverInfo();
            driverInfo.setNickname(String.valueOf(System.currentTimeMillis()));
            driverInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
            driverInfo.setWxOpenId(openId);
            this.save(driverInfo);

            //初始化默认设置
            DriverSet driverSet = new DriverSet();
            driverSet.setDriverId(driverInfo.getId());
            driverSet.setOrderDistance(new BigDecimal(0));//0：无限制
            driverSet.setAcceptDistance(new BigDecimal(SystemConstant.ACCEPT_DISTANCE));//默认接单范围：5公里
            driverSet.setIsAutoAccept(0);//0：否 1：是
            driverSetMapper.insert(driverSet);

            //初始化司机账户
            DriverAccount driverAccount = new DriverAccount();
            driverAccount.setDriverId(driverInfo.getId());
            driverAccountMapper.insert(driverAccount);
        }

        //登录日志
        DriverLoginLog driverLoginLog = new DriverLoginLog();
        driverLoginLog.setDriverId(driverInfo.getId());
        driverLoginLog.setMsg("小程序登录");
        driverLoginLogMapper.insert(driverLoginLog);
        return driverInfo.getId();
    }

    @Override
    public DriverLoginVo getDriverLoginInfo(Long driverId) {
        DriverInfo driverInfo = this.getById(driverId);
        DriverLoginVo driverLoginVo = BeanUtil.copyProperties(driverInfo, DriverLoginVo.class);

        //是否创建人脸库人员，接单时做人脸识别判断
        Boolean isArchiveFace = StringUtils.isBlank(driverInfo.getFaceModelId());
        driverLoginVo.setIsArchiveFace(isArchiveFace);
        return driverLoginVo;
    }
}