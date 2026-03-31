package com.hairloss.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hairloss.system.entity.SysConfig;
import com.hairloss.system.mapper.SysConfigMapper;
import com.hairloss.system.service.SysConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 系统配置服务实现类
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public String getConfigValue(String key) {
        SysConfig config = this.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, key));
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfig(String key, String value) {
        SysConfig config = this.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, key));

        if (config == null) {
            throw new RuntimeException("配置项不存在");
        }

        config.setConfigValue(value);
        return this.updateById(config);
    }

    @Override
    public Page<SysConfig> getConfigPage(Page<SysConfig> page, String type) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            wrapper.eq(SysConfig::getConfigType, type);
        }
        wrapper.orderByAsc(SysConfig::getConfigKey);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addConfig(SysConfig config) {
        // 检查配置键是否存在
        Long count = this.count(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, config.getConfigKey()));
        if (count > 0) {
            throw new RuntimeException("配置键已存在");
        }

        return this.save(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfigById(SysConfig config) {
        return this.updateById(config);
    }
}
