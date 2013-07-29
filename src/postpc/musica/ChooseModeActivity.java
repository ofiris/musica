package postpc.musica;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ChooseModeActivity extends ParentActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_mode);
		Button speaker    =(Button) findViewById(R.id.speaker);
		Button broadaster =(Button) findViewById(R.id.broadcaster);
		
		TextView txt = (TextView) findViewById(R.id.choose_mode_hint1);  
		Typeface font = Typeface.createFromAsset(getAssets(), "aescrawl.ttf");  
		
		txt.setTypeface(font);  
		txt.setTextSize(30);
		
		speaker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), ConnectSpeakerActivity.class);

				startActivity(i);
			}
		});

		broadaster.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), ConnectBroadcasterActivity.class);

				startActivity(i);
			}
		});



	}

	Activity getActivity(){
		return this;
	}

}
