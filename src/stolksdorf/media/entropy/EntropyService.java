package stolksdorf.media.entropy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Filter;
import android.widget.Toast;

public class EntropyService extends Service{
	private static final String TAG = "EntropyService";
	private HashMap<Integer, Song> library;
	private ArrayList<Integer> songHistory;
	private ArrayList<Integer> shuffleHistory;
	
	private int PLAYORDER = Constants.ORDER_RANDOM;
	private Song currentSong;
	MediaPlayer mediaPlayer;
	
	private static final int NOTIFY_ID = 1234;
    private NotificationManager notifyManager;
    private SharedPreferences preferences;
    
    private boolean isPlaying = false;
    private boolean pausedFromJack = false;


	
	@Override
	public void onCreate() {
		super.onCreate();
		notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		preferences = getSharedPreferences(Settings.PREFS_NAME, 0);		
		songHistory = new ArrayList<Integer>();
		shuffleHistory = new ArrayList<Integer>();
		library = new HashMap<Integer, Song>();
		mediaPlayer = new MediaPlayer();
		currentSong = new Song();
		currentSong.id = -1;
		
		Log.v(TAG, "CREATED"); 
		writePref(Constants.PREF_RUNNING, 1);
		
		
		this.registerReceiver(new JackHandler(), new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		this.registerReceiver(new JackHandler(), new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
		
	}

	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {		
		Log.v(TAG, "DESTROYED");
		notifyManager.cancel(NOTIFY_ID);		
		mediaPlayer.stop();
		writePref(Constants.PREF_RUNNING, 0);
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		
		if(intent.hasExtra("type")){
			String intent_type = intent.getStringExtra("type");
			
			if( intent_type.equalsIgnoreCase(Constants.INTENT_STARTUP)){ 
				Log.v(TAG, "Starting Service...");
			}else if(intent_type.equalsIgnoreCase(Constants.INTENT_NEXT)){
				Log.v(TAG, "Next Song");
				next(true);
			}else if(intent_type.equalsIgnoreCase( Constants.INTENT_PREVIOUS)){
				Log.v(TAG, "Previous Song");
				previous();
			}else if(intent_type.equalsIgnoreCase( Constants.INTENT_PAUSE)){
				Log.v(TAG, "Pausing Song");
				pause();
			}else if(intent_type.equalsIgnoreCase(Constants.INTENT_RESUME)){
				Log.v(TAG, "Resuming Song");
				resume();
			//Whent he user updates preferences critical for the service, we'll update them too	
			}else if(intent_type.equalsIgnoreCase(Constants.INTENT_UPDATEORDER)){
				Log.v(TAG, "Updating Play Order");
				if(intent.hasExtra("playOrder")){
					PLAYORDER =  intent.getIntExtra("playOrder",0);
				}
				
			}else if(intent_type.equalsIgnoreCase(Constants.INTENT_PLAYSONG)){
				if(intent.hasExtra("songId")){
					int temp_songId = intent.getIntExtra("songId",0);
					songHistory.add(temp_songId);
					if(!shuffleHistory.contains(temp_songId)) shuffleHistory.add(temp_songId);					
					playSong(temp_songId);
				}
			
				
			//Adds Song to service Library
			}else if(intent_type.equalsIgnoreCase(Constants.INTENT_ADDSONG)){
				if(intent.hasExtra("songId") && intent.hasExtra("path") && intent.hasExtra("artist") && intent.hasExtra("title")){
					Log.v(TAG, "adding Song "+intent.getStringExtra("path"));	
					Song temp_song = new Song();
					temp_song.id = intent.getIntExtra("songId", 0);
					temp_song.path = intent.getStringExtra("path");
					temp_song.artist = intent.getStringExtra("artist");
					temp_song.title = intent.getStringExtra("title");
					library.put(temp_song.id, temp_song );
				}	
			
			//Deletes song from the sd card
			}else if(intent_type.equalsIgnoreCase(Constants.INTENT_DELETE)){
				if(intent.hasExtra("songId")){
					Log.v(TAG, "Deleting Song "+library.get(intent.getIntExtra("songId",0)).title);	
					deleteSong(intent.getIntExtra("songId",0));
				}
			} 
		}
	}
	
	
	
	private void makeNotification(Song song){
		
		Log.v(TAG, song.title);
		
		notifyManager.cancel(NOTIFY_ID);
		String songString = song.title + " - " + song.artist;
		
		 Notification notification = new Notification(R.drawable.play, songString, System.currentTimeMillis());
		 
		 notification.flags |= Notification.FLAG_ONGOING_EVENT;
		 notification.flags |= Notification.FLAG_NO_CLEAR;		 

		 Intent notificationIntent = new Intent(this, Entropy.class);
		 PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		 notification.setLatestEventInfo(this, Settings.APP_NAME, songString, contentIntent);
		 notifyManager.notify(NOTIFY_ID, notification);	
	}
	
	public class JackHandler extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
		    if (intent.getAction().equalsIgnoreCase(Intent.ACTION_HEADSET_PLUG)) {
		        if(intent.getIntExtra("state", 0) ==1){
		        	Log.v(TAG, "Jack State changed to IN");
		        	if(pausedFromJack && !isPlaying) resume();
		        }
		        	
		    } else if (intent.getAction().equalsIgnoreCase(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) { 
		    	Log.v(TAG, "Jack State changed to OUT");
		    	//Pause while setting a boolean saying it was from the jack
		    	if(isPlaying){
			    	pause();
			    	pausedFromJack = true;
		    	}
		    }
		}
	}
	
	
	private void playSong(int songId){

		try {
			currentSong = library.get(songId);
			
			Log.v(TAG, "the library is now this big: " + library.size());
			
			makeNotification(currentSong);
			
			mediaPlayer.stop();
			mediaPlayer.reset();
			mediaPlayer.setDataSource(currentSong.path);
			mediaPlayer.prepare();
			mediaPlayer.start();
			writePref(Constants.PREF_ISPLAYING, 1);
			isPlaying = true;
			pausedFromJack = false;
			Log.v(TAG, "Playing song" + currentSong.id + " - " + currentSong.title + " " + currentSong.path);
		} catch (Exception e) {
			Log.v(TAG, "Song isn't playing correctly");
			e.printStackTrace();
		}		

		//When the song finishes, play the next one!
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
             public void onCompletion(MediaPlayer arg0) {
            	 next(false);
             }
		});
	}
	
	private void resume(){
		
		if(currentSong.path == null) next(true);
		else{
			mediaPlayer.start();
			writePref(Constants.PREF_ISPLAYING, 1);
			makeNotification(currentSong);
			isPlaying = true;
			pausedFromJack = false;
		}
	}
	
	private void pause(){
		mediaPlayer.pause();
		notifyManager.cancel(NOTIFY_ID);
		isPlaying = false;
		writePref(Constants.PREF_ISPLAYING, 0);
	}
	
	private void next(boolean fromControl){
		int resultId = 0;
		Random rand = new Random();
		if(Constants.ORDER_SHUFFLE ==PLAYORDER){
				//If we've listened to all the songs, clear the list!
				do{
					Object[] values = library.keySet().toArray();
					int randomKey = (Integer) values[rand.nextInt(values.length)];					
					resultId = library.get(randomKey).id;
					Log.v(TAG, "Trying " + resultId);
				}while(shuffleHistory.contains(resultId) || resultId == currentSong.id);	//Make sure we go through the library first before repeating songs
				
				//Add song to histories  //TODO: Possibly Externilize to a function
				shuffleHistory.add(resultId);
				if(shuffleHistory.size() >= library.size())	shuffleHistory.clear();
				songHistory.add(resultId);
		}else if(Constants.ORDER_LOOP == PLAYORDER && !fromControl){
				resultId = currentSong.id;
		}else{                         //PLAYORDER == RANDOM
			do{
				Object[] values = library.keySet().toArray();
				int randomKey = (Integer) values[rand.nextInt(values.length)];					
				resultId = library.get(randomKey).id;
			}while(resultId == currentSong.id);
			
			//Add our song to the histories
			shuffleHistory.add(resultId);
			if(shuffleHistory.size() >= library.size())	shuffleHistory.clear();
			songHistory.add(resultId);		
		}
		playSong(resultId);
	}
	
	private void previous(){
		Log.v(TAG, "the current position is " +mediaPlayer.getCurrentPosition() + " ans " + songHistory.size() );
		if(mediaPlayer.getCurrentPosition() < 5000 && songHistory.size()>1){  //Go to a previous song
			songHistory.remove(songHistory.size()-1); //remove current song from history
			playSong(library.get(songHistory.get(songHistory.size()-1)).id); //play the song previous to the current one
		}else{
			mediaPlayer.seekTo(0); //restart the song
			if(!isPlaying){
				mediaPlayer.start();
				writePref(Constants.PREF_ISPLAYING, 1);
				isPlaying = true;
				pausedFromJack = false;
			}
		}
	}
	
	private void deleteSong(int songId){
		//change song if it's the current
		if(songId == currentSong.id) next(true);
		

		//delete the file		
		File temp = new File(library.get(songId).path);
		boolean delete_success = temp.delete();
		if(delete_success)
			Toast.makeText(this, "Deleted Song " + library.get(songId).title + " by " + library.get(songId).title, Toast.LENGTH_SHORT).show();
		
		//remove all instances for each list
		if(shuffleHistory.contains(songId))
			shuffleHistory.remove(shuffleHistory.indexOf(songId)); 
		if(songHistory.contains(songId))
			songHistory.remove(songHistory.indexOf(songId));
		library.remove(songId);
	}
	
	
    public void writePref(String pref, int value){
     	SharedPreferences.Editor prefEditor = preferences.edit();
     	prefEditor.putInt(pref, value);
     	prefEditor.commit();
     }

	
	

	

}


	