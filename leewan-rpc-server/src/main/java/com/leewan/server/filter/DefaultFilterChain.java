package com.leewan.server.filter;

import com.leewan.share.message.RequestMessage;
import com.leewan.share.message.ResponseMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DefaultFilterChain implements FilterChain {
    /**
     *
     */
    private List<Filter> filters = new ArrayList<>(1);

    private ThreadLocal<Integer> index = new ThreadLocal<>();

    public void addFilter(Filter filter){
        filters.add(filter);
    }

    private Filter next(){
        Filter filter = null;
        Integer filterIndex = index.get();
        if (filterIndex == null) {
            filterIndex = 0;
        }
        if (filterIndex < filters.size()) {
            filter = filters.get(filterIndex);
            index.set(filterIndex+1);
        }
        return filter;
    }

    @Override
    public void doFilter(RequestMessage request, ResponseMessage response) {
        Filter filter = next();
        if (filter != null) {
            try {
                filter.doFilter(request, response, this);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                response.setExceptionMessage(response.getExceptionMessage());
            }
        }
    }


}
