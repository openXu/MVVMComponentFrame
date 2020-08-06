package com.openxu.ui;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.openxu.db.bean.OneSentence;
import com.openxu.english.R;
import com.openxu.utils.MyUtil;
import com.openxu.utils.NetWorkUtil;

/**
 * @author openXu
 */
public class ActivitySearch extends BaseActivity implements OnClickListener{
	
	private ImageView iv_icon;
	private TextView tv_one_eng, tv_one_chi;
	private LinearLayout ll_langdu; 
	private MediaPlayer mp;
	
	private OneSentence sentence;
	@Override
	protected void initView() {
		setContentView(R.layout.sentence_activity);
		TAG = "ActivitySearch";
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_one_eng = (TextView) findViewById(R.id.tv_one_eng);
		tv_one_chi  = (TextView) findViewById(R.id.tv_one_chi);
		ll_langdu = (LinearLayout) findViewById(R.id.ll_langdu);
	}
	@Override
	protected void setPf() {
		super.setPf();
	}
	@Override
	protected void initData() {
		sentence = getIntent().getParcelableExtra("sentence");
		BitmapUtils bitmapUtils = new BitmapUtils(mContext);
		bitmapUtils.display(iv_icon, sentence.getPicture2());
		tv_one_eng.setText(sentence.getContent());
		tv_one_chi.setText(sentence.getNote());
		mp = new MediaPlayer();
		ll_langdu.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_langdu:
			if(!NetWorkUtil.isNetworkAvailable(mContext)){
				MyUtil.showToast(mContext, R.string.no_net, "");
				return;
			}
			try {
				try {
                    mp.setDataSource(sentence.getTts());
                    mp.prepare();
                    mp.start();
                 } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                 } catch (IllegalStateException e) {
                    e.printStackTrace();
                 } catch (IOException e) {
                    e.printStackTrace();
                 }
                 mp.setOnCompletionListener(new OnCompletionListener(){
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                 });
//				PLAY_STATE state = MyApplication.getApplication().player.getState();
//				if (state != PLAY_STATE.STOPED) {
//					Log.e(TAG, "上次正在合成，先结束");
//					MyApplication.getApplication().player.cancel();
//				}
//				MyApplication.getApplication().player.play(sentence.getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}


}
