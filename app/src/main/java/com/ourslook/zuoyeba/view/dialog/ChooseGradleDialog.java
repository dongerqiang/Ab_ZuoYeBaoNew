package com.ourslook.zuoyeba.view.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.model.GradeModel;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by huangyi on 16/5/17.
 */
public class ChooseGradleDialog extends BaseDialog implements View.OnClickListener {
    @Bind(R.id.btn_sure_choose_gradle_dialog)
    Button btnSureChooseGradleDialog;
    @Bind(R.id.pic_choose_gradle_dialog)
    NumberPicker picChooseGradleDialog;
    ArrayList<GradeModel> gradleList;
    String[] gradleStrings;

    int gradleId;

    onChooseDialogListern mchooseDialog;
    @Bind(R.id.ll_chooseGradle_back)
    LinearLayout llChooseGradleBack;

    public ChooseGradleDialog(Context context, ArrayList<GradeModel> gradleList, int gradleId, onChooseDialogListern mchooseDialog) {
        super(context, R.layout.dialog_choose_gradle);
        this.gradleList = gradleList;
        this.gradleId = gradleId;
        this.mchooseDialog = mchooseDialog;
        init();
    }

    private void init() {
        gradleStrings = new String[gradleList.size()];
        int index = 0;
        for (int i = 0; i < gradleList.size(); i++) {
            gradleStrings[i] = gradleList.get(i).gradename;
            if (gradleList.get(i).gradeid == gradleId) {
                index = i;
            }
        }
        NumberPicker picker = picChooseGradleDialog;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(picker, new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimary)));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        picChooseGradleDialog.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picChooseGradleDialog.setDisplayedValues(gradleStrings);
        picChooseGradleDialog.setMaxValue(gradleStrings.length - 1);
        setOnClickListeners(this, btnSureChooseGradleDialog,llChooseGradleBack);
        picChooseGradleDialog.setValue(index);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure_choose_gradle_dialog:
                dismiss();
                mchooseDialog.chooseDialog(gradleList.get(picChooseGradleDialog.getValue()));
                break;
            case R.id.ll_chooseGradle_back:
                dismiss();
                break;
        }
    }

    public interface onChooseDialogListern {
        void chooseDialog(GradeModel model);
    }
}
