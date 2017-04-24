package demo.com.myapplication.pages;

import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.news.yazhidao.application.QiDianApplication;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.GsonUtil;
import com.news.yazhidao.utils.LogUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.utils.manager.UserManager;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import demo.com.myapplication.R;

public class SplashAty extends BaseActivity implements NativeAD.NativeAdListener {

    private ImageView mSplashSlogan;
    private ImageView mSplashMask;
    private TextView mSplashVersion;
    private int mScreenWidth;
    //baidu Map
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private RequestManager mRequestManager;
    //广告
    private ImageView ivAD;
    private ImageView ivSkip;
    private NativeAD mNativeAD;

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onADLoaded(List<NativeADDataRef> list) {
        if (!TextUtil.isListEmpty(list)) {
            LogUtil.adGetLog(this, 1, list.size(), Long.valueOf(CommonConstant.NEWS_FEED_GDT_SDK_SPLASHPOSID), CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE);
            final NativeADDataRef dataRef = list.get(0);
            if (dataRef != null) {
//                adtvTitle.setText(dataRef.getDesc());
                final String url = dataRef.getImgUrl();
                if (!TextUtil.isEmptyString(url)) {
                    mRequestManager.load(url).placeholder(R.drawable.bg_load_default_small).into(ivAD);
                    ivAD.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mRequestManager.load(url).placeholder(R.drawable.bg_load_default_small).into(ivAD);
                        }
                    });
                }
                dataRef.onExposured(ivAD);
                ivAD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LogUtil.adClickLog(Long.valueOf(CommonConstant.NEWS_FEED_GDT_SDK_SPLASHPOSID), SplashAty.this, CommonConstant.LOG_SHOW_FEED_AD_GDT_SDK_SOURCE, dataRef.getTitle());
                        dataRef.onClicked(ivAD);
                    }
                });
            }
        }
    }

    @Override
    public void onNoAD(int i) {

    }

    @Override
    public void onADStatusChanged(NativeADDataRef nativeADDataRef) {

    }

    @Override
    public void onADError(NativeADDataRef nativeADDataRef, int i) {

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location 如果定位成功需要及时关闭定位,否则耗电太快
            if (location != null) {
                mLocationClient.stop();
            }
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            SharedPreManager.mInstance(SplashAty.this).save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE, location.getProvince());
            SharedPreManager.mInstance(SplashAty.this).save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY, location.getCity());
            SharedPreManager.mInstance(SplashAty.this).save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR, location.getAddrStr());
            SharedPreManager.mInstance(SplashAty.this).save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_USER_LOCATION, GsonUtil.serialized(location.getAddress()));
            SharedPreManager.mInstance(SplashAty.this).save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LATITUDE, location.getLatitude() + "");
            SharedPreManager.mInstance(SplashAty.this).save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LONGITUDE, location.getLongitude() + "");
        }
    }

    @Override
    protected boolean isNeedAnimation() {
        return false;
    }

    @Override
    protected void setContentView() {
        ShareSDK.initSDK(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aty_splash);
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        UserManager.registerVisitor(this, null);
        mRequestManager = Glide.with(this);
        mNativeAD = new NativeAD(QiDianApplication.getInstance().getAppContext(), CommonConstant.APPID, CommonConstant.NEWS_FEED_GDT_SDK_SPLASHPOSID, this);
        mNativeAD.loadAD(1);
    }


    @Override
    protected void initializeViews() {
        ivSkip = (ImageView) findViewById(R.id.skip_image);
        ivSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainAty = new Intent(SplashAty.this, MainActivity.class);
                startActivity(mainAty);
                finish();
            }
        });
        ivAD = (ImageView) findViewById(R.id.ad_image);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivAD.getLayoutParams();
        layoutParams.width = mScreenWidth;
        layoutParams.height = (int) (mScreenWidth * 3 / 2.0f);
        ivAD.setLayoutParams(layoutParams);
//        mSplashSlogan = (ImageView) findViewById(R.id.mSplashSlogan);
//        mSplashMask = (ImageView) findViewById(R.id.mSplashMask);
//        mSplashVersion = (TextView) findViewById(R.id.mSplashVersion);
//        mSplashVersion.setText(getResources().getString(R.string.app_name) + " v" + getResources().getString(R.string.app_version));
//        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_alpha_in);
//        mSplashSlogan.setAnimation(animation);
//        final Animation mask = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mSplashMask.setAnimation(mask);
//            }
//        }, 100);
//        mask.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                mSplashMask.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
        //baidu Map
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        mLocationClient.start();
    }


    @Override
    protected void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                boolean showGuidePage = SharedPreManager.mInstance(SplashAty.this).getBoolean(CommonConstant.FILE_USER, CommonConstant.KEY_USER_NEED_SHOW_GUIDE_PAGE);
//                if (!showGuidePage) {
//                    Intent intent = new Intent(SplashAty.this, GuideLoginAty.class);
//                    startActivity(intent);
//                } else {
                    Intent mainAty = new Intent(SplashAty.this, MainActivity.class);
                    startActivity(mainAty);
//                }
                SplashAty.this.finish();
            }
        }, 8000);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
