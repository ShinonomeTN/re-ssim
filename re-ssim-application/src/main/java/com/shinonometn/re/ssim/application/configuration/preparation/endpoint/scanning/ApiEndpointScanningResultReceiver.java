package com.shinonometn.re.ssim.application.configuration.preparation.endpoint.scanning;

import java.util.Collection;
import java.util.Date;

public interface ApiEndpointScanningResultReceiver {
    void acceptResult(Collection<EndpointInformation> endpointInformation, Date timestamp);
}
