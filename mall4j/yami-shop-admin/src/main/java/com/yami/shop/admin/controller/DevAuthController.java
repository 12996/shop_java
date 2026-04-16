package com.yami.shop.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yami.shop.admin.vo.DevTokenInfoVO;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.security.common.bo.UserInfoInTokenBO;
import com.yami.shop.security.common.enums.SysTypeEnum;
import com.yami.shop.security.common.manager.TokenStore;
import com.yami.shop.security.common.vo.TokenInfoVO;
import com.yami.shop.sys.constant.Constant;
import com.yami.shop.sys.model.SysMenu;
import com.yami.shop.sys.model.SysUser;
import com.yami.shop.sys.service.SysMenuService;
import com.yami.shop.sys.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Dev-only endpoint for automation token bootstrap.
 */
@RestController
@Profile("dev")
@Tag(name = "Dev Auth")
public class DevAuthController {

    private static final long SUPER_ADMIN_USER_ID = Constant.SUPER_ADMIN_ID;

    @Value("${app.dev-auth.enabled:true}")
    private boolean devAuthEnabled;

    @Value("${sa-token.token-name}")
    private String tokenName;

    private final TokenStore tokenStore;

    private final SysUserService sysUserService;

    private final SysMenuService sysMenuService;

    public DevAuthController(TokenStore tokenStore, SysUserService sysUserService, SysMenuService sysMenuService) {
        this.tokenStore = tokenStore;
        this.sysUserService = sysUserService;
        this.sysMenuService = sysMenuService;
    }

    @PostMapping("/dev/auth/admin-token")
    @Operation(summary = "Issue a local dev admin token")
    public ServerResponseEntity<DevTokenInfoVO> adminToken(HttpServletRequest request) {
        if (!devAuthEnabled) {
            return ServerResponseEntity.showFailMsg("dev auth is disabled");
        }
        if (!isLocalRequest(request)) {
            return ServerResponseEntity.showFailMsg("dev auth only allows local requests");
        }

        SysUser admin = sysUserService.getById(SUPER_ADMIN_USER_ID);
        if (admin == null) {
            return ServerResponseEntity.showFailMsg("super admin user not found");
        }
        if (admin.getStatus() == null || admin.getStatus() != 1) {
            return ServerResponseEntity.showFailMsg("super admin is disabled");
        }

        UserInfoInTokenBO userInfoInToken = new UserInfoInTokenBO();
        userInfoInToken.setUserId(String.valueOf(admin.getUserId()));
        userInfoInToken.setSysType(SysTypeEnum.ADMIN.value());
        userInfoInToken.setEnabled(true);
        userInfoInToken.setPerms(getUserPermissions(admin.getUserId()));
        userInfoInToken.setNickName(admin.getUsername());
        userInfoInToken.setShopId(admin.getShopId());

        TokenInfoVO tokenInfoVO = tokenStore.storeAndGetVo(userInfoInToken);

        DevTokenInfoVO devTokenInfoVO = new DevTokenInfoVO();
        devTokenInfoVO.setAccessToken(tokenInfoVO.getAccessToken());
        devTokenInfoVO.setRefreshToken(tokenInfoVO.getRefreshToken());
        devTokenInfoVO.setExpiresIn(tokenInfoVO.getExpiresIn());
        devTokenInfoVO.setTokenName(tokenName);
        return ServerResponseEntity.success(devTokenInfoVO);
    }

    private Set<String> getUserPermissions(Long userId) {
        List<String> permsList;
        if (userId == SUPER_ADMIN_USER_ID) {
            List<SysMenu> menuList = sysMenuService.list(Wrappers.emptyWrapper());
            permsList = menuList.stream().map(SysMenu::getPerms).collect(Collectors.toList());
        } else {
            permsList = sysUserService.queryAllPerms(userId);
        }
        return permsList.stream().flatMap(perms -> {
            if (StrUtil.isBlank(perms)) {
                return Stream.empty();
            }
            return Arrays.stream(perms.trim().split(StrUtil.COMMA));
        }).collect(Collectors.toSet());
    }

    private boolean isLocalRequest(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        return "127.0.0.1".equals(remoteAddr)
                || "0:0:0:0:0:0:0:1".equals(remoteAddr)
                || "::1".equals(remoteAddr)
                || "localhost".equalsIgnoreCase(remoteAddr);
    }
}
