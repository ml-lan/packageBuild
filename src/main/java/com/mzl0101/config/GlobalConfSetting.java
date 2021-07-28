package com.mzl0101.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.Map;

/**
 * @创建人 mzl
 * @创建时间 2021/7/17 21:19
 * @描述
 */
@State(name = "GlobalConfSetting", storages = {@Storage(value = "GlobalConfSetting.xml")})
public class GlobalConfSetting implements PersistentStateComponent<GlobalConfSetting> {
    public Map<String, String> confPathMap; //全局配置路径
    public GlobalConfSetting getState() {
        return this;
    }

    public void loadState(GlobalConfSetting state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}