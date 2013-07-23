package postpc.musica;

import android.app.Service;
import android.content.Intent;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;
import android.media.AudioManager;

public class SoundPoolService extends Service{

    private final IBinder mBinder = new ServiceBinder();
    SoundPool mPlayer;
    private int length = 0;
    Intent intent;
    int right,left;
    public SoundPoolService() { }

    /*
     * This special binder returns the service object in
     * its method getService()
     * so the activity can use the service public methods
     */
    public class ServiceBinder extends Binder {
     	 SoundPoolService getService()
    	 {
    		return SoundPoolService.this;
    	 }
    }

    /*
     *  implement the onBind() callback method for the bound service.
     *  This method returns an IBinder object that defines the
     *  programming interface that clients can use to 
     *  interact with the service.
     */
    public IBinder onBind(Intent arg0)
    {
    	intent = arg0;
    	right = intent.getIntExtra("right", 100);
    	left  = intent.getIntExtra("left", 100);
    	return mBinder;
    }
    int soundId;
    @Override
    public void onCreate (){
	  super.onCreate();
      mPlayer = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
 	   soundId = mPlayer.load(this,R.raw.m, 1);
       
       
	}

    @Override
	public int onStartCommand (Intent intent, int flags, int startId)
	{
         return START_STICKY;
	}

	public void pauseMusic()
	{
		/*if(mPlayer.isPlaying())
		{
			mPlayer.pause();
			length=mPlayer.getCurrentPosition();

		}*/
	}

	public void resumeMusic()
	{	
		mPlayer.play(soundId, 0.99f, 0.99f, 1, 0, 1);
	}

	public void stopMusic()
	{
		//mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
	}

	@Override
	public void onDestroy ()
	{
		super.onDestroy();
		if(mPlayer != null)
		{
		try{
		
		 mPlayer.release();
			}finally {
				mPlayer = null;
			}
		}
	}

}