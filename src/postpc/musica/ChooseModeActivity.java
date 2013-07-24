package postpc.musica;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChooseModeActivity extends ParentActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_mode);
		
		if(Consts.debugYouTube){
			Intent intent = new Intent(this, SearchActivity.class);
			//intent.putExtra("youTubeId",Consts.quckPlayVideo); //TODO 
			this.startActivity(intent);
			return;
		}
		
		Button speaker    =(Button) findViewById(R.id.speaker);
		Button broadaster =(Button) findViewById(R.id.broadcaster);

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
