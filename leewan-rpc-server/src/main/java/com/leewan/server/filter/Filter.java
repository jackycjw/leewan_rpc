package com.leewan.server.filter;

import com.leewan.share.message.RequestMessage;

public interface Filter {

    void doFilter(RequestMessage request);
}
