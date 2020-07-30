package com.actor.testapplication.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.actor.testapplication.R;
import com.actor.testapplication.service.CheckUpdateService;
import com.actor.testapplication.utils.Global;
import com.actor.testapplication.widget.BasePopupWindow;
import com.blankj.utilcode.util.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tv_version)//版本
    TextView  tvVersion;
    @BindView(R.id.video_view)
    VideoView videoView;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setTitle("主页");
        AppUtils.AppInfo appInfo = AppUtils.getAppInfo();
        tvVersion.setText(getStringFormat("VersionName: %s(VersionCode: %d)", appInfo.getVersionName(), appInfo.getVersionCode()));//版本
        startService(new Intent(this, CheckUpdateService.class));//检查更新
    }

    @OnClick({R.id.btn_play, R.id.btn_glide, R.id.btn_expandable_item, R.id.btn_custom_view,
            R.id.btn_surface_view, R.id.btn_nested_scroll_view, R.id.btn_go2_test})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play://播放视频
                if (uri == null) videoView.setVideoURI(uri = Uri.parse(Global.BAIDU_VIDEO));
                if (!videoView.isPlaying()) videoView.start();
                break;
            case R.id.btn_glide://Glide测试
                startActivity(new Intent(this, GlideTestActivity.class));
                break;
            case R.id.btn_expandable_item://分组的伸缩栏(ExpandableItemAdapter)
                startActivity(new Intent(this, ExpandableItemActivity.class));
                break;
            case R.id.btn_custom_view://自定义View
                startActivity(new Intent(this, CustomViewActivity.class));
                break;
            case R.id.btn_surface_view://SurfaceView
                startActivity(new Intent(this, SurfaceViewActivity.class));
                break;
            case R.id.btn_nested_scroll_view://NestedScrollView
                startActivity(new Intent(this, NestedScrollViewActivity.class));
                break;
            case R.id.btn_go2_test://Test测试页面
                startActivity(new Intent(this, TestActivity.class), false, view);
//                showPopupWindow(view);
                break;
        }
    }

    private void showPopupWindow(View v) {
        BasePopupWindow popup = new BasePopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setLayout(activity, R.layout.item_add_minus_layout);

        //显示在某个位置
//        popup.showAtLocation(v, Gravity.BOTTOM, 0, 0);

        //显示在某个控件正下方
        popup.showAsDropDown(v, 0, 0, Gravity.TOP | Gravity.END);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, CheckUpdateService.class));
    }
}
