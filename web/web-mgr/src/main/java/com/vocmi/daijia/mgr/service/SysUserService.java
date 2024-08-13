package com.vocmi.daijia.mgr.service;


import com.vocmi.daijia.model.entity.system.SysUser;
import com.vocmi.daijia.model.query.system.SysUserQuery;
import com.vocmi.daijia.model.vo.base.PageVo;

public interface SysUserService {

    SysUser getById(Long id);

    void save(SysUser sysUser);

    void update(SysUser sysUser);

    void remove(Long id);

    PageVo<SysUser> findPage(Long page, Long limit, SysUserQuery sysUserQuery);

    void updateStatus(Long id, Integer status);


}
