package com.github.gallery;

/**
 * Created by ZhongXiaolong on 2020/12/15 6:10 PM.
 * <p>
 * 扫描结果返回对象
 */
class LoadResultData {

    public static final int CODE_SUCCESS= 200;
    public static final int CODE_NO_PERMISSION = 503;

    /**
     * 200,正确
     * 503,没有权限
     */
    private final int code;

    public LoadResultData(int code) {
        this.code = code;
    }

    /**
     * @return errorCode
     */
    public int getCode() {
        return code;
    }

}
