package andfix.khch.andfixlibrary.apatchupdate.bean;

/**
 * Created by kuanghaochuan on 16/8/17.
 */
public class ApatchResponseBean {

    /**
     * 自定义关于获取apatch信息的json格式
     * code 请求状态码
     * apatchVersionCode 对应当前app的versionCode
     * apatchUrl 对应apatch文件的下载地址
     * apatchLength 对应apatch文件的size
     * apatchMD5 对应apatch文件的md5
     * showPrompt 对应是否因为补丁而弹出提示，如重新启动应用
     * <p>
     * <p>
     * code : 200
     * apatchVersionCode : 100
     * apatchUrl : http://xxx/100wisetv.apatch
     * apatchLength : 25999
     * apatchMD5 : 38f0cc188fe22334268b64290a34fd24
     * showPrompt : false
     */

    private String code;
    private String apatchVersionCode;
    private String apatchUrl;
    private String apatchLength;
    private String apatchMD5;
    private boolean showPrompt;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getApatchVersionCode() {
        return apatchVersionCode;
    }

    public void setApatchVersionCode(String apatchVersionCode) {
        this.apatchVersionCode = apatchVersionCode;
    }

    public String getApatchUrl() {
        return apatchUrl;
    }

    public void setApatchUrl(String apatchUrl) {
        this.apatchUrl = apatchUrl;
    }

    public String getApatchLength() {
        return apatchLength;
    }

    public void setApatchLength(String apatchLength) {
        this.apatchLength = apatchLength;
    }

    public String getApatchMD5() {
        return apatchMD5;
    }

    public void setApatchMD5(String apatchMD5) {
        this.apatchMD5 = apatchMD5;
    }

    public boolean isShowPrompt() {
        return showPrompt;
    }

    public void setShowPrompt(boolean showPrompt) {
        this.showPrompt = showPrompt;
    }

    @Override
    public String toString() {
        return "ApatchResponseBean{" +
                "apatchLength='" + apatchLength + '\'' +
                ", code='" + code + '\'' +
                ", apatchVersionCode='" + apatchVersionCode + '\'' +
                ", apatchUrl='" + apatchUrl + '\'' +
                ", apatchMD5='" + apatchMD5 + '\'' +
                ", showPrompt=" + showPrompt +
                '}';
    }
}
