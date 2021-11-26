package com.tanhua.model.enums;

/**
 * 评论类型：1-点赞，2-评论，3-喜欢,4-关注,5-视频点赞,6-视频点赞            喜欢
 */
public enum CommentType {

    LIKE(1), COMMENT(2), LOVE(3),ATTENTION(4),LIKES(5),LOVES(6);

    int type;

    CommentType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}