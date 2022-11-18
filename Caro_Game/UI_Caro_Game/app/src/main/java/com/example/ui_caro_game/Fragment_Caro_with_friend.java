package com.example.ui_caro_game;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragment_Caro_with_friend  extends Fragment implements FragmentCallBacks {
    HomeActivity main;
    EditText edt1;
    TextView tv1;
    Button bt_send;
    public static Fragment_Caro_with_friend newInstance(String Arg1)
    {
        Fragment_Caro_with_friend fragment= new Fragment_Caro_with_friend();
        Bundle bundle = new Bundle();
        bundle.putString("Arg1",Arg1);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!(getActivity() instanceof  MainCallBacks))
        {
            throw new IllegalStateException("Error");
        }
        main = (HomeActivity) getActivity();

    }
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        LinearLayout layout_right=(LinearLayout) inflater.inflate(R.layout.caro_with_friend,null);
        edt1=(EditText) layout_right.findViewById(R.id.edt1);
        tv1=(TextView) layout_right.findViewById(R.id.tv1);
        bt_send=(Button) layout_right.findViewById(R.id.send);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=edt1.getText().toString();
                main.onMsgFromFragToMain("abc",200,msg);
            }
        });

        return layout_right;
    }
    @Override
    public void onMsgFromMainToFragment(String text,Integer x , Integer y) {
        /*txtID.setText(ID[i]);
        txtTEN.setText("Họ tên: "+ten[i]);
        txtLOP.setText("Lớp: "+lop[i]);
        txtDIEM.setText("Điểm trung bình : "+diem[i]);
        pos=i;*/
        tv1.setText(text);
    }

}
