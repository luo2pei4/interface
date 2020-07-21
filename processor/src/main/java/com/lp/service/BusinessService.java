package com.lp.service;

import com.alibaba.fastjson.JSONObject;
import com.lp.connector.internal.InternalConnector;
import com.lp.entity.AdsbEntity;
import com.lp.entity.FlightInfoEntity;
import com.lp.common.exception.AdapterConnectException;
import com.lp.repository.AdsbRepository;
import com.lp.repository.FlightInfoRepository;
import com.lp.common.utils.DateUtil;
import com.lp.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 业务服务
 *
 * @author luo2p
 * @since 2020/07/19
 */
@Component
public class BusinessService extends Thread {

    private static Logger logger = LoggerFactory.getLogger(BusinessService.class);

    @Autowired
    private InternalConnector internalConnector;

    @Autowired
    private AdsbRepository adsbRepository;

    @Autowired
    private FlightInfoRepository flightInfoRepository;

    @Value("${app.name}")
    private String appName;

    @Override
    public void run() {

        while (true) {

            try {

                if (!internalConnector.isConnected()) {

                    internalConnector.connect();

                } else {

                    String msg = internalConnector.receive();

                    if (!StringUtil.isEmpty(msg)) {

                        logger.info("收到消息: " + msg);

                        JSONObject jsonObject = JSONObject.parseObject(msg);

                        AdsbEntity adsbEntity = new AdsbEntity();
                        adsbEntity.setArr(jsonObject.getString("arr"));
                        adsbEntity.setDep(jsonObject.getString("dep"));
                        adsbEntity.setFi(jsonObject.getString("fi"));
                        adsbEntity.setIcao24(jsonObject.getString("icao24"));
                        adsbEntity.setAn(jsonObject.getString("an"));
                        adsbEntity.setAlt(jsonObject.getString("alt"));
                        adsbEntity.setLon(jsonObject.getString("lon"));
                        adsbEntity.setCas(jsonObject.getString("cas"));
                        adsbEntity.setLat(jsonObject.getString("lat"));

                        if (StringUtil.isEmpty(jsonObject.getString("vec"))) {

                            adsbEntity.setVec(BigDecimal.valueOf(0.0));

                        } else {

                            adsbEntity.setVec(jsonObject.getBigDecimal("vec"));
                        }

                        adsbEntity.setSsr(jsonObject.getString("ssr"));
                        adsbEntity.setType(jsonObject.getString("type"));
                        adsbEntity.setSendTime(DateUtil.getLocalDateTime(jsonObject.getString("sendTime"), "yyyyMMddHHmmss"));
                        adsbEntity.setThreadid(appName);
                        adsbEntity.setCreatetime(LocalDateTime.now());
                        adsbRepository.saveAndFlush(adsbEntity);

                        FlightInfoEntity flightInfoEntity = flightInfoRepository.searchEntityByAn(jsonObject.getString("an"));

                        if (flightInfoEntity == null) {

                            flightInfoEntity = new FlightInfoEntity();
                            flightInfoEntity.setAn(jsonObject.getString("an"));
                            flightInfoEntity.setUpdatetimes(0);

                        } else {

                            Integer updateTimes = flightInfoEntity.getUpdatetimes();
                            flightInfoEntity.setUpdatetimes(updateTimes + 1);
                        }

                        flightInfoEntity.setArr(jsonObject.getString("arr"));
                        flightInfoEntity.setDep(jsonObject.getString("dep"));
                        flightInfoEntity.setFi(jsonObject.getString("fi"));
                        flightInfoEntity.setIcao24(jsonObject.getString("icao24"));
                        flightInfoEntity.setAlt(jsonObject.getString("alt"));
                        flightInfoEntity.setLon(jsonObject.getString("lon"));
                        flightInfoEntity.setCas(jsonObject.getString("cas"));
                        flightInfoEntity.setLat(jsonObject.getString("lat"));

                        if (StringUtil.isEmpty(jsonObject.getString("vec"))) {

                            adsbEntity.setVec(BigDecimal.valueOf(0.0));

                        } else {

                            adsbEntity.setVec(jsonObject.getBigDecimal("vec"));
                        }

                        flightInfoEntity.setSsr(jsonObject.getString("ssr"));
                        flightInfoEntity.setType(jsonObject.getString("type"));
                        flightInfoEntity.setSendTime(DateUtil.getLocalDateTime(jsonObject.getString("sendTime"), "yyyyMMddHHmmss"));
                        flightInfoEntity.setThreadid(appName);
                        flightInfoEntity.setCreatetime(LocalDateTime.now());

                        flightInfoRepository.saveAndFlush(flightInfoEntity);

                    } else {

                        sleep(1000);
                    }
                }

            } catch (AdapterConnectException adapterConnectException) {

                logger.error(adapterConnectException.getBusinessMessage(), adapterConnectException);

                internalConnector.close();

                try {

                    sleep(5000);

                } catch (InterruptedException interruptedException) {

                    logger.error("线程休眠异常", interruptedException);
                }

            } catch (Exception exception) {

                logger.error("业务处理异常", exception);

                // 关闭连接
                internalConnector.close();

                try {

                    sleep(5000);

                } catch (InterruptedException interruptedException) {

                    logger.error("线程休眠异常", interruptedException);
                }
            }
        }
    }
}
