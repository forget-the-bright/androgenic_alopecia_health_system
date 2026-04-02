package com.hairloss.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hairloss.system.entity.SysConfig;

import java.util.List;

/**
 * 系统配置服务接口
 */
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 根据键获取配置值
     * @param key 配置键
     * @return 配置值
     */
    String getConfigValue(String key);

    /**
     * 根据键获取配置实体
     * @param key 配置键
     * @return 配置实体
     */
    SysConfig getByKey(String key);

    /**
     * 更新配置
     * @param key 配置键
     * @param value 配置值
     * @return 更新结果
     */
    boolean updateConfig(String key, String value);

    /**
     * 分页获取所有配置
     * @param page 分页参数
     * @param type 配置类型（可选）
     * @return 分页结果
     */
    Page<SysConfig> getConfigPage(Page<SysConfig> page, String type);

    /**
     * 添加配置
     * @param config 配置信息
     * @return 添加结果
     */
    boolean addConfig(SysConfig config);

    /**
     * 根据 ID 更新配置
     * @param config 配置信息
     * @return 更新结果
     */
    boolean updateConfigById(SysConfig config);
}
