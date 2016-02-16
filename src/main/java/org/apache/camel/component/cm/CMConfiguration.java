package org.apache.camel.component.cm;

import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UriParams
public class CMConfiguration {

    private static final Logger LOG = LoggerFactory
            .getLogger(CMConfiguration.class);

    @UriParam
    @NotNull
    private UUID productToken;

    /**
     * Required. This is the sender name. The maximum length is 11 characters.
     */
    @UriParam
    @NotNull
    @Size(min = 1, max = 11)
    private String defaultFrom;

    /**
     * If it is a multipart message forces the max number. Message can be
     * truncated. Technically the gateway will first check if a message is
     * larger than 160 characters, if so, the message will be cut into multiple
     * 153 characters parts limited by these parameters. <br>
     * {@link https://dashboard.onlinesmsgateway.com/docs#send-a-message-multipart}
     */
    @UriParam
    @Max(8)
    @Min(1)
    private int defaultMaxNumberOfParts = 8;

    @UriParam
    private boolean testConnectionOnStartup = false;

    public UUID getProductToken() {
        return productToken;
    }

    public void setProductToken(String tokenAsString) {
        productToken = UUID.fromString(tokenAsString);
    }

    public String getDefaultFrom() {
        return defaultFrom;
    }

    public void setDefaultFrom(String defaultFrom) {
        this.defaultFrom = defaultFrom;
    }

    public int getDefaultMaxNumberOfParts() {
        return defaultMaxNumberOfParts;
    }

    public void setDefaultMaxNumberOfParts(int defaultMaxNumberOfParts) {
        this.defaultMaxNumberOfParts = defaultMaxNumberOfParts;
    }

    public boolean isTestConnectionOnStartup() {
        return testConnectionOnStartup;
    }

    public void setTestConnectionOnStartup(boolean testConnectionOnStartup) {
        this.testConnectionOnStartup = testConnectionOnStartup;
    }

    public void setProductToken(UUID productToken) {
        this.productToken = productToken;
    }
}
