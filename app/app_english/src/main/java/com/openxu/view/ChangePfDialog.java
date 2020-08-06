package com.openxu.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.openxu.english.R;

public class ChangePfDialog extends ItotemBaseDialog {

	private MyAdapter adapter;
	private ListView lv_color;
	private int witch;
	private PfListener listener;
	private TextView tv_cancle;

    public ChangePfDialog(Context context, int witch) {
        super(context, R.layout.dialog_change_pf, R.style.ItotemTheme_Dialog);
        this.witch = witch-1;
        adapter.setSelected(this.witch);
    }

    @Override
    protected void initView() {
    	lv_color = (ListView) findViewById(R.id.lv_color);
    	adapter = new MyAdapter(getContext());
    	lv_color.setAdapter(adapter);
    	tv_cancle = (TextView) findViewById(R.id.tv_cancle);
    	tv_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
    }

    public void setPfListener(PfListener listener){
    	this.listener = listener;
    }
    @Override
    protected void initData() {
    }

    @Override
    protected void setListener() {

    }

    class MyAdapter extends BaseAdapter{
    	private int selected;
		private LayoutInflater mInflater;
		private String colors[] = new String[]{"天蓝","草绿", "粉红"};
		private int colorId[] = new int[]{R.color.pf1_title_bg, R.color.pf2_title_bg, R.color.pf3_title_bg};

		public MyAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getSelected() {
			return selected;
		}

		public void setSelected(int selected) {
			this.selected = selected;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return colors.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.dialog_change_pf_item, null);
				holder = new ViewHolder();
				holder.ll_item = (LinearLayout) convertView
						.findViewById(R.id.ll_item);
				holder.iv_check = (ImageView) convertView
						.findViewById(R.id.iv_check);
				holder.tv_text = (TextView) convertView
						.findViewById(R.id.tv_text);
				holder.iv_color = (ImageView) convertView
						.findViewById(R.id.iv_color);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			if(selected==position)
				holder.iv_check.setPressed(true);
			else 
				holder.iv_check.setPressed(false);
			holder.tv_text.setText(colors[position]);
			holder.iv_color.setImageResource(colorId[position]);
			holder.ll_item.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					selected = position;
					notifyDataSetChanged();
					int color = adapter.getSelected();
					listener.backPf(color+1);
					cancel();
				}
			});
			return convertView;
		}
	}
    private class ViewHolder {
    	private LinearLayout ll_item;
    	public ImageView iv_check;
		public TextView tv_text;
		public ImageView iv_color;
	}

    public interface PfListener{
    	public abstract void backPf(int color);
    }
    
}
