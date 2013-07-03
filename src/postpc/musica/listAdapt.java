package postpc.musica;


import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class listAdapt extends ArrayAdapter<youTubeEntery> {

	SearchActivity _activity;//TODO
	public listAdapt(SearchActivity activity, List<youTubeEntery> courses) {
		super(activity, R.layout.youtube_list_row_layout, courses);
		_activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		youTubeEntery entery = getItem(position);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.youtube_list_row_layout, null);

		String title = entery.title;
		TextView mainText = (TextView)view.findViewById(R.id.title);
		mainText.setText(title);

		return view;
	}


}