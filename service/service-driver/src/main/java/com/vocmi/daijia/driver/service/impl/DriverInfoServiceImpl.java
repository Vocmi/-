package com.vocmi.daijia.driver.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.iai.v20180301.IaiClient;
import com.tencentcloudapi.iai.v20180301.models.*;
import com.vocmi.daijia.common.constant.SystemConstant;
import com.vocmi.daijia.common.execption.VocmiException;
import com.vocmi.daijia.common.result.ResultCodeEnum;
import com.vocmi.daijia.driver.config.TencentCloudProperties;
import com.vocmi.daijia.driver.mapper.*;
import com.vocmi.daijia.driver.service.CosService;
import com.vocmi.daijia.driver.service.DriverInfoService;
import com.vocmi.daijia.model.entity.driver.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vocmi.daijia.model.form.driver.DriverFaceModelForm;
import com.vocmi.daijia.model.form.driver.UpdateDriverAuthInfoForm;
import com.vocmi.daijia.model.vo.driver.DriverAuthInfoVo;
import com.vocmi.daijia.model.vo.driver.DriverLoginVo;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

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

    @Resource
    private CosService cosService;

    @Resource
    private TencentCloudProperties tencentCloudProperties;

    @Resource
    private DriverFaceRecognitionMapper driverFaceRecognitionMapper;

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
        Boolean isArchiveFace = StrUtil.isNotEmpty(driverInfo.getFaceModelId());
        driverLoginVo.setIsArchiveFace(isArchiveFace);
        return driverLoginVo;
    }

    @Override
    public DriverAuthInfoVo getDriverAuthInfo(Long driverId) {
        DriverInfo driverInfo = this.getById(driverId);
        DriverAuthInfoVo driverAuthInfoVo = BeanUtil.copyProperties(driverInfo, DriverAuthInfoVo.class);
        driverAuthInfoVo.setIdcardBackShowUrl(cosService.getImageUrl(driverAuthInfoVo.getIdcardBackUrl()));
        driverAuthInfoVo.setIdcardFrontShowUrl(cosService.getImageUrl(driverAuthInfoVo.getIdcardFrontUrl()));
        driverAuthInfoVo.setIdcardHandShowUrl(cosService.getImageUrl(driverAuthInfoVo.getIdcardHandUrl()));
        driverAuthInfoVo.setDriverLicenseFrontShowUrl(cosService.getImageUrl(driverAuthInfoVo.getDriverLicenseFrontUrl()));
        driverAuthInfoVo.setDriverLicenseBackShowUrl(cosService.getImageUrl(driverAuthInfoVo.getDriverLicenseBackUrl()));
        driverAuthInfoVo.setDriverLicenseHandShowUrl(cosService.getImageUrl(driverAuthInfoVo.getDriverLicenseHandUrl()));
        return driverAuthInfoVo;
    }

    @Override
    public Boolean creatDriverFaceModel(DriverFaceModelForm driverFaceModelForm) {
        DriverInfo driverInfo = this.getById(driverFaceModelForm.getDriverId());
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(tencentCloudProperties.getSecretId(), tencentCloudProperties.getSecretKey());
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            IaiClient client = new IaiClient(cred, tencentCloudProperties.getRegion(), clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            CreatePersonRequest req = new CreatePersonRequest();
            req.setGroupId(tencentCloudProperties.getPersonGroupId());
            //基本信息
            req.setPersonId(String.valueOf(driverInfo.getId()));
            req.setGender(Long.parseLong(driverInfo.getGender()));
            req.setQualityControl(4L);
            req.setUniquePersonControl(4L);
            req.setPersonName(driverInfo.getName());
            req.setImage(driverFaceModelForm.getImageBase64());

            // 返回的resp是一个CreatePersonResponse的实例，与请求对象对应
            CreatePersonResponse resp = client.CreatePerson(req);
            // 输出json格式的字符串回包
            System.out.println(CreatePersonResponse.toJsonString(resp));
            if (StringUtils.isNotBlank(resp.getFaceId())) {
                //人脸校验必要参数，保存到数据库表
                driverInfo.setFaceModelId(resp.getFaceId());
                this.updateById(driverInfo);
            }
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            return false;
        }
        return true;
    }

    @Override
    public DriverSet getDriverSet(Long driverId) {
        LambdaQueryWrapper<DriverSet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DriverSet::getDriverId, driverId);
        return driverSetMapper.selectOne(queryWrapper);
    }

    @Override
    public Boolean isFaceRecognition(Long driverId) {
        LambdaQueryWrapper<DriverFaceRecognition> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DriverFaceRecognition::getDriverId, driverId).eq(DriverFaceRecognition::getFaceDate, new DateTime().toString("yyyy-MM-dd"));
        long count = driverFaceRecognitionMapper.selectCount(queryWrapper);
        return count != 0;
    }

    /**
     * 人脸验证
     * 文档地址：
     * https://cloud.tencent.com/document/api/867/44983
     * https://console.cloud.tencent.com/api/explorer?Product=iai&Version=2020-03-03&Action=VerifyFace
     *
     * @param driverFaceModelForm
     * @return
     */
    @Override
    public Boolean verifyDriverFace(DriverFaceModelForm driverFaceModelForm) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(tencentCloudProperties.getSecretId(), tencentCloudProperties.getSecretKey());
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            IaiClient client = new IaiClient(cred, tencentCloudProperties.getRegion(), clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            VerifyFaceRequest req = new VerifyFaceRequest();
            req.setImage(driverFaceModelForm.getImageBase64());
            req.setPersonId(String.valueOf(driverFaceModelForm.getDriverId()));
            // 返回的resp是一个VerifyFaceResponse的实例，与请求对象对应
            VerifyFaceResponse resp = client.VerifyFace(req);
            // 输出json格式的字符串回包
            System.out.println(VerifyFaceResponse.toJsonString(resp));
            if (resp.getIsMatch()) {
                //活体检查
                if (this.detectLiveFace(driverFaceModelForm.getImageBase64())) {
                    DriverFaceRecognition driverFaceRecognition = new DriverFaceRecognition();
                    driverFaceRecognition.setDriverId(driverFaceModelForm.getDriverId());
                    driverFaceRecognition.setFaceDate(new Date());
                    driverFaceRecognitionMapper.insert(driverFaceRecognition);
                    return true;
                }
                ;
            }
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        throw new VocmiException(ResultCodeEnum.FACE_FAIL);
    }

    @Transactional
    @Override
    public Boolean updateServiceStatus(Long driverId, Integer status) {
        LambdaQueryWrapper<DriverSet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DriverSet::getDriverId, driverId);
        DriverSet driverSet = new DriverSet();
        driverSet.setServiceStatus(status);
        driverSetMapper.update(driverSet, queryWrapper);
        return true;
    }

    /**
     * 人脸静态活体检测
     * 文档地址：
     * https://cloud.tencent.com/document/api/867/48501
     * https://console.cloud.tencent.com/api/explorer?Product=iai&Version=2020-03-03&Action=DetectLiveFace
     *
     * @param imageBase64
     * @return
     */
    private Boolean detectLiveFace(String imageBase64) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(tencentCloudProperties.getSecretId(), tencentCloudProperties.getSecretKey());
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("iai.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            IaiClient client = new IaiClient(cred, tencentCloudProperties.getRegion(), clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DetectLiveFaceRequest req = new DetectLiveFaceRequest();
            req.setImage(imageBase64);
            // 返回的resp是一个DetectLiveFaceResponse的实例，与请求对象对应
            DetectLiveFaceResponse resp = client.DetectLiveFace(req);
            // 输出json格式的字符串回包
            System.out.println(DetectLiveFaceResponse.toJsonString(resp));
            if (resp.getIsLiveness()) {
                return true;
            }
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return false;
    }

    @Override
    public Boolean updateDriverAuthInfo(UpdateDriverAuthInfoForm updateDriverAuthInfoForm) {
        DriverInfo driverInfo = BeanUtil.copyProperties(updateDriverAuthInfoForm, DriverInfo.class);
        driverInfo.setId(updateDriverAuthInfoForm.getDriverId());
        return this.updateById(driverInfo);
    }
}