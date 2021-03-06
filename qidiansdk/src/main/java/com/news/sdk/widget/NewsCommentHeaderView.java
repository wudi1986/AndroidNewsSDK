package com.news.sdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.news.sdk.R;
import com.news.sdk.entity.NewsFeed;
import com.news.sdk.utils.DateUtil;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.TextUtil;

import static com.news.sdk.R.id.content_layout;

public class NewsCommentHeaderView extends LinearLayout {

    private TextView mtvNewsCommentTitle;
    private TextView mtvNewsCommentContent, news_comment_hotComment;
    private View news_comment_hotComment_Line1, news_comment_hotComment_Line2;
    private LinearLayout mllNewsCommentNoCommentsLayout;
    private LinearLayout mllContentLayout;
    private NewsFeed mNewsFeed;
    private Context mContext;
    private ImageView mivSofa;
    private TextView mtvSofa;

    //新闻标题,新闻时间,新闻描述
    public NewsCommentHeaderView(Context context) {
        this(context, null);
    }

    public NewsCommentHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NewsCommentHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View rootView = View.inflate(context, R.layout.news_comment_fragment_headerview, this);
        mtvNewsCommentTitle = (TextView) rootView.findViewById(R.id.news_comment_Title);
        mtvNewsCommentContent = (TextView) rootView.findViewById(R.id.news_comment_content);
        mllNewsCommentNoCommentsLayout = (LinearLayout) rootView.findViewById(R.id.news_comment_NoCommentsLayout);
        mllContentLayout = (LinearLayout) rootView.findViewById(content_layout);
        news_comment_hotComment = (TextView) rootView.findViewById(R.id.news_comment_hotComment);
        news_comment_hotComment_Line1 = rootView.findViewById(R.id.news_comment_hotComment_Line1);
        news_comment_hotComment_Line2 = rootView.findViewById(R.id.news_comment_hotComment_Line2);
        mtvSofa = (TextView) rootView.findViewById(R.id.text_sofa);
        mivSofa = (ImageView) rootView.findViewById(R.id.image_sofa);
    }

    public void setData(NewsFeed newsFeed) {
        mNewsFeed = newsFeed;
        updateView();
    }

    private void updateView() {
        TextUtil.setTextColor(mContext, news_comment_hotComment, R.color.color2);
        TextUtil.setLayoutBgResource(mContext, news_comment_hotComment_Line1, R.color.color1);
        TextUtil.setLayoutBgResource(mContext, news_comment_hotComment_Line2, R.color.color5);
        TextUtil.setLayoutBgResource(mContext, mllContentLayout, R.color.color6);
        TextUtil.setLayoutBgResource(mContext, mllNewsCommentNoCommentsLayout, R.color.color6);
        TextUtil.setTextColor(mContext, mtvNewsCommentTitle, R.color.color2);
        TextUtil.setTextColor(mContext, mtvNewsCommentContent, R.color.color3);
        TextUtil.setTextColor(mContext, mtvSofa, R.color.color3);
        ImageUtil.setAlphaImage(mivSofa);
        if (mNewsFeed != null) {
            String title = mNewsFeed.getTitle();
            if (!TextUtil.isEmptyString(title)) {
                mtvNewsCommentTitle.setText(title);
            }
            int comment = mNewsFeed.getComment();
            String pname = mNewsFeed.getPname();
            String ptime = mNewsFeed.getPtime();
            if (0 == comment) {
                mtvNewsCommentContent.setText(pname + "  " + DateUtil.getMonthAndDay(ptime));
                setNoCommentsLayoutVisible();
            } else {
                mtvNewsCommentContent.setText(pname + "  " + DateUtil.getMonthAndDay(ptime) + "  " + TextUtil.getCommentNum(String.valueOf(comment)));
                setNoCommentsLayoutGone();
            }
        }
    }

    public void setNoCommentsLayoutVisible() {
        mllNewsCommentNoCommentsLayout.setVisibility(View.VISIBLE);
    }

    public void setNoCommentsLayoutGone() {
        mllNewsCommentNoCommentsLayout.setVisibility(View.GONE);
    }

    public void setNewsCommentTitleTextSize(int size) {
        mtvNewsCommentTitle.setTextSize(size);
    }
}
