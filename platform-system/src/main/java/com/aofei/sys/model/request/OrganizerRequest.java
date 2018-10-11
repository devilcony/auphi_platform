package com.aofei.sys.model.request;

import com.aofei.base.entity.DataEntity;
import com.aofei.base.model.request.BaseRequest;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 组织信息
 * </p>
 *
 * @author Tony
 * @since 2018-10-09
 */
@Data
public class OrganizerRequest extends BaseRequest<OrganizerRequest> {

    private static final long serialVersionUID = 1L;

    /**
     * 组织ID
     */
    private Long organizerId;
    /**
     * 组织名称
     */
    private String organizerName;

    private String organizerContact;

    private String organizerEmail;

    private String organizerTelphone;
    @TableField("ORGANIZER_MOBILE")
    private String organizerMobile;
    @TableField("ORGANIZER_ADDRESS")
    private String organizerAddress;
    @TableField("ORGANIZER_VERIFY_CODE")
    private String organizerVerifyCode;
    /**
     * 0 已注册未验证通过，1已注册并验证通过， 2 注销
     */
    @TableField("ORGANIZER_STATUS")
    private Integer organizerStatus;




}
