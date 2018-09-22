package com.aofei.admin.controller;

import com.aofei.admin.authorization.Token;
import com.aofei.admin.authorization.jwt.JwtConfig;
import com.aofei.admin.authorization.jwt.JwtTokenBuilder;
import com.aofei.authorization.manager.TokenManager;
import com.aofei.base.common.UserUtil;
import com.aofei.base.controller.BaseController;
import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.model.response.Response;
import com.aofei.sys.exception.SystemError;
import com.aofei.sys.model.request.UserRequest;
import com.aofei.sys.model.response.UserResponse;
import com.aofei.sys.service.IUserService;
import com.aofei.utils.BeanCopier;
import com.aofei.utils.StringUtils;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther Tony
 * @create 2018-09-15 15:45
 */
@Api(tags = { "系统管理-登录认证模块接口" })
@RestController
@RequestMapping(value = "/auth", produces = {"application/json;charset=UTF-8"})
public class AccountController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private JwtTokenBuilder jwtTokenBuilder;

    @Autowired
    private TokenManager tokenManager;



    /**
     * 登录
     *
     * @return
     */
    @ApiOperation(value = "用户登录", notes = "用户登录返回Token", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200001, message = "invalid username or password"),
            @ApiResponse(code = 200002, message = "captcha error"),
            @ApiResponse(code = 200, message = "success")})
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Response<Token> login(
            @ApiParam(value = "用户名", required = true)  @RequestParam(value = "username") String username,
            @ApiParam(value = "密码",   required = true)  @RequestParam(value = "password") String password,
            @ApiParam(value = "验证码", required = true)  @RequestParam(value = "captcha") String captcha) throws Exception {

        //验证码校验
        UserResponse user = null;
        try {
            //用户名密码验证
            user = userService.auth(username, password);
        }catch (ApplicationException e){
            throw new ApplicationException(SystemError.LOGIN_FAILED.getCode(), SystemError.LOGIN_FAILED.getMessage());
        }

        if(user != null) {
            UserUtil.setSessionUser(BeanCopier.copy(user, CurrentUserResponse.class));
            String host = StringUtils.getRemoteAddr();
            UserRequest userRequest = new UserRequest(user.getUserId());
            userRequest.setLoginIp(host);
            userRequest.setLoginTime(new Date());
            userService.updateLogin(userRequest);

            Map map = new HashMap();
            map.put("username", user.getUsername());
            String subject = JwtTokenBuilder.buildSubject(map);

            String accessToken = jwtTokenBuilder.buildToken(subject, jwtConfig.getExpiresSecond(), jwtConfig.getBase64Secret());
            String refreshToken = jwtTokenBuilder.buildToken(subject, jwtConfig.getRefreshExpiresSecond(), jwtConfig.getRefreshBase64Secret());
            Token token = new Token();
            token.setAccess_token(accessToken);
            token.setRefresh_token(refreshToken);
            token.setToken_type("bearer");
            token.setExpires_in(jwtConfig.getExpiresSecond());

            //存储到redis
            tokenManager.createRelationship(user.getUsername(), accessToken);

            return Response.ok(token);
        } else {
            throw new ApplicationException(SystemError.LOGIN_FAILED.getCode(), SystemError.LOGIN_FAILED.getMessage());
        }

    }

    /**
     * 刷新token，获取新的token
     *
     * @param refresh_token
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "刷新token，获取新的token", notes = "刷新token，获取新的token", httpMethod = "GET")
    @RequestMapping(value = "/refresh_token/{refresh_token}", method = RequestMethod.GET)
    @ResponseBody
    public Response<Token> refreshToken(@PathVariable String refresh_token) throws Exception {

        //验证token
        Claims claims = jwtTokenBuilder.decodeToken(refresh_token, jwtConfig.getRefreshBase64Secret());
        if (claims != null) {
            //如果token验证成功，返回新的token
            String subject = claims.getSubject();//用户信息
            String accessToken = jwtTokenBuilder.buildToken(subject, jwtConfig.getExpiresSecond(), jwtConfig.getBase64Secret());
            String refreshToken = jwtTokenBuilder.buildToken(subject, jwtConfig.getRefreshExpiresSecond(), jwtConfig.getRefreshBase64Secret());

            Token token = new Token();
            token.setAccess_token(accessToken);
            token.setRefresh_token(refreshToken);
            token.setToken_type("bearer");
            token.setExpires_in(jwtConfig.getExpiresSecond());

            Map<String,String> map = JwtTokenBuilder.decodeSubject(subject);
            //存储到redis
            tokenManager.createRelationship(map.get("username"), accessToken);

            return Response.ok(token);
        } else {
            throw new ApplicationException(StatusCode.UNAUTHORIZED.getCode(), "invalid refresh token");
        }
    }

    /**
     * 注销
     *
     * @return
     */
    @ApiOperation(value = "注销登录", notes = "注销登录", httpMethod = "POST")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Integer logout(@RequestHeader(name = "Authorization") String token) {
        //TODO 操蛋的JWT不能从服务端destroy token， logout目前只能在客户端的cookie 或 localStorage/sessionStorage  remove token
        //TODO 准备用jwt生成永久的token，再结合redis来实现Logout。具体是把token的生命周期交给redis来管理，jwt只负责生成token
        try {
            //多端登录，会有多个同一用户名但token不一样的键值对在redis中存在，所以只能通过token删除
           // tokenManager.delRelationshipByKey(user.getUsername());
            tokenManager.delRelationshipByToken(token);//注销成功
            return 1;
        } catch (Exception e) {
            return -1;
        }
    }

}
