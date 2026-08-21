package com.github.tvbox.osc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.github.tvbox.osc.ui.activity.HomeActivity;
import com.github.tvbox.osc.util.HawkConfig;
import com.orhanobut.hawk.Hawk;

/**
 * 开机自启进直播
 * 在设置里开启"开机自启进直播"后, 盒子开机自动进入 TVBox,
 * 配合"启动时进直播"开关即可直接进直播界面, 变成纯电视播放器。
 *
 * Android 10+ 后台 Activity 启动限制: BOOT_COMPLETED 广播里直接 startActivity
 * 会被系统拦截 (isBgStartWhitelisted=false), 需要:
 * 1. manifest 声明 SYSTEM_ALERT_WINDOW (授予后豁免后台启动限制)
 * 2. 延迟启动等系统完全就绪
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            try {
                if (Hawk.get(HawkConfig.BOOT_START_LIVE, false)) {
                    final Context ctx = context.getApplicationContext();
                    // 延迟启动: 等系统完全就绪 + 避免开机广播风暴期间被限制
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Intent i = new Intent(ctx, HomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ctx.startActivity(i);
                            } catch (Throwable ignored) {
                            }
                        }
                    }, 3000);
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
