package com.mzl0101.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.Map;

/**
 * @author mzl
 * @创建时间 2021/7/17 21:19
 * @描述
 */
@State(name = "GlobalConfig", storages = {@Storage(value = "GlobalConfig.xml")})
public class GlobalConfig implements PersistentStateComponent<GlobalConfig> {
    /**
     * 全局配置
     */
    public Map<String, String> globalConfigMap;

    @Override
    public GlobalConfig getState() {
        return this;
    }

    @Override
    public void loadState(GlobalConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}