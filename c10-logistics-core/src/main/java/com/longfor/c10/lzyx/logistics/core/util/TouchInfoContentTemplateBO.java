package com.longfor.c10.lzyx.logistics.core.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 内容模板信息表
 * </p>
 *
 * @author jisai
 * @since 2021-09-23 16:08:34
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TouchInfoContentTemplateBO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id主键 id varchar(64)  PK
     */
    private String id;
    /**
     * 消息code code varchar(128)
     */
    private String code;
    /**
     * 消息类型（文字消息；卡片消息） com.longfor.c10.lzyx.touch.entity.enums.TouchBotMessgeTypeEnum
     */
    private String type;
    /**
     * 消息名称 name varchar(256)
     */
    private String name;
    /**
     * 消息样式（样式1；样式2） message_style varchar(255)
     */
    private String messageStyle;
    /**
     * 消息标题 message_title varchar(255)
     */
    private String messageTitle;
    /**
     * 消息内容 message_content varchar(255)
     */
    private String messageContent;
    /**
     * 封面图链接 cover_picture_link varchar(255)
     */
    private String coverPictureLink;
    /**
     * 封面图缩略图链接 cover_picture_thumbnail_link varchar(255)
     */
    private String coverPictureThumbnailLink;
    /**
     * 是否允许跳转（是true；否false） allow_jump varchar(10)
     */
    private String allowJump;
    /**
     * 跳转url jump_url varchar(1024)
     */
    private String jumpUrl;
    /**
     * 状态（草稿；可用） status varchar(255)
     */
    private String status;
    /**
     * 是否删除（1是；0否） deleted tinyint(4)
     */
    private Integer deleted;
    /**
     * 创建人姓名 creator varchar(64)
     */
    private String creator;
    /**
     * 创建时间 create_time datetime(3)
     */
    private Date createTime;
    /**
     * 更新人姓名 updater varchar(64)
     */
    private String updater;
    /**
     * 更新时间 update_time datetime(3)
     */
    private Date updateTime;

}
    