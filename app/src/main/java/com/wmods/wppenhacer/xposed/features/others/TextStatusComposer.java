package com.wmods.wppenhacer.xposed.features.others;

import android.app.Activity;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.wmods.wppenhacer.views.dialog.SimpleColorPickerDialog;
import com.wmods.wppenhacer.xposed.core.Feature;
import com.wmods.wppenhacer.xposed.core.devkit.Unobfuscator;
import com.wmods.wppenhacer.xposed.utils.ReflectionUtils;
import com.wmods.wppenhacer.xposed.utils.Utils;

import java.util.concurrent.atomic.AtomicReference;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class TextStatusComposer extends Feature {
    private static AtomicReference<ColorData> colorData = new AtomicReference<>();

    public TextStatusComposer(@NonNull ClassLoader classLoader, @NonNull XSharedPreferences preferences) {
        super(classLoader, preferences);
    }

    @Override
    public void doHook() throws Throwable {
        var setColorTextComposer = Unobfuscator.loadTextStatusComposer(classLoader);
        log("setColorTextComposer: " + Unobfuscator.getMethodDescriptor(setColorTextComposer));

        XposedHelpers.findAndHookMethod("com.whatsapp.textstatuscomposer.TextStatusComposerActivity", classLoader, "onCreate", classLoader.loadClass("android.os.Bundle"),
                new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        var activity = (Activity) param.thisObject;
                        var viewRoot = activity.getWindow().getDecorView();
                        var pickerColor = viewRoot.findViewById(Utils.getID("color_picker_btn", "id"));
                        var entry = (EditText) viewRoot.findViewById(Utils.getID("entry", "id"));

                        pickerColor.setOnLongClickListener(v -> {
                            var dialog = new SimpleColorPickerDialog(activity, color -> {
                                XposedHelpers.setObjectField(param.thisObject, "A02", color);
                                ReflectionUtils.callMethod(setColorTextComposer, null, param.thisObject);
                            });
                            dialog.create().setCanceledOnTouchOutside(false);
                            dialog.show();
                            return true;
                        });

                        var textColor = viewRoot.findViewById(Utils.getID("font_picker_btn", "id"));
                        textColor.setOnLongClickListener(v -> {
                            var dialog = new SimpleColorPickerDialog(activity, color -> {
                                var colorData = new ColorData();
                                colorData.instance = param.thisObject;
                                colorData.color = color;
                                TextStatusComposer.colorData.set(colorData);
                                entry.setTextColor(color);
                            });
                            dialog.create().setCanceledOnTouchOutside(false);
                            dialog.show();
                            return true;
                        });


                    }
                });

        var setColorTextComposer2 = Unobfuscator.loadTextStatusComposer2(classLoader);
        log("setColorTextComposer2: " + Unobfuscator.getMethodDescriptor(setColorTextComposer2));
        XposedBridge.hookMethod(setColorTextComposer2, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (colorData.get() != null) {
                    var message = param.getResult();
                    var textData = XposedHelpers.getObjectField(message, "A02");
                    XposedHelpers.setObjectField(textData, "textColor", colorData.get().color);
                    colorData.set(null);
                }
            }
        });

    }

    @NonNull
    @Override
    public String getPluginName() {
        return "Text Status Composer";
    }

    public static class ColorData {
        public Object instance;
        public int color;
    }
}
