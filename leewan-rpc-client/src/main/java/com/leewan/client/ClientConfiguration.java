package com.leewan.client;

import com.leewan.share.configuration.Configuration;
import lombok.Data;

@Data
public class ClientConfiguration extends Configuration {
    private String remoteAddress = "localhost";
}
