package fsc.com.slicesdemo;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.GridRowBuilder;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.ListBuilder.RowBuilder;
import androidx.slice.builders.SliceAction;

public class MySliceProvider extends SliceProvider {
    /**
     * Instantiate any required objects. Return true if the provider was successfully created,
     * false otherwise.
     */
    @Override
    public boolean onCreateSliceProvider() {
        return true;
    }


    /**
     * Construct the Slice and bind data if available.
     */
    @SuppressLint("NewApi")
    public Slice onBindSlice(Uri sliceUri) {
        Context context = getContext();
        if (context == null) {
            return null;
        }
        SliceAction activityAction = createActivityAction(sliceUri.getPath());
        if ("/".equals(sliceUri.getPath())) {
            return new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                    .addRow(
                            new RowBuilder()
                                    .setTitle("默认的内容")
                                    .setPrimaryAction(activityAction)
                    )
                    .build();
        } else if ("/demo01".equals(sliceUri.getPath())) {
            return new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                    .setAccentColor(Color.DKGRAY)//设置图标的颜色
                    .setHeader(new ListBuilder.HeaderBuilder().setTitle("标题")//Smaller格式下默认
                            .setSubtitle("副标题")
                            .setSummary("摘要", false)//Smaller格式下如果同时设置了Summary和Subtitle，则只显示Summary
                    )
                    .addRow(new RowBuilder().setPrimaryAction(activityAction)
                            .setTitle("Home")
                            .setSubtitle("女孩你知道吗")
                            .addEndItem(IconCompat.createWithResource(getContext(), R.drawable.ic_launcher_foreground), ListBuilder.ICON_IMAGE)

                    ).build();
        } else if ("/demo02".equals(sliceUri.getPath())) {
            return new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                    .setAccentColor(0xfff4b400)
                    .setHeader(new ListBuilder.HeaderBuilder()
                            .setTitle("要做什么呢")
                    )
                    .addRow(new RowBuilder().setTitle("打开App")
                            .setPrimaryAction(createActivityAction("/open"))
                            .addEndItem(createActivityAction("/camera"))
                            .addEndItem(createActivityAction("/voice"))
                            .addEndItem(createActivityAction("/voice"))//看上去似乎是不能超过三个
                    )
                    .addAction(createActivityAction("/camera"))
                    .addAction(createActivityAction("/voice"))
                    .addAction(createActivityAction("/voice"))
                    .addAction(createActivityAction("/voice"))//第四个不生效了
                    .build();
        } else if ("/weather".equals(sliceUri.getPath())) {
            return new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                    .setHeader(new ListBuilder.HeaderBuilder()
                            .setTitle("最近天气")
                            .setPrimaryAction(activityAction)
                    )
                    .addGridRow(new GridRowBuilder()
                            .addCell(new GridRowBuilder.CellBuilder()
                                    .addImage(IconCompat.createWithResource(getContext(), R.drawable.xiaoyu), ListBuilder.LARGE_IMAGE)
                                    .addTitleText("今天天气")
                                    .addText("小雨")
                            )
                            .addCell(new GridRowBuilder.CellBuilder()
                                    .addImage(IconCompat.createWithResource(getContext(), R.drawable.duoyun), ListBuilder.LARGE_IMAGE)
                                    .addTitleText("明天天气")
                                    .addText("多云")
                            )
                            .addCell(new GridRowBuilder.CellBuilder()
                                    .addImage(IconCompat.createWithResource(getContext(), R.drawable.zhenyu), ListBuilder.LARGE_IMAGE)
                                    .addTitleText("后天天气")
                                    .addText("阵雨")
                            )
                    )
                    .build();
        } else if ("/wifi".equals(sliceUri.getPath())) {
            SliceAction toggleAction = SliceAction.createToggle(getPendingIntent("/wifi"), "Toggle Wi-FI", false);
            return new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                    .setAccentColor(0xff4285f4)
                    .addRow(new RowBuilder()
                            .setTitle("Wi-Fi")
                            .setPrimaryAction(createActivityAction(sliceUri.getPath()))
                            .addEndItem(toggleAction)
                    )
                    .build();
        } else {
            return new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                    .addRow(
                            new RowBuilder()
                                    .setTitle("Url错误")
                                    .setPrimaryAction(activityAction)
                    )
                    .build();
        }
    }

    private SliceAction createActivityAction(String path) {
        switch (path) {
            case "/voice":
                return SliceAction.create(
                        getPendingIntent("录音"),
                        IconCompat.createWithResource(getContext(), R.drawable.ic_keyboard_voice_black_24dp),
                        ListBuilder.ICON_IMAGE,
                        "录音"
                );
            case "/camera":
                return SliceAction.create(
                        getPendingIntent("拍照"),
                        IconCompat.createWithResource(getContext(), R.drawable.ic_camera_alt_black_24dp),
                        ListBuilder.ICON_IMAGE,
                        "拍照"
                );
            case "/wifi":
                return SliceAction.create(
                        getPendingIntent("拍照"),
                        IconCompat.createWithResource(getContext(), R.drawable.ic_wifi_black_24dp),
                        ListBuilder.ICON_IMAGE,
                        "WIFI切换"
                );
            case "/open":
            default:
                return SliceAction.create(
                        getPendingIntent("打开应用"),
                        IconCompat.createWithResource(getContext(), R.drawable.ic_launcher_foreground),
                        ListBuilder.ICON_IMAGE,
                        "打开应用"
                );
        }
    }

    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("action", action);
        return PendingIntent.getActivity(
                getContext(), 0, intent, 0
        );
    }

    /**
     * Slice has been pinned to external process. Subscribe to data source if necessary.
     */
    @Override
    public void onSlicePinned(Uri sliceUri) {
        // When data is received, call context.contentResolver.notifyChange(sliceUri, null) to
        // trigger MySliceProvider#onBindSlice(Uri) again.
    }

    /**
     * Unsubscribe from data source if necessary.
     */
    @Override
    public void onSliceUnpinned(Uri sliceUri) {
        // Remove any observers if necessary to avoid memory leaks.
    }
}
