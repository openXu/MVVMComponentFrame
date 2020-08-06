package cn.sharesdk.my;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.openxu.english.R;
import com.openxu.ui.MyApplication;

/**
 * TODO< 分享弹出框Adapter >
 * @data:  2014-7-21 下午2:30:32
 * @version:  V1.0
 */

public class ShareAdapter extends BaseAdapter {
	private Context context;
     private static String[] shareNames = new String[] {"微信", "QQ", "新浪微博","微信朋友圈", "QQ空间"};
        private int[] shareIcons = new int[] {R.drawable.logo_wechat, R.drawable.logo_qq,  
        		R.drawable.logo_sinaweibo,R.drawable.logo_wechatmoments,
                R.drawable.logo_qzone};
        private LayoutInflater inflater;
        public ShareAdapter(Context context)
        {
        	this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return shareNames.length;
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null){
                convertView = inflater.inflate(R.layout.share_item, null);
            }
            ImageView shareIcon = (ImageView) convertView.findViewById(R.id.share_icon);
            TextView shareTitle = (TextView) convertView.findViewById(R.id.share_title);
            shareTitle.setTextColor(context.getResources().getColor(MyApplication.pf.title_bg));
            shareIcon.setImageResource(shareIcons[position]);
            shareTitle.setText(shareNames[position]);

            return convertView;
        }
    }
