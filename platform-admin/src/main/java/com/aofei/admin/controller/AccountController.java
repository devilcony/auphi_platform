/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2018 by Auphi BI : http://www.doetl.com

 * Support：support@pentahochina.com
 *
 *******************************************************************************
 *
 * Licensed under the LGPL License, Version 3.0 the "License";
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/LGPL-3.0

 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package com.aofei.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aofei.admin.authorization.Token;
import com.aofei.admin.authorization.jwt.JwtConfig;
import com.aofei.admin.authorization.jwt.JwtTokenBuilder;
import com.aofei.base.common.Const;
import com.aofei.base.common.UserUtil;
import com.aofei.base.controller.BaseController;
import com.aofei.base.exception.ApplicationException;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.model.response.Response;
import com.aofei.sys.entity.User;
import com.aofei.sys.exception.SystemError;
import com.aofei.sys.model.request.EmailRegisterRequest;
import com.aofei.sys.model.request.PhoneRegisterRequest;
import com.aofei.sys.model.request.UserRequest;
import com.aofei.sys.model.response.UserResponse;
import com.aofei.sys.service.IUserService;
import com.aofei.utils.BeanCopier;
import com.aofei.utils.SendMailUtil;
import com.aofei.utils.StringUtils;
import com.aofei.utils.TencentSmsSingleSender;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
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
    private SendMailUtil sendMailUtil;

    @Autowired
    private TencentSmsSingleSender tencentSmsSingleSender;

    /**
     * 登录
     *
     * @return
     */
    @ApiOperation(value = "用户名登录", notes = "用户登录返回Token,后期访问接口在head中添加Authorization={Token}", httpMethod = "POST")
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
            userRequest.setLastLoginIp(host);
            userRequest.setLastLoginTime(new Date());
            userService.updateLogin(userRequest);

            Map map = new HashMap();
            map.put(Const.TOKEN_KEY, user.getUsername());
            String subject = JwtTokenBuilder.buildSubject(map);

            String accessToken = jwtTokenBuilder.buildToken(subject, jwtConfig.getExpiresSecond(), jwtConfig.getBase64Secret());
            //String refreshToken = jwtTokenBuilder.buildToken(subject, jwtConfig.getRefreshExpiresSecond(), jwtConfig.getRefreshBase64Secret());
            Token token = new Token();
            token.setAccess_token(accessToken);
            //token.setRefresh_token(refreshToken);
            token.setToken_type("bearer");
            token.setExpires_in(jwtConfig.getExpiresSecond());

            //存储到redis
            //tokenManager.createRelationship(user.getUsername(), accessToken);


            return Response.ok(token);
        } else {
            throw new ApplicationException(SystemError.LOGIN_FAILED.getCode(), SystemError.LOGIN_FAILED.getMessage());
        }
    }


    /**
     * 登录
     *
     * @return
     */
    @ApiOperation(value = "手机号验证码登录", notes = "用户登录返回Token,后期访问接口在head中添加Authorization={Token}", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200001, message = "invalid username or password"),
            @ApiResponse(code = 200002, message = "captcha error"),
            @ApiResponse(code = 200, message = "success")})
    @RequestMapping(value = "/login/phone", method = RequestMethod.POST)
    @ResponseBody
    public Response<Token> login_phone(
            @ApiParam(value = "国家代码", required = true)  @RequestParam(value = "countryCode") String countryCode,
            @ApiParam(value = "手机号",   required = true)  @RequestParam(value = "mobilephone") String mobilephone,
            @ApiParam(value = "验证码", required = true)  @RequestParam(value = "captcha") String captcha) throws Exception {

        if(tencentSmsSingleSender.validate(countryCode,mobilephone,captcha)){

            User user = userService.selectOne(new EntityWrapper<User>()
                    .eq("C_MOBILEPHONE",countryCode)
                    .eq("C_COUNTRY_CODE",mobilephone)
                    .eq("DEL_FLAG",User.DEL_FLAG_NORMAL));

            UserUtil.setSessionUser(BeanCopier.copy(user, CurrentUserResponse.class));
            String host = StringUtils.getRemoteAddr();
            UserRequest userRequest = new UserRequest(user.getUserId());
            userRequest.setLastLoginIp(host);
            userRequest.setLastLoginTime(new Date());
            userService.updateLogin(userRequest);


            Map map = new HashMap();
            map.put(Const.TOKEN_KEY, user.getUsername());
            String subject = JwtTokenBuilder.buildSubject(map);

            String accessToken = jwtTokenBuilder.buildToken(subject, jwtConfig.getExpiresSecond(), jwtConfig.getBase64Secret());
            //String refreshToken = jwtTokenBuilder.buildToken(subject, jwtConfig.getRefreshExpiresSecond(), jwtConfig.getRefreshBase64Secret());
            Token token = new Token();
            token.setAccess_token(accessToken);
            //token.setRefresh_token(refreshToken);
            token.setToken_type("bearer");
            token.setExpires_in(jwtConfig.getExpiresSecond());

            //存储到redis
            //tokenManager.createRelationship(user.getUsername(), accessToken);


            return Response.ok(token);

        }else{
            throw new ApplicationException(SystemError.CAPTCHA_ERROR.getCode(),"the captcha error");
        }

    }


    /**
     * 注销
     *
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Integer logout(@RequestHeader(name = "Authorization") String token) {
        //TODO JWT不能从服务端destroy token， logout目前只能在客户端的cookie 或 localStorage/sessionStorage  remove token
        //TODO 准备用jwt生成永久的token，再结合redis来实现Logout。具体是把token的生命周期交给redis来管理，jwt只负责生成token
        try {

            return 1;
        } catch (Exception e) {
            return -1;
        }
    }







    /**
     * 检查用户名是否存在
     *
     * @return
     */
    @ApiOperation(value = "检查用户名是否存在", notes = "检查用户名是否存在 0:不存在 1:存在 ", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")})
    @RequestMapping(value = "/{username}/exist", method = RequestMethod.POST)
    public Response<Integer> checkUsernameIsExist(@PathVariable String username) throws Exception {
        int count = userService.selectCount(new EntityWrapper<User>()
                .eq("C_USER_NAME",username)
                .eq("DEL_FLAG",User.DEL_FLAG_NORMAL));
        return Response.ok(count);
    }

    /**
     * 手机号注册
     *
     * @return
     */
    @ApiOperation(value = "手机号注册", notes = "手机号注册", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200005, message = "the username is exist"),
            @ApiResponse(code = 200013, message = "the phone number is exist"),
            @ApiResponse(code = 200, message = "success")})
    @RequestMapping(value = "/register/phone", method = RequestMethod.POST)
    public Response<Integer> phoneRegister(@RequestBody PhoneRegisterRequest request) throws Exception {

        int usernameCount = userService.selectCount(new EntityWrapper<User>()
                .eq("C_USER_NAME",request.getMobilephone())
                .eq("DEL_FLAG",User.DEL_FLAG_NORMAL));
        if(usernameCount>0){
            throw new ApplicationException(SystemError.USERNAME_EXIST.getCode(),"the username is exist");
        }
        int mobilephoneCount = userService.selectCount(new EntityWrapper<User>()
                .eq("C_MOBILEPHONE",request.getMobilephone())
                .eq("C_COUNTRY_CODE",request.getCountryCode())
                .eq("DEL_FLAG",User.DEL_FLAG_NORMAL));
        if(mobilephoneCount>0){
            throw new ApplicationException(SystemError.PHONE_NUMBER_EXIST.getCode(),"the phone number is exist");
        }
        if(tencentSmsSingleSender.validate(request.getCountryCode(),request.getMobilephone(),request.getCaptcha())){
            userService.register(request);
        }else{
          throw new ApplicationException(SystemError.CAPTCHA_ERROR.getCode(),"the captcha error");
        }

        return Response.ok(0);
    }


    /**
     * 邮箱用户注册(该接口只发送邮件，用户在邮件点击列表完成注册)
     *
     * @return
     */

    @ApiOperation(value = "邮箱用户注册", notes = "邮箱用户注册(该接口只发送邮件，用户在邮件点击列表完成注册)", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200005, message = "username is exist"),
            @ApiResponse(code = 200011, message = "email is exist"),
            @ApiResponse(code = 200, message = "success")})
    @RequestMapping(value = "/register/email", method = RequestMethod.POST)
    public Response<Integer> emailRegister(@RequestBody EmailRegisterRequest request) throws Exception {

        int usernameCount = userService.selectCount(new EntityWrapper<User>().eq("C_USER_NAME",request.getUsername()));
        if(usernameCount>0){
            throw new ApplicationException(SystemError.USERNAME_EXIST.getCode(),"username is exist");
        }

        int emailCount = userService.selectCount(new EntityWrapper<User>().eq("C_EMAIL",request.getEmail()));
        if(emailCount>0){
            throw new ApplicationException(SystemError.EMAIL_EXIST.getCode(),"email is exist");
        }

        String json =  JSON.toJSONString(request);


        String accessToken = jwtTokenBuilder.buildToken(json, 1000*60*60*24, jwtConfig.getBase64Secret());

        sendMailUtil.sendHtmlMail(request.getEmail(),"傲飞数据整合平台-用户注册认证",accessToken,null);

        //TODO发送邮件继续注册

        return Response.ok(0);
    }

    /**
     * 邮箱验证
     *
     * @return
     */
    @ApiOperation(value = "邮箱验证", notes = "邮箱验证", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200012, message = "mail overdue"),
            @ApiResponse(code = 200, message = "success")})
    @RequestMapping(value = "/register/active", method = RequestMethod.POST)
    public Response<Integer> active(
            @ApiParam(value = "验证码", required = true)String auth_code) throws Exception {



        Claims claims = jwtTokenBuilder.decodeToken(auth_code, jwtConfig.getBase64Secret());
        if(claims==null){
            throw  new ApplicationException(SystemError.ACTIVE_OVERDUE.getCode(),"mail overdue");
        }else{
            String subject = claims.getSubject();//用户信息
            PhoneRegisterRequest request = JSON.parseObject(subject, new TypeReference<PhoneRegisterRequest>() {});
            userService.register(request);
        }

        return Response.ok(0);
    }

}
