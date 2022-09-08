package com.leewan.rpc.server.internal;

import com.leewan.rpc.share.internal.dto.HearBeatDTO;
import com.leewan.rpc.share.internal.service.HeartBeatService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatServiceImpl implements HeartBeatService {
    @Override
    public HearBeatDTO send(HearBeatDTO dto) {
        log.info("receive heart beat");
        return new HearBeatDTO();
    }
}
