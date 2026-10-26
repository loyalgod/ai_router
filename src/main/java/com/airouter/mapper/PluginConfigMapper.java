/**
 * 插件配置 Mapper
 *
 * @author ai-router
 */
package com.airouter.mapper;

import com.mybatisflex.core.BaseMapper;
import com.airouter.model.entity.PluginConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PluginConfigMapper extends BaseMapper<PluginConfig> {
}
