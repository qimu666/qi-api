package com.qimu.qiapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qimu.qiapibackend.common.ErrorCode;
import com.qimu.qiapibackend.exception.BusinessException;
import com.qimu.qiapibackend.mapper.UserInterfaceInvokeMapper;
import com.qimu.qiapibackend.model.entity.InterfaceInfo;
import com.qimu.qiapibackend.model.entity.UserInterfaceInvoke;
import com.qimu.qiapibackend.model.vo.UserVO;
import com.qimu.qiapibackend.service.InterfaceInfoService;
import com.qimu.qiapibackend.service.UserInterfaceInvokeService;
import com.qimu.qiapibackend.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.qimu.qiapibackend.model.enums.UserAccountStatusEnum.BAN;

/**
 * @Author: QiMu
 * @Date: 2023/09/04 11:30:02
 * @Version: 1.0
 * @Description: 用户界面调用服务impl
 */
@Service
public class UserInterfaceInvokeServiceImpl extends ServiceImpl<UserInterfaceInvokeMapper, UserInterfaceInvoke>
        implements UserInterfaceInvokeService {
    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean invoke(InterfaceInfo interfaceInfo, UserVO userVO) {
        if (ObjectUtils.anyNull(interfaceInfo, userVO) || interfaceInfo.getId() <= 0 || userVO.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long interfaceInfoId = interfaceInfo.getId();
        Long userId = userVO.getId();
        String userAccount = userVO.getUserAccount();
        Integer status = userVO.getStatus();
        Integer balance = userVO.getBalance();
        if (status.equals(BAN.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该账号已封禁");
        }
        if (balance <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "余额不足，请先充值。");
        }
        synchronized (userAccount.intern()) {
            LambdaQueryWrapper<UserInterfaceInvoke> invokeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            invokeLambdaQueryWrapper.eq(UserInterfaceInvoke::getInterfaceId, interfaceInfoId);
            invokeLambdaQueryWrapper.eq(UserInterfaceInvoke::getUserId, userId);
            UserInterfaceInvoke userInterfaceInvoke = this.getOne(invokeLambdaQueryWrapper);
            // 不存在就创建一条记录
            boolean invokeResult = false;
            if (userInterfaceInvoke == null) {
                userInterfaceInvoke = new UserInterfaceInvoke();
                userInterfaceInvoke.setInterfaceId(interfaceInfoId);
                userInterfaceInvoke.setUserId(userId);
                userInterfaceInvoke.setTotalInvokes(1L);
                invokeResult = this.save(userInterfaceInvoke);
            } else {
                LambdaUpdateWrapper<UserInterfaceInvoke> invokeLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                invokeLambdaUpdateWrapper.eq(UserInterfaceInvoke::getInterfaceId, interfaceInfoId);
                invokeLambdaUpdateWrapper.eq(UserInterfaceInvoke::getUserId, userId);
                invokeLambdaUpdateWrapper.setSql("totalInvokes = totalInvokes + 1");
                invokeResult = this.update(invokeLambdaUpdateWrapper);
            }
            // 更新接口总调用次数
            boolean interfaceUpdateInvokeSave = interfaceInfoService.updateTotalInvokes(interfaceInfoId);
            // 更新用户钱包积分
            boolean reduceWalletBalanceResult = userService.reduceWalletBalance(userId, interfaceInfo.getReduceScore());
            boolean updateResult = invokeResult && interfaceUpdateInvokeSave && reduceWalletBalanceResult;
            if (!updateResult) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用失败");
            }
            return true;
        }
    }
}




