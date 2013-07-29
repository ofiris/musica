package postpc.musica;


import java.util.List;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class listAdapt extends ArrayAdapter<youTubeEntery> {

	private SearchActivity _activity;
	private Master_Get_Connection _mServ;
	
	public listAdapt(SearchActivity activity, List<youTubeEntery> courses, Master_Get_Connection mServ) {
		super(activity, R.layout.youtube_list_row_layout, courses);
		_activity = activity;
		_mServ = mServ;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		youTubeEntery entery = getItem(position);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.youtube_list_row_layout, null);

		String title = entery.title;
		final String youTubeId = entery.videoId; 
		TextView mainText = (TextView)view.findViewById(R.id.title);
		mainText.setText(title);
		mainText.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) { 
				_mServ.startedPlayMode(youTubeId);
				Intent intent = new Intent(_activity, MasterPlayActivity.class);
				intent.putExtra("youTubeId",youTubeId); 
				_activity.startActivity(intent);
			}
		});
		ImageView play = (ImageView)view.findViewById(R.id.play);
		play.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) { 
				_mServ.startedPlayMode(youTubeId);
				Intent intent = new Intent(_activity, MasterPlayActivity.class);
				intent.putExtra("youTubeId",youTubeId); 
				_activity.startActivity(intent);
			}
		});
		

		return view;
	}




}