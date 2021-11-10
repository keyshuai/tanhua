package com.tanhua.server.interceptor;

import com.tanhua.model.domain.User;

public class UserHolder {
    private static  ThreadLocal<User>t1 =new ThreadLocal<>();

    public static void set(User user){
        t1.set(user);
    }

    public static User get(){
        return t1.get();
    }

    public static Long getUserId(){
        return t1.get().getId();
    }

    public static String getMobile(){
        return t1.get().getMobile();
    }

    //清空
    public static void remove(){
        t1.remove();
    }
}
