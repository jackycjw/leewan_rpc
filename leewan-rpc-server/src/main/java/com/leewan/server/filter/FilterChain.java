package com.leewan.server.filter;

import com.leewan.share.message.RequestMessage;
import com.leewan.share.message.ResponseMessage;

import java.io.IOException;

public interface FilterChain {
    void doFilter(RequestMessage request, ResponseMessage response);
}
