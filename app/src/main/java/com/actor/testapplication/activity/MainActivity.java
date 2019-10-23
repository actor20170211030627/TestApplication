package com.actor.testapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.actor.testapplication.R;
import com.actor.testapplication.service.CheckUpdateService;
import com.blankj.utilcode.util.AppUtils;
import com.blikoon.qrcodescanner.QrCodeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tv_version)//版本
    TextView  tvVersion;
    @BindView(R.id.video_view)
    VideoView videoView;
    @BindView(R.id.tv_result)//显示结果
    TextView  tvResult;

    //thanks www.baidu.com百度
    private String url = "http://tb-video.bdstatic.com/tieba-smallvideo-transcode/8_4871b1e9218ec13f03131176197ef53d_1.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tvVersion.setText(getStringFormat("VersionName: %s(VersionCode: %d)",
                AppUtils.getAppVersionName(), AppUtils.getAppVersionCode()));//版本
        startService(new Intent(this, CheckUpdateService.class));//检查更新

        videoView.setVideoURI(Uri.parse(url));
    }

    @OnClick({R.id.btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
//                videoView.start();

                Intent i = new Intent(MainActivity.this, QrCodeActivity.class);
                startActivityForResult( i, REQUEST_CODE_QR_SCAN);

//                startActivity(new Intent(this, TestActivity.class));

                break;
        }
    }

    private static final int REQUEST_CODE_QR_SCAN = 101;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if(resultCode != Activity.RESULT_OK) {
                //解析图片
                String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
                tvResult.setText(result);
            } else {
                //扫描二维码
                String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
                tvResult.setText(result);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, CheckUpdateService.class));
    }
}
