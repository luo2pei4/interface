package com.lp.core;

import com.lp.connector.internal.InternalConnector;
import com.lp.common.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class InternalConnectorManagement {

    private static Map<String, InternalConnector> internalConnectorMap = new HashMap<>();

    /**
     * 向内部连接器管理Map中增加内部连接器
     *
     * @param name
     * @param internalConnector
     */
    public static void addInternalConnector(String name, InternalConnector internalConnector) {

        internalConnectorMap.put(name, internalConnector);
    }

    public static InternalConnector getInternalConnector(String name) {

        if (!StringUtil.isEmpty(name)) {

            return internalConnectorMap.get(name);

        } else {

            return null;
        }
    }
}
