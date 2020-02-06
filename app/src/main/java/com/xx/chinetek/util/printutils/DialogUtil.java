package com.xx.chinetek.util.printutils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.xx.chinetek.util.printutils.interfaces.SingleButtonCallback;


/**
 * @ Des:  dialog工具类
 * @ Created by yangyiqing on 2018/7/29.
 */
public class DialogUtil {
    public static void createDialog(Context context, String content, int type, final SingleButtonCallback listener) {
        switch (type) {
            case 1:
                MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                        .content(content)
                        .positiveText("确定")
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (listener != null) {
                                    listener.onPositive();
                                }
                                dialog.dismiss();
                            }
                        })
                        .negativeText("取消")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (listener != null) {
                                    listener.onNegative();
                                }
                                dialog.dismiss();
                            }
                        });
                builder.show();
                break;
            case 2:
                MaterialDialog.Builder builder2 = new MaterialDialog.Builder(context)
                        .content(content)
                        .positiveText("确定")
                        .cancelable(false);
                builder2.show();
                break;

            case 3:
                MaterialDialog.Builder builder3 = new MaterialDialog.Builder(context)
                        .content(content)
                        .positiveText("确定")
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (listener != null) {
                                    listener.onPositive();
                                }
                                dialog.dismiss();
                            }
                        });
                builder3.show();
        }
    }
}
