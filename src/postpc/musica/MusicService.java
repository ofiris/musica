package postpc.musica;

import android.app.Service;
import android.content.Intent;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service{

    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer;
    private int length = 0;
    Intent intent;
    int right,left;
    public MusicService() { }

    /*
     * This special binder returns the service object in
     * its method getService()
     * so the activity can use the service public methods
     */
    public class ServiceBinder extends Binder {
     	 MusicService getService()
    	 {
    		return MusicService.this;
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

    @Override
    public void onCreate (){
	  super.onCreate();
      mPlayer = MediaPlayer.create(this, R.raw.m);
       if(mPlayer!= null)
        {
        	mPlayer.setLooping(true);
        	mPlayer.setVolume(100,100);
        }
	}

    @Override
	public int onStartCommand (Intent intent, int flags, int startId)
	{
         return START_STICKY;
	}

	public void pauseMusic()
	{
		if(mPlayer.isPlaying())
		{
			mPlayer.pause();
			length=mPlayer.getCurrentPosition();

		}
	}

	public void resumeMusic()
	{
		if(mPlayer.isPlaying()==false)
		{
			mPlayer.seekTo(length);
			mPlayer.start();
		}
	}

	public void stopMusic()
	{
		mPlayer.stop();
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
		 mPlayer.stop();
		 mPlayer.release();
			}finally {
				mPlayer = null;
			}
		}
	}

}