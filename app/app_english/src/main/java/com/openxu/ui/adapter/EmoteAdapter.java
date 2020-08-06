package com.openxu.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.openxu.db.bean.FaceText;
import com.openxu.english.R;
import com.openxu.utils.MyUtil;

public class EmoteAdapter extends BaseArrayListAdapter {

	private String TAG = "EmoteAdapter";
	public EmoteAdapter(Context context, List<FaceText> datas) {
		super(context, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_face_text, null);
			holder = new ViewHolder();
			holder.mIvImage = (ImageView) convertView.findViewById(R.id.iv_face_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FaceText faceText = (FaceText) getItem(position);
		MyUtil.LOG_I(TAG, "获取表情text:"+faceText);
		String key = faceText.text.substring(1);
		Drawable drawable =mContext.getResources().getDrawable(mContext.getResources().getIdentifier(key, "drawable", mContext.getPackageName()));
		holder.mIvImage.setImageDrawable(drawable);
		return convertView;
	}

	class ViewHolder {
		ImageView mIvImage;
	}
}
