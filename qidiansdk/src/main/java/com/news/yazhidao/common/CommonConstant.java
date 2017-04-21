package com.news.yazhidao.common;

/**
 * Created by fengjigang on 15/4/1.
 */
public final class CommonConstant {
    //uemng statistic constant startwith US
    //用户进入app
    public static final String US_BAINEWS_USER_ASSESS_APP = "bainews_user_assess_app";
    //用户点击大图新闻
    public static final String US_BAINEWS_VIEW_HEAD_NEWS = "bainews_view_head_news";
    //用户点击其他观点
    public static final String US_BAINEWS_ONCLICK_OTHER_VIEWPOINT = "bainews_onclick_other_viewpoint";
    //用户点击头图下面的url
    public static final String US_BAINEWS_CLICK_URL_BELOW_HEAD_VEWS = "bainews_click_url_below_head_vews";
    //用户在详情页里面点击相关的item
    public static final String US_BAINEWS_NEWSDETAIL_RELATE_ITEM_CLICK = "bainews_newsdetail_relate_item_click";
    //用户点击详情中的百度百科
    public static final String US_BAINEWS_NEWSDETAIL_BAIDUBAIKU = "bainews_newsdetail_baidubaiku";
    //用户点击详情中得新浪微博
    public static final String US_BAINEWS_NEWSDETAIL_WEIBO = "bainews_newsdetail_weibo";
    //用户点击详情中得知乎
    public static final String US_BAINEWS_NEWSDETAIL_ZHIHU = "bainews_newsdetail_zhihu";
    //用户点击详情中得豆瓣
    public static final String US_BAINEWS_NEWSDETAIL_DOUBAI = "bainews_newsdetail_doubai";
    //用户在详情页中点击展开全文
    public static final String US_BAINEWS_NEWSDETAIL_CLICK_PULLDOWN = "bainews_newsdetail_click_pulldown";
    //用户收到推送消息通知
    public static final String US_BAINEWS_NOTIFICATION_RECEIVED = "notification_receive";
    //用户收到推送消息并打开通知
    public static final String US_BAINEWS_NOTIFICATION_OPENED = "notification_open";

    public static final String FILE_USER = "user";
    public static final String FILE_AD = "ad";
    public static final String FILE_JPUSH = "jpush";
    public static final String FILE_DATA = "data";
    public static final String FILE_UMENG = "umeng";
    public static final String FILE_USER_CENTER = "user_center";
    public static final String USER_CENTER_SHOW = "user_center_show";
    public static final String KEY_USER_INFO = "user_info";
    public static final String KEY_VISITOR_INFO = "key_visitor_info";//游客信息
    public static final String KEY_JPUSH_ID = "jpush_id";
    public static final String KEY_UMENG_ID = "umeng_id";
    public static final String JINYU_JPUSH_ID = "0005150a7dd";
    public static final String FILE_SEARCH_WORDS = "serach_words";//用户搜索词sp 存储文件名
    public static final String KEY_SEARCH_WORDS = "key_search_words";
    public static final String FILE_USER_LOCATION = "file_user_location";
    public static final String KEY_USER_LOCATION = "key_user_location";
    public static final String KEY_LOCATION_LATITUDE = "key_location_latitude";
    public static final String KEY_LOCATION_LONGITUDE = "key_location_longitude";
    public static final String KEY_SOURCE = "key_source";
    public static final String KEY_USER_NEED_SHOW_GUIDE_PAGE = "key_user_need_show_guide_page";
    public static final String KEY_USER_OPEN_APP = "key_user_open_app";//用户打开app的次数
    public static final String KEY_UUID = "key_uuid";//确保用户唯一的uuid
    public static final String UPLOAD_LOG = "uploadlog";//用户上传日志
    public static final String UPLOAD_LOG_MAIN = "uploadlog_main";//用户上传日志
    public static final String UPLOAD_LOG_DETAIL = "uploadlog_detali";//用户上传日志
    public static final String MY_FAVORITE = "my_favorite";//我的收藏
    public static final String SEARCH_HISTORY = "search_history";//我的收藏
    public static final String KEY_SUBSCRIBE_LIST = "key_subscribe_list";//订阅信息
    public static final String KEY_ATTENTION_ID = "key_attention_id";//是否是以一次点击关注源
    public static final String KEY_LOCATION_PROVINCE = "key_location_province";
    public static final String KEY_LOCATION_CITY = "key_location_city";
    public static final String KEY_LOCATION_ADDR = "key_location_addr";
    public static final String KEY_ATTENTION_TITLE = "key_attention_title";
    public static final String KEY_ATTENTION_HEADIMAGE = "key_detail_headimage";
    public static final String KEY_ATTENTION_CONPUBFLAG = "key_detail_conpubflag";
    public static final String KEY_ATTENTION_INDEX = "key_attention_index";
    public static final String NEWS_COMMENT_NUM = "news_comment_num";
    public static final String NEWS_ID = "news_id";
    public static final String FILE_DATA_ATTENTION = "file_data_attention";
    public static final String KEY_ATTENTIN_LIST = "key_attentin_list";
    public static final int TEXT_SIZE_NORMAL = 18;
    public static final int TEXT_SIZE_SMALL = 16;
    public static final int REQUEST_ATTENTION_CODE = 1040;
    public static final int RESULT_ATTENTION_CODE = 2040;
    public static final int REQUEST_SUBSCRIBE_LIST_CODE = 3040;
    public static final int RESULT_SUBSCRIBE_LIST_CODE = 4040;
    public static final int REQUEST_LOGIN_CODE = 5040;
    public static final int RESULT_LOGIN_CODE = 6040;
    public static final String LOGIN_AUTHORIZEDUSER_ACTION = "login_authorizeduser_action";
    public static final String CHANGE_TEXT_ACTION = "change_text";
    public static final String CHANGE_COMMENT_NUM_ACTION = "change_comment_num";
    public static final String TYPE_SHOWIMAGES = "showImagesType";
    //分享action
    public static final String SHARE_TITLE = "share_title";
    public static final String SHARE_URL = "share_url";
    public static final String SHARE_WECHAT_MOMENTS_ACTION = "share_wechat_moments_action";
    public static final String SHARE_WECHAT_ACTION = "share_wechat_action";
    public static final String SHARE_SINA_WEIBO_ACTION = "share_sina_weibo_action";
    public static final String SHARE_QQ_ACTION = "share_qq_action";
    //广点通SDK id 广告位id
    public static final String APPID = "1105847205";
    public static final String NativePosID = "2000611873536900";
    public static final String NEWS_FEED_GDT_SDK_NativePosID = "5090120177109704";
    public static final String NEWS_DETAIL_GDT_SDK_NativePosID = "1070526117209763";
    //广点通API id 广告位id
    public static final String NEWS_FEED_GDT_API_NativePosID = "246";
    public static final String NEWS_DETAIL_GDT_API_NativePosID = "237";
    public static final String NEWS_RELATE_GDT_API_NativePosID = "237";
    //通知登录action
    public static final String USER_LOGIN_ACTION = "sdk_user_login_action";
    //1：奇点资讯， 2：黄历天气，3：纹字锁屏，4：猎鹰浏览器，5：白牌 6.纹字主题
    public static final int NEWS_CTYPE = 3;
    //平台1.ios 2.android 3.网页 4.无法识别
    public static final int NEWS_PTYPE = 2;

    public static final String LOG_PTYPE = "Android";
    //qidian 奇点资讯
    //huangli 黄历天气
    //wenzi	纹字锁屏
    //lybrowser	猎鹰浏览器
    //baipai 白牌
    public static final String LOG_CTYPE = "wenzi";

    public static final String LOG_SHOW_FEED_SOURCE = "feed";
    public static final String LOG_CLICK_FEED_SOURCE = "feed";
    public static final String LOG_CLICK_RELATE_SOURCE = "relate";
    public static final String LOG_CLICK_TOPIC_SOURCE = "topic";
    public static final String LOG_SHOW_FEED_AD_GDT_API_SOURCE = "gdtAPI";
    public static final String LOG_SHOW_FEED_AD_GDT_SDK_SOURCE = "gdtSDK";
    /**
     * atype取值 对应动作	对应params
     * login	登录
     * refreshFeed	上拉刷新feed流	chid(频道id)
     * loadFeed	下拉加载feed流	chid
     * detailClick	点击详情页	nid(新闻id)，logchid（点击来源，首页或频道页或相关推荐列表）
     * commentClick	点击获取评论	nid
     * changeChannel	切换频道	fchid,tchid
     * subChannel	订阅频道	chids(频道id列表，逗号分隔)
     * subPublisher	订阅来源	publisher(来源名称)
     * search	搜索	query
     * relateClick	点击相关新闻	nid
     * myComments	我的评论
     * myCollections	我的收藏
     * myMessages	我的消息
     */
    public static final String LOG_ATYPE_LOGIN = "login";
    public static final String LOG_ATYPE_REFRESHFEED = "refreshFeed";
    public static final String LOG_ATYPE_LOADFEED = "loadFeed";
    public static final String LOG_ATYPE_DETAILCLICK = "detailClick";
    public static final String LOG_ATYPE_VIDEO_DETAILCLICK = "videoDetailClick";
    public static final String LOG_ATYPE_CHANNELCLICK = "channelClick";
    public static final String LOG_ATYPE_COMMENTCLICK = "commentClick";
    public static final String LOG_ATYPE_CHANGECHANNEL = "changeChannel";
    public static final String LOG_ATYPE_SUBCHANNEL = "subChannel";
    public static final String LOG_ATYPE_SUBPUBLISHER = "subPublisher";
    public static final String LOG_ATYPE_SEARCH = "search";
    public static final String LOG_ATYPE_RELATECLICK = "relateClick";
    public static final String LOG_ATYPE_MYCOMMENTS = "myComments";
    public static final String LOG_ATYPE_MYCOLLECTIONS = "myCollections";
    public static final String LOG_ATYPE_MYMESSAGES = "myMessages";

    /**
     * page取值	对应页面
     * feedPage	feed流页面
     * detailPage	新闻详情页
     * channelPage	设置页面
     * topicPage	专题页面
     * loginPage	登录页面
     * searchPage	搜索页面
     * pfeedPage	来源feed流页面
     * attentionPage	关注页面
     * myCommentPage	我的评论页面
     * myCollectionPage	我的收藏页面
     * myMessagePage	我的消息页面
     * settingPage	设置页面
     */
    public static final String LOG_PAGE_FEEDPAGE = "feedPage";
    public static final String LOG_PAGE_DETAILPAGE = "detailPage";
    public static final String LOG_PAGE_VIDEODETAILPAGE = "videoDetailPage";
    public static final String LOG_PAGE_CHANNELPAGE = "channelPage";
    public static final String LOG_PAGE_TOPICPAGE = "topicPage";
    public static final String LOG_PAGE_LOGINPAGE = "loginPage";
    public static final String LOG_PAGE_SEARCHPAGE = "searchPage";
    public static final String LOG_PAGE_ATTENTIONPAGE = "attentionPage";
    public static final String LOG_PAGE_PFEEDPAGE = "pfeedPage";
    public static final String LOG_PAGE_MYCOMMENTPAGE = "myCommentPage";
    public static final String LOG_PAGE_MYCOLLECTIONPAGE = "myCollectionPage";
    public static final String LOG_PAGE_MYMESSAGEPAGE = "myMessagePage";
    public static final String LOG_PAGE_SETTINGPAGE = "settingPage";

}
