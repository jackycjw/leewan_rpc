package com.leewan.rpc.share.internal.service;

import com.leewan.rpc.share.internal.dto.HearBeatDTO;

public interface HeartBeatService {

    HearBeatDTO send(HearBeatDTO dto);
    
}
