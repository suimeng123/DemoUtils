package com.lx.demoutils.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lx.demoutils.R;
import com.lx.demoutils.utils.FragmentViewModel;

/**
 * com.lx.demoutils.fragment
 * DemoUtils
 * Created by lixiao2
 * 2018/9/30.
 */

public class ViewModelFragment extends Fragment {

    EditText editText;

    FragmentViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_viewmodel, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editText = view.findViewById(R.id.edit);
//        viewModel = ViewModelProviders.of(getActivity()).get(FragmentViewModel.class);
//
//
//        view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String content = editText.getText().toString();
//                if (TextUtils.isEmpty(content)) {
//                    showToast("内容不能为空");
//                    return;
//                }
//                //TODO 有数据就显示到另外一个Fragment
//                viewModel.setData(content);
//            }
//        });
    }


    Toast toast;
    private void showToast(String str) {
        if (toast == null) {
            toast = Toast.makeText(getActivity(),str,Toast.LENGTH_SHORT);
        } else {
            toast.setText(str);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
