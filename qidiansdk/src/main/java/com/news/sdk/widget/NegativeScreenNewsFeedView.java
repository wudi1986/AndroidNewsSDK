package com.news.sdk.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.sdk.R;
import com.news.sdk.adapter.NegativeScreenNewsFeedAdapter;
import com.news.sdk.application.QiDianApplication;
import com.news.sdk.common.CommonConstant;
import com.news.sdk.common.HttpConstant;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.database.NewsFeedDao;
import com.news.sdk.entity.ADLoadNewsFeedEntity;
import com.news.sdk.entity.ADLoadVideoFeedEntity;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.entity.User;
import com.news.sdk.net.volley.NewsFeedRequestPost;
import com.news.sdk.utils.AdUtil;
import com.news.sdk.utils.DateUtil;
import com.news.sdk.utils.DeviceInfoUtil;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.LogUtil;
import com.news.sdk.utils.Logger;
import com.news.sdk.utils.NetUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.manager.SharedPreManager;
import com.news.sdk.utils.manager.UserManager;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NegativeScreenNewsFeedView extends View implements ThemeManager.OnThemeChangeListener, NativeAD.NativeAdListener {

    private Context mContext;
    RelativeLayout mRootView;
    private TextView mChannel1, mChannel2;
    public static String KEY_NEWS_ID = "key_news_id";
    public static final int PULL_DOWN_REFRESH = 1;
    public static final int PULL_UP_REFRESH = 2;
    public static final int REQUEST_CODE = 1060;
    private NegativeScreenNewsFeedAdapter mAdapter;
    private ArrayList<NewsFeed> mArrNewsFeed = new ArrayList<>();
    private LinkedList<NewsFeed> mUploadArrNewsFeed = new LinkedList<>();
    private PullToRefreshListView mlvNewsFeed;
    private int mChannelId = 1;
    private NewsFeedDao mNewsFeedDao;
    private boolean mFlag;
    private SharedPreferences mSharedPreferences;
    private boolean mIsFirst = true;
    private NewsSaveDataCallBack mNewsSaveCallBack;
    private View mHomeRetry;
    private RelativeLayout bgLayout;
    private boolean isListRefresh = false;
    private Handler mHandler;
    private Runnable mThread;
    private TextView footView_tv, mRefreshTitleBar;
    private ProgressBar footView_progressbar;
    private boolean isBottom;
    private RefreshReceiver mRefreshReceiver;
    private LinearLayout footerView;
    private ProgressBar imageAni;
    private TextView textAni;
    //广点通
    private NativeAD mNativeAD;
    private boolean isADRefresh;
    private List<NativeADDataRef> mADs;
    public static final int AD_COUNT = 5;
    private ImageView mivShareBg;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    private int adPosition, adFlag;
    private long scid = 0;

    //新闻标题,新闻时间,新闻描述
    public NegativeScreenNewsFeedView(Context context) {
        super(context);
        mContext = context;
        initializeViews();
    }

    private void initializeViews() {
        mRootView = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.negative_screen_news, null);
        mAlphaAnimationIn = new AlphaAnimation(0, 1.0f);
        mAlphaAnimationIn.setDuration(500);
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
        mNewsFeedDao = new NewsFeedDao(mContext);
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mFlag = mSharedPreferences.getBoolean("isshow", false);
        mRefreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
        intentFilter.addAction(CommonConstant.CHANGE_COMMENT_NUM_ACTION);
        mContext.registerReceiver(mRefreshReceiver, intentFilter);
        ThemeManager.registerThemeChangeListener(this);
        mNativeAD = new NativeAD(QiDianApplication.getInstance().getAppContext(), CommonConstant.APPID, CommonConstant.NEWS_FEED_GDT_SDK_BIGPOSID, this);
        bgLayout = (RelativeLayout) mRootView.findViewById(R.id.bgLayout);
        imageAni = (ProgressBar) mRootView.findViewById(R.id.imageAni);
        textAni = (TextView) mRootView.findViewById(R.id.textAni);
        mivShareBg = (ImageView) mRootView.findViewById(R.id.share_bg_imageView);
        mRefreshTitleBar = (TextView) mRootView.findViewById(R.id.mRefreshTitleBar);
        mHomeRetry = mRootView.findViewById(R.id.mHomeRetry);
        mHomeRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlvNewsFeed.setRefreshing();
                mHomeRetry.setVisibility(View.GONE);
            }
        });
        mChannel1 = (TextView) mRootView.findViewById(R.id.channel1);
        mChannel1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setChannelId(1);
            }
        });
        mChannel2 = (TextView) mRootView.findViewById(R.id.channel2);
        mChannel2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setChannelId(44);
            }
        });
        mlvNewsFeed = (PullToRefreshListView) mRootView.findViewById(R.id.news_feed_listView);
        mlvNewsFeed.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mlvNewsFeed.setMainFooterView(true);
        mlvNewsFeed.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isListRefresh = true;
                adFlag = PULL_DOWN_REFRESH;
                loadData(PULL_DOWN_REFRESH);
                scrollAd();
                if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                    for (NewsFeed newsFeed : mArrNewsFeed) {
                        if (!newsFeed.isUpload() && newsFeed.isVisble()) {
                            newsFeed.setUpload(true);
                            mUploadArrNewsFeed.add(newsFeed);
                        }
                    }
                }
                if (!TextUtil.isListEmpty(mUploadArrNewsFeed) && mUploadArrNewsFeed.size() > 4) {
                    while (mUploadArrNewsFeed.size() > 4) {
                        ArrayList<NewsFeed> newsFeeds = new ArrayList<>();
                        for (int i = 0; i < 4; i++) {
                            newsFeeds.add(mUploadArrNewsFeed.poll());
                        }
                        LogUtil.userShowLog(newsFeeds, mContext);
                    }
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isListRefresh = true;
                adFlag = PULL_UP_REFRESH;
                loadData(PULL_UP_REFRESH);
                scrollAd();
            }
        });
        addHFView();
        mAdapter = new NegativeScreenNewsFeedAdapter(mContext, this, null);
        mlvNewsFeed.setAdapter(mAdapter);
        mlvNewsFeed.setEmptyView(View.inflate(mContext, R.layout.qd_listview_empty_view, null));
        initTheme();
        //load news data
        mHandler = new Handler();
        mThread = new Runnable() {
            @Override
            public void run() {
//                mlvNewsFeed.setRefreshing();
                /**
                 *  梁帅： 2016.08.31 修改加载逻辑
                 *  如果有数据，拿数据的一条的是时间是下拉刷新
                 *  如果没有数据，直接加载
                 */
                ArrayList<NewsFeed> arrNewsFeed = mNewsFeedDao.queryByChannelId(mChannelId);
                if (!TextUtil.isListEmpty(arrNewsFeed)) {
                    loadData(PULL_DOWN_REFRESH);
                } else {
                    loadData(PULL_UP_REFRESH);
                }
                isListRefresh = false;
            }
        };
        int delay = 1500;
        if (mChannelId != 0 && mChannelId == 1) {
            delay = 500;
        }
//        mHandler.postDelayed(mThread, delay);
    }

    public View getNewsView() {
        return this.mRootView;
    }

    @Override
    public void onThemeChanged() {
        initTheme();
    }

    public void initTheme() {
        if (mlvNewsFeed != null) {
            mlvNewsFeed.setHeaderLoadingView();
            TextUtil.setLayoutBgResource(mContext, mRefreshTitleBar, R.color.color1);
            mRefreshTitleBar.setAlpha(0.9f);
            TextUtil.setTextColor(mContext, mRefreshTitleBar, R.color.color10);
            TextUtil.setLayoutBgResource(mContext, mlvNewsFeed, R.color.color6);
            TextUtil.setLayoutBgResource(mContext, footerView, R.color.color6);
            TextUtil.setLayoutBgResource(mContext, bgLayout, R.color.color6);
            TextUtil.setTextColor(mContext, textAni, R.color.color3);
            ImageUtil.setAlphaProgressBar(imageAni);
            TextUtil.setTextColor(mContext, footView_tv, R.color.color3);
//            if (navContainer != null && mChannelId == 44) {
//                TextUtil.setLayoutBgResource(mContext, nav, R.color.color9);
//                for (int i = 0; i < mChannelList.getChildCount(); i++) {
//                    RadioButton rb = (RadioButton) mChannelList.getChildAt(i);
//                    TextUtil.setLayoutBg(mContext, rb, R.drawable.bg_video_channel_selector);
//                    TextUtil.setTextColor(mContext, rb, R.color.text_video_channel_selector);
//                }
//            }
//            if (navContainer1 != null && mChannelId == 44) {
//                TextUtil.setLayoutBgResource(mContext, nav1, R.color.color9);
//                for (int i = 0; i < mChannelList1.getChildCount(); i++) {
//                    RadioButton rb = (RadioButton) mChannelList1.getChildAt(i);
//                    TextUtil.setLayoutBg(mContext, rb, R.drawable.bg_video_channel_selector);
//                    TextUtil.setTextColor(mContext, rb, R.color.text_video_channel_selector);
//                }
//            }
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setChannelId(int channelId) {
        mChannelId = channelId;
        if (mAdapter != null && !TextUtil.isListEmpty(mArrNewsFeed)) {
            mArrNewsFeed.remove(mArrNewsFeed);
            mArrNewsFeed = null;
//            mAdapter.setNewsFeed(null);
            mAdapter.notifyDataSetChanged();
        }
        if (mlvNewsFeed != null) {
            mlvNewsFeed.getRefreshableView().setSelection(0);
        }
        isListRefresh = false;
        loadData(PULL_UP_REFRESH);
    }


    public interface NewsSaveDataCallBack {
        void result(int channelId, ArrayList<NewsFeed> results);
    }

    public void setNewsSaveDataCallBack(NewsSaveDataCallBack listener) {
        this.mNewsSaveCallBack = listener;
    }

    boolean isNotLoadData;

    public void setNewsFeed(ArrayList<NewsFeed> results) {
        isNotLoadData = true;
        this.mArrNewsFeed = results;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
        }
    }

    public Rect rect = new Rect();

    public int getVisibilityPercents(View view) {
        View tv = view;
        tv.getLocalVisibleRect(rect);
        int height = tv.getHeight();
        int percents = 100;
        if (rect.top == 0 && rect.bottom == height) {
            percents = 100;
        } else if (rect.top > 0) {
            percents = (height - rect.top) * 100 / height;
        } else if (rect.bottom > 0 && rect.bottom < height) {
            percents = rect.bottom * 100 / height;
        }
        return percents;
    }

    public void getFirstPosition() {
        if (mlvNewsFeed == null) {//防止listview为空
            return;
        }
        isNotLoadData = false;
        mlvNewsFeed.getRefreshableView().setSelection(0);
        mlvNewsFeed.getRefreshableView().smoothScrollToPosition(0);
    }


    public void refreshData() {
        if (mlvNewsFeed == null) {//防止listview为空
            return;
        }
        isNotLoadData = false;
        mThread = new Runnable() {
            @Override
            public void run() {
                mlvNewsFeed.setRefreshing();
                isListRefresh = true;
            }
        };
        mHandler.postDelayed(mThread, 1000);
    }


    public void destroyView() {
        ThemeManager.unregisterThemeChangeListener(this);
        if (mRefreshReceiver != null) {
            mContext.unregisterReceiver(mRefreshReceiver);
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(mThread);
        }
    }


    /**
     * 当前新闻feed流fragment的父容器是否是MainAty,如果是则进行刷新动画
     */
    public void startTopRefresh() {
    }

    /**
     * 当前新闻feed流fragment的父容器是否是MainAty,如果是则停止刷新动画
     *
     * @return
     */
    public void stopRefresh() {
    }

    private void loadAD() {
        if (mNativeAD == null) {
            mNativeAD = new NativeAD(QiDianApplication.getInstance().getAppContext(), CommonConstant.APPID, CommonConstant.NEWS_FEED_GDT_SDK_BIGPOSID, this);
        }
        if (mNativeAD != null && !isADRefresh && !TextUtil.isEmptyString(CommonConstant.APPID)) {
            mNativeAD.loadAD(AD_COUNT);
            isADRefresh = true;
        }
    }

    private void loadNewsFeedData(String url, final int flag) {
        if (!isListRefresh) {
            bgLayout.setVisibility(View.VISIBLE);
        }
        if (mChannelId != 0 && mChannelId == 44) {
            adPosition = SharedPreManager.mInstance(mContext).getAdVideoFeedPosition(CommonConstant.FILE_AD, CommonConstant.AD_FEED_VIDEO_POS);
        } else {
            adPosition = SharedPreManager.mInstance(mContext).getAdFeedPosition(CommonConstant.FILE_AD, CommonConstant.AD_FEED_POS);
        }
        String requestUrl;
        String tstart = "";
        ADLoadNewsFeedEntity adLoadNewsFeedEntity = new ADLoadNewsFeedEntity();
        adLoadNewsFeedEntity.setCid(mChannelId == 0 ? null : Long.valueOf(mChannelId));
        adLoadNewsFeedEntity.setUid(SharedPreManager.mInstance(mContext).getUser(mContext).getMuid());
        adLoadNewsFeedEntity.setT(1);
        adLoadNewsFeedEntity.setV(1);
        adLoadNewsFeedEntity.setAds(SharedPreManager.mInstance(mContext).getAdChannelInt(CommonConstant.FILE_AD, CommonConstant.AD_CHANNEL));
        adLoadNewsFeedEntity.setB(TextUtil.getBase64(AdUtil.getAdMessage(mContext, CommonConstant.NEWS_FEED_GDT_API_BIGPOSID)));
        Gson gson = new Gson();
        if (flag == PULL_DOWN_REFRESH) {
            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                for (int i = 0; i < mArrNewsFeed.size(); i++) {
                    NewsFeed newsFeed = mArrNewsFeed.get(i);
                    if (newsFeed.getRtype() != 3 && newsFeed.getRtype() != 4) {
                        adLoadNewsFeedEntity.setNid(newsFeed.getNid());
                        break;
                    }
                }
            }
            tstart = getFirstItemTime(mArrNewsFeed);
            requestUrl = HttpConstant.URL_FEED_AD_PULL_DOWN;
            //下拉刷新打点
            if (mChannelId != 0) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("chid", mChannelId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.userActionLog(mContext, CommonConstant.LOG_ATYPE_LOADFEED, CommonConstant.LOG_PAGE_FEEDPAGE, CommonConstant.LOG_PAGE_FEEDPAGE, jsonObject, true);
            }
        } else {
            if (mFlag) {
                if (mIsFirst) {
                    ArrayList<NewsFeed> arrNewsFeed = mNewsFeedDao.queryByChannelId(mChannelId);
                    tstart = getFirstItemTime(arrNewsFeed);
                } else {
                    tstart = getLastItemTime(mArrNewsFeed);
                }
            } else {
                mSharedPreferences.edit().putBoolean("isshow", true).commit();
                mFlag = true;
                tstart = getFirstItemTime(null);
            }
            requestUrl = HttpConstant.URL_FEED_AD_LOAD_MORE;
            //上拉刷新打点
            if (mChannelId != 0) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("chid", mChannelId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.userActionLog(mContext, CommonConstant.LOG_ATYPE_REFRESHFEED, CommonConstant.LOG_PAGE_FEEDPAGE, CommonConstant.LOG_PAGE_FEEDPAGE, jsonObject, true);
            }
        }
        adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart) ? null : Long.parseLong(tstart));
        String params;
        if (mChannelId == 44 && scid != 0) {
            ADLoadVideoFeedEntity adLoadVideoFeedEntity = new ADLoadVideoFeedEntity();
            adLoadVideoFeedEntity.setNid(adLoadNewsFeedEntity.getNid());
            adLoadVideoFeedEntity.setCid(adLoadNewsFeedEntity.getCid());
            adLoadVideoFeedEntity.setUid(adLoadNewsFeedEntity.getUid());
            adLoadVideoFeedEntity.setT(adLoadNewsFeedEntity.getT());
            adLoadVideoFeedEntity.setV(adLoadNewsFeedEntity.getV());
            adLoadVideoFeedEntity.setAds(adLoadNewsFeedEntity.getAds());
            adLoadVideoFeedEntity.setB(adLoadNewsFeedEntity.getB());
            adLoadVideoFeedEntity.setTcr(adLoadNewsFeedEntity.getTcr());
            adLoadVideoFeedEntity.setScid(scid);
            params = gson.toJson(adLoadVideoFeedEntity);
        } else {
            params = gson.toJson(adLoadNewsFeedEntity);
        }
        RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
        NewsFeedRequestPost<ArrayList<NewsFeed>> newsFeedRequestPost = new NewsFeedRequestPost(requestUrl, params, new Response.Listener<ArrayList<NewsFeed>>() {
            @Override
            public void onResponse(final ArrayList<NewsFeed> result) {
                loadNewFeedSuccess(result, flag);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadNewFeedError(error, flag);
            }
        });
        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        newsFeedRequestPost.setRequestHeaders(header);
        newsFeedRequestPost.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(newsFeedRequestPost);
    }

    private String getFirstItemTime(ArrayList<NewsFeed> newsFeedArrayList) {
        if (!TextUtil.isListEmpty(newsFeedArrayList)) {
            for (NewsFeed newsFeed : newsFeedArrayList) {
                String time = newsFeed.getPtime();
                if (!TextUtil.isEmptyString(time)) {
                    return DateUtil.dateStr2Long(time) + "";
                }
            }
        }
        return System.currentTimeMillis() - 1000 * 60 * 60 * 12 + "";
    }

    private String getLastItemTime(ArrayList<NewsFeed> newsFeedArrayList) {
        if (!TextUtil.isListEmpty(newsFeedArrayList)) {
            for (int i = newsFeedArrayList.size() - 1; i >= 0; i--) {
                String time = newsFeedArrayList.get(i).getPtime();
                if (!TextUtil.isEmptyString(time)) {
                    return DateUtil.dateStr2Long(time) + "";
                }
            }
        }
        return System.currentTimeMillis() - 1000 * 60 * 60 * 12 + "";
    }

    public void loadNewFeedSuccess(final ArrayList<NewsFeed> result, int flag) {
        removePrompt();
        if (mIsFirst || flag == PULL_DOWN_REFRESH) {
            if (result == null || result.size() == 0) {
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
                return;
            }
            mRefreshTitleBar.setText("又发现了" + result.size() + "条新数据");
            mRefreshTitleBarAnimation();
        }
        //如果频道是1,则说明此频道的数据都是来至于其他的频道,为了方便存储,所以要修改其channelId
        if (mChannelId != 0 && 1 == mChannelId || 35 == mChannelId || 44 == mChannelId) {
            for (NewsFeed newsFeed : result) {
                if (1 == mChannelId) {
                    newsFeed.setChannel_id(1);
                    if (newsFeed.getStyle() == 6) {
                        newsFeed.setStyle(8);
                    }
                } else if (35 == mChannelId) {
                    newsFeed.setChannel_id(35);
                    if (newsFeed.getStyle() == 6) {
                        newsFeed.setStyle(8);
                    }
                } else {
                    newsFeed.setChannel_id(newsFeed.getChannel());
                }
                if (newsFeed.getRtype() == 3) {
                    newsFeed.setSource(CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE);
                    newsFeed.setAid(Long.valueOf(CommonConstant.NEWS_FEED_GDT_API_BIGPOSID));
                    LogUtil.adGetLog(mContext, 1, 1, Long.valueOf(CommonConstant.NEWS_FEED_GDT_API_BIGPOSID), CommonConstant.LOG_SHOW_FEED_AD_GDT_API_SOURCE);
                } else {
                    newsFeed.setSource(CommonConstant.LOG_SHOW_FEED_SOURCE);
                }
            }
        }
//        for (Iterator it = result.iterator(); it.hasNext();) {
//            NewsFeed newsFeed = (NewsFeed) it.next();
//            if(newsFeed.getRtype()==3){
//                it.remove();
//            }
//        }
        if (flag == PULL_DOWN_REFRESH && !mIsFirst && !TextUtil.isListEmpty(result)) {
            NewsFeed newsFeed = new NewsFeed();
            newsFeed.setStyle(900);
            result.add(newsFeed);
        }
        mHomeRetry.setVisibility(View.GONE);
        stopRefresh();
        if (result != null && result.size() > 0) {
            switch (flag) {
                case PULL_DOWN_REFRESH:
                    if (mArrNewsFeed == null) {
                        mArrNewsFeed = result;
                    } else {
                        //type==4 专题
                        if (result.get(0).getRtype() == 4) {
                            Iterator<NewsFeed> iterator = mArrNewsFeed.iterator();
                            while (iterator.hasNext()) {
                                NewsFeed newsFeed = iterator.next();
                                if (newsFeed.getRtype() == 4 && result.get(0).getNid() == newsFeed.getNid()) {
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                        mArrNewsFeed.addAll(0, result);
                    }
                    mlvNewsFeed.getRefreshableView().setSelection(0);
                    break;
                case PULL_UP_REFRESH:
                    if (mArrNewsFeed == null) {
                        mArrNewsFeed = result;
                    } else {
                        mArrNewsFeed.addAll(result);
                    }
                    break;
            }
            if (mNewsSaveCallBack != null) {
                mNewsSaveCallBack.result(mChannelId, mArrNewsFeed);
            }
            mAdapter.setNewsFeed(mArrNewsFeed);
            mAdapter.notifyDataSetChanged();
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
            //广点通sdk请求广告
            if (SharedPreManager.mInstance(mContext).getBoolean(CommonConstant.FILE_AD, CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE)) {
                if (TextUtil.isListEmpty(mADs)) {
                    loadAD();
                } else {
                    addADToList(flag);
                }
            }
        } else {
            //向服务器发送请求,已成功,但是返回结果为null,需要显示重新加载view
            if (TextUtil.isListEmpty(mArrNewsFeed)) {
                ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mChannelId);
                if (TextUtil.isListEmpty(newsFeeds)) {
                    mHomeRetry.setVisibility(View.VISIBLE);
                } else {
                    mArrNewsFeed = newsFeeds;
                    mHomeRetry.setVisibility(View.GONE);
                    mAdapter.setNewsFeed(newsFeeds);
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                mAdapter.setNewsFeed(mArrNewsFeed);
                mAdapter.notifyDataSetChanged();
            }
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
        }
        if (mIsFirst) {
            mIsFirst = false;
        }
        mlvNewsFeed.onRefreshComplete();
    }

    private void loadNewFeedError(VolleyError error, final int flag) {
        if (error.toString().contains("2002")) {
            removePrompt();

            mRefreshTitleBar.setText("已是最新数据");
            mRefreshTitleBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mRefreshTitleBar.getVisibility() == View.VISIBLE) {
                        mRefreshTitleBar.setVisibility(View.GONE);
                    }
                }
            }, 1000);
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
        } else if (error.toString().contains("4003") && mChannelId == 1) {//说明三方登录已过期,防止开启3个loginty
            User user = SharedPreManager.mInstance(mContext).getUser(mContext);
            user.setUtype("2");
            SharedPreManager.mInstance(mContext).saveUser(user);
//                    Intent loginAty = new Intent(mContext, LoginAty.class);
//                    startActivityForResult(loginAty, REQUEST_CODE);
            UserManager.registerVisitor(mContext, new UserManager.RegisterVisitorListener() {
                @Override
                public void registerSuccess() {
                    mlvNewsFeed.onRefreshComplete();
                    loadNewsFeedData("", flag);
                }
            });
        }
        if (TextUtil.isListEmpty(mArrNewsFeed)) {
            ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mChannelId);
            if (TextUtil.isListEmpty(newsFeeds)) {
                mHomeRetry.setVisibility(View.VISIBLE);
            } else {
                mArrNewsFeed = newsFeeds;
                mHomeRetry.setVisibility(View.GONE);
                mAdapter.setNewsFeed(newsFeeds);
                mAdapter.notifyDataSetChanged();
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
            }
        }
        stopRefresh();
        mlvNewsFeed.onRefreshComplete();
    }

    public void loadData(final int flag) {
        User user = SharedPreManager.mInstance(mContext).getUser(mContext);
        if (null != user) {
            if (NetUtil.checkNetWork(mContext)) {
                if (!isNotLoadData && mChannelId != 0) {
                    loadNewsFeedData("recommend", flag);
                    startTopRefresh();
                } else {
                    isNotLoadData = false;
                    setRefreshComplete();
                }
            } else {
                setRefreshComplete();
                ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mChannelId);
                if (TextUtil.isListEmpty(newsFeeds)) {
                    mHomeRetry.setVisibility(View.VISIBLE);
                } else {
                    mAdapter.setNewsFeed(newsFeeds);
                    mAdapter.notifyDataSetChanged();
                    mHomeRetry.setVisibility(View.GONE);
                }
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
            }
        } else {
            mHomeRetry.setVisibility(View.VISIBLE);
            setRefreshComplete();
            //请求token
            UserManager.registerVisitor(mContext, new UserManager.RegisterVisitorListener() {
                @Override
                public void registerSuccess() {
                    loadData(flag);
                }
            });
        }
    }

    private void setRefreshComplete() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mlvNewsFeed.onRefreshComplete();
            }
        }, 500);
    }

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonConstant.CHANGE_TEXT_ACTION.equals(intent.getAction())) {
                Logger.e("aaa", "文字的改变！！！");
                mAdapter.notifyDataSetChanged();
            } else if (CommonConstant.CHANGE_COMMENT_NUM_ACTION.equals(intent.getAction()) && intent != null) {
                int newsId = intent.getIntExtra(CommonConstant.NEWS_ID, 0);
                if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                    for (NewsFeed newsFeed : mArrNewsFeed) {
                        if (newsFeed.getNid() == newsId) {
                            int num = intent.getIntExtra(CommonConstant.NEWS_COMMENT_NUM, 0);
                            newsFeed.setComment(num);
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
    }

    public void addHFView() {
        ListView lv = mlvNewsFeed.getRefreshableView();
        footerView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.footerview_layout, null);
        lv.addFooterView(footerView);
        footView_tv = (TextView) footerView.findViewById(R.id.footerView_tv);
        footView_progressbar = (ProgressBar) footerView.findViewById(R.id.footerView_pb);
        mlvNewsFeed.setOnStateListener(new PullToRefreshBase.onStateListener() {
            @Override
            public void getState(PullToRefreshBase.State mState) {
                if (!isBottom) {
                    return;
                }
                boolean isVisisyProgressBar = false;
                switch (mState) {
                    case RESET://初始
                        isVisisyProgressBar = false;
                        footView_tv.setText("上拉获取更多文章");
                        break;
                    case PULL_TO_REFRESH://更多推荐
                        isVisisyProgressBar = false;
                        footView_tv.setText("上拉获取更多文章");
                        break;
                    case RELEASE_TO_REFRESH://松开推荐
                        isVisisyProgressBar = false;
                        footView_tv.setText("松手获取更多文章");
                        break;
                    case REFRESHING:
                    case MANUAL_REFRESHING://推荐中
                        isVisisyProgressBar = true;
                        footView_tv.setText("正在获取更多文章...");
                        break;
                    case OVERSCROLLING:
                        // NO-OP
                        break;
                }
                if (isVisisyProgressBar) {
                    footView_progressbar.setVisibility(View.VISIBLE);
                } else {
                    footView_progressbar.setVisibility(View.GONE);
                }
                mlvNewsFeed.setFooterViewInvisible();
            }
        });

        // 监听listview滚到最底部
        mlvNewsFeed.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //正在滚动时回调，回调2-3次，手指没抛则回调2次。scrollState = 2的这次不回调
                //回调顺序如下
                //第1次：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动
                //第2次：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）                //第3次：scrollState = SCROLL_STATE_IDLE(0) 停止滚动                //当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；                //由于用户的操作，屏幕产生惯性滑动时为2
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            isBottom = true;
                            isListRefresh = true;
                            loadData(PULL_UP_REFRESH);
                        } else {
                            isBottom = false;
                        }
                        if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                            for (NewsFeed newsFeed : mArrNewsFeed) {
                                if (!newsFeed.isUpload() && newsFeed.isVisble()) {
                                    newsFeed.setUpload(true);
                                    mUploadArrNewsFeed.add(newsFeed);
                                }
                            }
                        }
                        if (!TextUtil.isListEmpty(mUploadArrNewsFeed) && mUploadArrNewsFeed.size() > 4) {
                            while (mUploadArrNewsFeed.size() > 4) {
                                ArrayList<NewsFeed> newsFeeds = new ArrayList<>();
                                for (int i = 0; i < 4; i++) {
                                    newsFeeds.add(mUploadArrNewsFeed.poll());
                                }
                                LogUtil.userShowLog(newsFeeds, mContext);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int num = 0;
                if (mChannelId != 0 && mChannelId == 44) {
                    num = firstVisibleItem + visibleItemCount - 2;
                } else {
                    num = firstVisibleItem + visibleItemCount - 3;
                }
                if (!TextUtil.isListEmpty(mArrNewsFeed) && mArrNewsFeed.size() > num && num >= 0) {
                    NewsFeed feed = mArrNewsFeed.get(num);
                    View v = view.getChildAt(visibleItemCount - 1);
                    int percents = getVisibilityPercents(v);
                    if (!feed.isUpload() && feed.isVisble() && percents < 50) {
                        feed.setVisble(false);
                    } else {
                        feed.setVisble(true);
                    }
                }
            }
        });
    }


    public void mRefreshTitleBarAnimation() {
        //初始化
        Animation mStartAlphaAnimation = new AlphaAnimation(0f, 1.0f);
        //设置动画时间
        mStartAlphaAnimation.setDuration(500);
        mRefreshTitleBar.startAnimation(mStartAlphaAnimation);
        mStartAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mRefreshTitleBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation mEndAlphaAnimation = new AlphaAnimation(1.0f, 0f);
                        mEndAlphaAnimation.setDuration(500);
                        mRefreshTitleBar.startAnimation(mEndAlphaAnimation);
                        mEndAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mRefreshTitleBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void removePrompt() {
        if (!TextUtil.isListEmpty(mArrNewsFeed)) {
            Iterator<NewsFeed> iterator = mArrNewsFeed.iterator();
            while (iterator.hasNext()) {
                NewsFeed newsFeed = iterator.next();
                if (newsFeed.getStyle() == 900) {
                    iterator.remove();
                    mAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void setTextSize() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void addADToList(int flag) {
        if (!TextUtil.isListEmpty(mADs) && mAdapter != null) {
            // 强烈建议：多个广告之间的间隔最好大一些，优先保证用户体验！
            // 此外，如果开发者的App的使用场景不是经常被用户滚动浏览多屏的话，没有必要在调用loadAD(int count)时去加载多条，只需要在用户即将进入界面时加载1条广告即可。
//                            mAdapter.addADToPosition((AD_POSITION + i * 10) % MAX_ITEMS, mADs.get(i));
            NativeADDataRef data = mADs.get(0);
            if (mArrNewsFeed != null && mArrNewsFeed.size() > 2) {
//                NewsFeed newsFeedFirst = mArrNewsFeed.get(1);
                NewsFeed newsFeed = new NewsFeed();
                newsFeed.setTitle(data.getDesc());
                newsFeed.setRtype(3);
                ArrayList<String> imgs = new ArrayList<>();
                imgs.add(data.getImgUrl());
                newsFeed.setImgs(imgs);
                newsFeed.setIcon(data.getIconUrl());
                newsFeed.setPname(data.getTitle());
//                int style = newsFeedFirst.getStyle();
//                if (style == 11 || style == 12 || style == 13 || style == 5) {
//                    newsFeed.setStyle(50);
//                } else {
//                    newsFeed.setStyle(51);
//                }
//                int i = Math.random() > 0.5 ? 1 : 0;
//                if (i == 0) {
//                    newsFeed.setStyle(50);
//                } else {
//                    newsFeed.setStyle(51);
//                }
                newsFeed.setStyle(51);
                if (44 == mChannelId) {
                    newsFeed.setChannel(44);
                }
                newsFeed.setAid(Long.valueOf(CommonConstant.NEWS_FEED_GDT_SDK_BIGPOSID));
                newsFeed.setSource(CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE);
                newsFeed.setDataRef(data);
                if (PULL_DOWN_REFRESH == flag) {
                    if (mArrNewsFeed.size() > adPosition) {
                        mArrNewsFeed.add(adPosition, newsFeed);
                    }
                } else {
                    if (mArrNewsFeed.size() >= 14) {
                        if (mChannelId != 0 && mChannelId == 44) {
                            mArrNewsFeed.add(mArrNewsFeed.size() - 11, newsFeed);
                        } else {
                            mArrNewsFeed.add(mArrNewsFeed.size() - 8, newsFeed);
                        }
                    } else {
                        if (mArrNewsFeed.size() > adPosition) {
                            mArrNewsFeed.add(adPosition, newsFeed);
                        }
                    }
                }
                mADs.remove(0);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onADLoaded(List<NativeADDataRef> list) {
        if (!TextUtil.isListEmpty(list)) {
            LogUtil.adGetLog(mContext, AD_COUNT, list.size(), Long.valueOf(CommonConstant.NEWS_FEED_GDT_SDK_BIGPOSID), CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE);
            mADs = list;
            addADToList(adFlag);
        }
        isADRefresh = false;
    }

    @Override
    public void onNoAD(int i) {
        isADRefresh = false;
    }

    @Override
    public void onADStatusChanged(NativeADDataRef nativeADDataRef) {
        getADButtonText(nativeADDataRef);
        isADRefresh = false;
    }

    @Override
    public void onADError(NativeADDataRef nativeADDataRef, int i) {
        isADRefresh = false;
    }

    /**
     * App类广告安装、下载状态的更新（普链广告没有此状态，其值为-1） 返回的AppStatus含义如下： 0：未下载 1：已安装 2：已安装旧版本 4：下载中（可获取下载进度“0-100”）
     * 8：下载完成 16：下载失败
     */

    private String getADButtonText(NativeADDataRef adItem) {
        if (adItem == null) {
            return "……";
        }
        if (!adItem.isAPP()) {
            return "查看详情";
        }
        switch (adItem.getAPPStatus()) {
            case 0:
                return "点击下载";
            case 1:
                return "点击启动";
            case 2:
                return "点击更新";
            case 4:
                return adItem.getProgress() > 0 ? "下载中" + adItem.getProgress() + "%" : "下载中"; // 特别注意：当进度小于0时，不要使用进度来渲染界面
            case 8:
                return "下载完成";
            case 16:
                return "下载失败,点击重试";
            default:
                return "查看详情";
        }
    }

    /**
     * 广告滑动接口
     */
    private void scrollAd() {
        User user = SharedPreManager.mInstance(mContext).getUser(mContext);
        if (user != null) {
            int uid = user.getMuid();
            //渠道类型, 1：奇点资讯， 2：黄历天气，3：纹字锁频，4：猎鹰浏览器，5：白牌 6:纹字主题
            int ctype = CommonConstant.NEWS_CTYPE;
            //平台类型，1：IOS，2：安卓，3：网页，4：无法识别
            int ptype = CommonConstant.NEWS_PTYPE;
            //mid
            String imei = DeviceInfoUtil.getDeviceImei(mContext);
            String requestUrl = HttpConstant.URL_SCROLL_AD + "?uid=" + uid + "&ctype=" + ctype + "&ptype=" + ptype + "&mid=" + imei;
            RequestQueue requestQueue = QiDianApplication.getInstance().getRequestQueue();
            StringRequest request = new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, null);
            requestQueue.add(request);
        }
    }

}
