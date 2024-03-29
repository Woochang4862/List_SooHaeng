package com.jeongwoochang.list_soohaeng.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.jeongwoochang.list_soohaeng.R;

import androidx.annotation.NonNull;

public class GroupNameInputDialog extends Dialog {

    private TextInputEditText groupName;
    private TextView addBtn;
    private OnAddButtonClickListener onAddButtonClickListener;

    public GroupNameInputDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.dialog_group_name_input);

        groupName = findViewById(R.id.group_name);
        addBtn = findViewById(R.id.add_button);
        addBtn.setOnClickListener(v -> {
            if (!groupName.getText().toString().isEmpty())
                if (onAddButtonClickListener != null)
                    onAddButtonClickListener.onAddButtonClick(GroupNameInputDialog.this, groupName.getText().toString());
        });
    }

    public interface OnAddButtonClickListener {
        void onAddButtonClick(Dialog dialog, String groupName);
    }

    public void setOnAddButtonClickListener(OnAddButtonClickListener onAddButtonClickListener) {
        this.onAddButtonClickListener = onAddButtonClickListener;
    }
}
