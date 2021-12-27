package com.leewan.server.filter;

import com.leewan.share.message.RequestMessage;

import java.io.IOException;

public interface FilterChain {
    void doFilter(RequestMessage request);
}
