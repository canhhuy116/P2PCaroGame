package com.example.ui_caro_game;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragHome extends Fragment implements FragmentCallBacks {
   HomeActivity main;
   Context context = null;
   String msg = "";
   Integer pos;
   Button Caro_AI, DiscoveryBtn;
   public static FragHome newInstance(String Arg) {
      FragHome fragment = new FragHome();
      Bundle args = new Bundle();
      args.putString("Arg1", Arg);
      fragment.setArguments(args);
      return fragment;
   }
   @Override
   public void onCreate( Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      try {
         context = getActivity();
         main = (HomeActivity) getActivity();
      } catch (IllegalStateException e) {
         throw new IllegalStateException("Error");
      }
   }



   @Override
   public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
      LinearLayout layout_left = (LinearLayout) inflater.inflate(R.layout.layout_home, null);
      Caro_AI=(Button) layout_left.findViewById(R.id.play_now);
      DiscoveryBtn=(Button) layout_left.findViewById(R.id.play_with_friend);
      //txtLeft = (TextView) layout_left.findViewById(R.id.ID);

      ///ist = (ListView) layout_left.findViewById(R.id.list_item);



      //CustomLabel adapter = new CustomLabel(context,R.layout.items, ID,Avt);


      // ArrayAdapter<String> adapter= new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,ID);
      //list.setAdapter(adapter);
     // list.setSelection(0);
      //list.smoothScrollToPosition(0);

     /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
            main.onMsgFromFragToMain("Left-frag", i);
            txtLeft.setText(ID[i]);


            list.getChildAt(i).setBackgroundColor(Color.rgb(120, 223, 151));
            if(tmp!=-1)
               list.getChildAt(tmp).setBackgroundColor(Color.rgb(43, 188, 230));

            tmp=i;

         }
      });*/
      Caro_AI.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            main.onMsgFromFragToMain("Left-frag", 250,"");
         }
      });
      DiscoveryBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            main.onMsgFromFragToMain("Left-frag", 251,"");
         }
      });
      return layout_left;
   }
   @Override
   public void onMsgFromMainToFragment(String text,Integer x ,Integer y) {

      /*pos=i;
      txtLeft.setText(ID[i]);
      main.onMsgFromFragToMain("Left-frag",i);
      if(tmp!=i){
         list.getChildAt(tmp).setBackgroundColor(Color.rgb(43, 188, 230));

         list.getChildAt(i).setBackgroundColor(Color.rgb(120, 223, 151));
         tmp=i;
      }*/
   }

}



