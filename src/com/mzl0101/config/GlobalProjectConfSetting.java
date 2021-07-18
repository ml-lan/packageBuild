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
@State(name = "GlobalProjectConfSetting", storages = {@Storage(value = "GlobalProjectConfSetting.xml")})
public class GlobalProjectConfSetting implements PersistentStateComponent<GlobalProjectConfSetting> {
    public String projectNum; //项目编号
    public GlobalProjectConfSetting getState() {
        return this;
    }

    public void loadState(GlobalProjectConfSetting state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}