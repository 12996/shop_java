package com.yami.shop.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Dev environment debug token response body.
 */
@Data
public class DevTokenInfoVO {

    @Schema(description = "accessToken")
    private String accessToken;

    @Schema(description = "refreshToken")
    private String refreshToken;

    @Schema(description = "expiresIn")
    private Integer expiresIn;

    @Schema(description = "token request header name")
    private String tokenName;
}
