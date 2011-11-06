package stolksdorf.media.entropy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import stolksdorf.media.entropy.R;
import stolksdorf.media.entropy.EntropyService.JackHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Entropy extends Activity {	
	private static final String TAG = "Entropy";
	
	
//	Menus SettingsMenu;
//	Buttons ControlButtons;
	private ArrayList<Song> library;
	private ListView List;	
	
	private ImageButton btnPrevious;
	private ImageButton btnPlay;
	private ImageButton btnNext;
	
	private AlertDialog sortAlert;
	private AlertDialog orderAlert;
	private AlertDialog viewAlert;
	
	int CURRENT_ORDER;
	int CURRENT_VIEW;
	int CURRENT_SORT=-1;
	int CURRENT_SIZE=35;
	
	private Vibrator hapticFeedback;
	private SharedPreferences preferences;
	
    private boolean isPlaying = false;
    private boolean pausedFromJack = false;
    
    private int listPosition = -1;

	
	//private boolean isPlaying = false; 
	


	
     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  
        
        hapticFeedback = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        List = (ListView)findViewById(R.id.song_list);
        registerForContextMenu(List);
        
        //load preferences
     	preferences = getSharedPreferences(Settings.PREFS_NAME, 0);
     	CURRENT_ORDER = preferences.getInt(Settings.ORDER_PREF, 0);
     	CURRENT_VIEW = preferences.getInt(Settings.VIEW_PREF, 2);   
     	CURRENT_SORT = preferences.getInt(Settings.SORT_PREF, -1); 
     	
     	//Create data for Activity
     	loadLibrary();
    	createButtons();
    	createMenus();
    	sortList(CURRENT_SORT);
    	 
    	Log.v(TAG,"Is the service running? " + preferences.getInt(Constants.PREF_RUNNING, 0));
     	
    	//Check to see if the service is already running
     //	if(preferences.getInt(Constants.PREF_RUNNING, 0) == 1){
     		
     		
     	//}else{
     		startService();
        	addLibraryToService();
        	sendUpdateOrderIntent(CURRENT_ORDER);  
        	//checkServicePlaying();
     //	}

   		this.registerReceiver(new JackHandler(), new IntentFilter(Intent.ACTION_HEADSET_PLUG));
   		this.registerReceiver(new JackHandler(), new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        
    }
     



     
	//Make sure when the jack is removed our play button pauses with the music
 	public class JackHandler extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
		    if (intent.getAction().equalsIgnoreCase(Intent.ACTION_HEADSET_PLUG) && intent.getIntExtra("state", 0) ==1){
		    	checkServicePlaying();
		    	if(pausedFromJack){
		    		makeToast("Headphones are back! Let's keep grooving");
		    		pausedFromJack = false; 
		       	 	btnPlay.setBackgroundResource(R.drawable.pause);
		       	 	isPlaying = true;
		    	}
		    } else if (intent.getAction().equalsIgnoreCase(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) { 
		    		isPlaying = false;
		    		pausedFromJack = true;
		    		btnPlay.setBackgroundResource(R.drawable.play);
		    		makeToast("Woah! Where'd the headphones go?");
		    }
		}
	}     
     
     
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {    	
    	 menu.add(0, Constants.MENU_SORT, 0, "Sort Songs");   //Make strings constants
         menu.add(0, Constants.MENU_ORDER, 0, "Play Order");
         menu.add(0, Constants.MENU_VIEW, 0, "View");
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
    	 switch(item.getItemId()) {
         case Constants.MENU_SORT:
         	sortAlert.show();             
             return true;
         case Constants.MENU_ORDER:
         	orderAlert.show();
         	return true;
         case Constants.MENU_VIEW:
         	viewAlert.show();
         	return true;
     }
     return false;
     } 
     
     //The menu for setting up long click on songs
     @Override 
     public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
       if (v.getId()==R.id.song_list) {
         AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
         
         listPosition = info.position;
         
         menu.setHeaderTitle(library.get(info.position).title + " - " + library.get(info.position).artist);
         for (int i = 0; i<Constants.CONTEXT_OPTIONS.length; i++)
           menu.add(Menu.FLAG_ALWAYS_PERFORM_CLOSE, i, i, Constants.CONTEXT_OPTIONS[i]);

       }
     }
     
     //Events for when you long click a song
     @Override
     public boolean onContextItemSelected(MenuItem item) {
       final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
       int menuItemIndex = item.getItemId();
       if(menuItemIndex == 0){
    	   Log.v(TAG,"You hit Edit!");
    	   new AlertDialog.Builder(this)
           		.setMessage("Totally not in this version, sorry bro!")
           		.setPositiveButton("OK", null)
           		.show();
       } else if(menuItemIndex == 1){
    	   Log.v(TAG,"You hit Repeat!");
  	    	CURRENT_ORDER = Constants.ORDER_LOOP;
   	    	sendUpdateOrderIntent(Constants.ORDER_LOOP);
   	    	writePref(Constants.PREF_ORDER, Constants.ORDER_LOOP);
   	    	makeToast("Play order switched to " + Constants.ORDER_OPTIONS[Constants.ORDER_LOOP]); 

   	    	
   	    	Log.v(TAG,"MenuItem: " + listPosition );
   	    	//Switch to the song
   	    	sendSongIntent(library.get(listPosition).id);
       } else if(menuItemIndex == 2){
    	   Log.v(TAG,"You hit Delete!");    	   
    	   
    	   //Create a dialog just to make sure they want to delete their song
    	  new AlertDialog.Builder(this)
    	   		.setMessage("Are you sure you want to delete " + library.get(info.position).title + "?")
    	        .setCancelable(false) 
    	        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	              public void onClick(DialogInterface dialog, int id) {
    	                  Log.v(TAG,"Going to delete "+ library.get(info.position).title);
    	                  sendDeleteSongIntent(library.get(info.position).id);
    	                  library.remove(info.position);
    	                  loadList();    	               //reload list after removing the song
    	                  makeToast("Deleted " + library.get(info.position).title);
    	              }
    	          })
    	        .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	              public void onClick(DialogInterface dialog, int id) {
    	                   dialog.cancel();
    	              }
    	          })
    	        .show();
       }
       return true;
     }
     
     
     public void createButtons(){
    	 
    	 btnPrevious = (ImageButton)findViewById(R.id.previous);
    	 btnPlay = (ImageButton)findViewById(R.id.play);
         btnNext = (ImageButton)findViewById(R.id.next);
         
    	 btnPrevious.setOnClickListener(new ImageButton.OnClickListener() { 
         	public void onClick (View v){ 
         		hapticFeedback.vibrate(50);
 				sendIntent(Constants.INTENT_PREVIOUS);     
     			btnPlay.setBackgroundResource(R.drawable.pause);
     			isPlaying = true;
         	}
         }); 
         
     	 btnPlay.setOnClickListener(new ImageButton.OnClickListener() { 
         	public void onClick (View v){
         		hapticFeedback.vibrate(50);
         		if(isPlaying){
         			sendIntent(Constants.INTENT_PAUSE);
         			btnPlay.setBackgroundResource(R.drawable.play);
         			isPlaying = false;
         		}else{
         			sendIntent(Constants.INTENT_RESUME);
         			btnPlay.setBackgroundResource(R.drawable.pause);
         			isPlaying = true;
         		}
         	}
         }); 
         
     	 btnNext.setOnClickListener(new ImageButton.OnClickListener() { 
         	public void onClick (View v){ 
         		hapticFeedback.vibrate(50);
         		sendIntent(Constants.INTENT_NEXT);     
     			btnPlay.setBackgroundResource(R.drawable.pause);
     			isPlaying = true;  		
         	}
         }); 
 
     }
      
     public void createMenus(){
       	AlertDialog.Builder sort_builder = new AlertDialog.Builder(this);
       	AlertDialog.Builder order_builder = new AlertDialog.Builder(this);
       	AlertDialog.Builder view_builder = new AlertDialog.Builder(this);
       	
       	sort_builder.setTitle("Choose a sort order");   //TODO: Make constants
       	order_builder.setTitle("Choose play order");
       	view_builder.setTitle("Choose a list size order");
       	
       	//Sorts our play list differently
       	sort_builder.setSingleChoiceItems(Constants.SORT_OPTIONS, CURRENT_SORT, new DialogInterface.OnClickListener() {
       	    public void onClick(DialogInterface dialog, int item) { 
       	    	hapticFeedback.vibrate(150);
       	    	Log.v(TAG, "Clicked sort dialog");
       	    	sortList(item);  //TODO: add in toast confirmation
       	    	writePref(Constants.PREF_SORT, item);
                makeToast("Sorting switched to " + Constants.SORT_OPTIONS[item]);
       	    	dialog.dismiss(); 
       	    	} 
       	});
       	
        //Changes the play order of the songs   	
       	order_builder.setSingleChoiceItems(Constants.ORDER_OPTIONS, CURRENT_ORDER, new DialogInterface.OnClickListener() {
       	    public void onClick(DialogInterface dialog, int item) { 
       	    	hapticFeedback.vibrate(150);
       	    	Log.v(TAG, "clicked order dialog");
       	    	CURRENT_ORDER = item;
       	    	sendUpdateOrderIntent(item);
       	    	writePref(Constants.PREF_ORDER, item);
       	    	makeToast("Play order switched to " + Constants.ORDER_OPTIONS[item]);
       	    	dialog.dismiss(); 
       	    } 
       	});
       	

       	//Set the view different for differnet font sizes
       	view_builder.setSingleChoiceItems(Constants.VIEW_OPTIONS, CURRENT_VIEW, new DialogInterface.OnClickListener() {
       	    public void onClick(DialogInterface dialog, int item) { 
       	    	hapticFeedback.vibrate(150);
       	    	Log.v(TAG, "clicked view dialog");
       	    	changeListView(item);
       	    	writePref(Constants.PREF_VIEW, item);
       	    	makeToast("List view switched to " + Constants.VIEW_OPTIONS[item]);
       	    	dialog.dismiss(); 
       	    } 
       	});
       	
       	sortAlert = sort_builder.create();
       	orderAlert = order_builder.create();
       	viewAlert = view_builder.create();    	 
     }
     
    public void loadList(){
  		
 		List.setAdapter(new SongListAdapter(this,library, CURRENT_SIZE)); 		
 		List.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				hapticFeedback.vibrate(100); //TODO: make Constant	            
	            sendSongIntent(library.get(position).id);
	            isPlaying = true;
	            btnPlay.setBackgroundResource(R.drawable.pause);  
			}
	     });
     }

	public void sortList(int sort_order){
    	CURRENT_SORT=sort_order;
     	Comparator<Song> c = new AscendingAge(); 
    	
     	Log.v(TAG, "Sorting with order " + sort_order);
        switch(sort_order) {
        case Constants.SORT_TITLE:
        	c = new AscendingTitle();         
            break;
        case Constants.SORT_ARTIST:
        	c = new AscendingArtist(); 
        	break;   
        case Constants.SORT_AGE:
        	c = new AscendingAge(); 
        	break; 
        }    	 
        Collections.sort(library, c);        
    	loadList();     	 
     }
     
	public void changeListView(int view){
	    	//CURRENT_VIEW = type;
	    	float temp_size = 35;  //TODO: Make these constants
	    	switch(view){
	    	case Constants.VIEW_TINY:
	    		CURRENT_SIZE = Constants.FONT_TINY;
	    		break;
	    	case Constants.VIEW_NORMAL:
	    		CURRENT_SIZE =Constants.FONT_NORMAL;
	    		break;
	    	case Constants.VIEW_BIG:
	    		CURRENT_SIZE =Constants.FONT_BIG;
	    		break;
	    	case Constants.VIEW_BIBLICAL:
	    		CURRENT_SIZE =Constants.FONT_BIBLICAL;
	    		break;    		
	    	}  
	    	Log.v(TAG, "Changing list with size of "+ CURRENT_SIZE);
	    	loadList();		
	}
	
	//Checks if the service is playing and changes the play/pause button accordingly
	public void checkServicePlaying(){
 		if(preferences.getInt(Constants.PREF_ISPLAYING, 0) == 1){
 			isPlaying = true;
 			btnPlay.setBackgroundResource(R.drawable.pause);     			
 		}else{
 			isPlaying = false;
 			btnPlay.setBackgroundResource(R.drawable.play);   
 		}  	
	}
    
     ////////////////// LIBRARY STUFF ///////////////////
     
     public void loadLibrary(){ 
    	 //Clear out the library
    	 library = new ArrayList<Song>();
 	  	
 		//Set up the database query string
 		String[] songQuery= new String[] {				
 				android.provider.MediaStore.Audio.Media._ID,
 		        android.provider.MediaStore.Audio.Media.TITLE,
 		        android.provider.MediaStore.Audio.Media.ARTIST,
 		        android.provider.MediaStore.Audio.Media.DISPLAY_NAME};
 		
 		//Call up all content on our sd-card and apply our query
 		Cursor songCursor = managedQuery( 
 				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
 				songQuery, null, null, null);  
 		
 		//Loop through each row in the db
 		while(songCursor.moveToNext()) {
 				Song songTemp = new Song();
 		    	  
 				songTemp.id = Integer.parseInt(songCursor.getString(0));
 				songTemp.title = songCursor.getString(1);
 				songTemp.artist = songCursor.getString(2);
 				songTemp.path = Constants.BASE_PATH + songCursor.getString(3); //produces an absolute path to song
 				songTemp.age = library.size();

 				library.add(songTemp);
 		} 
     } 
     
     public void addLibraryToService(){
    	 for(Song song : library )
    		 sendAddSongIntent(song);
    	 //send library finsihed intent?
     }
     
     public void makeToast(String text){
    	 Toast.makeText(this, text, Toast.LENGTH_SHORT).show();    	 
     }
     

     //////////////////  Testing Functions  ////////
        
     public void makeAlert(String text){
    	new AlertDialog.Builder(this)
		            .setMessage(text)
		            .setPositiveButton("OK", null)
		            .show();
		
     }
    
     public ArrayList<Song> TestData(){
    	ArrayList<Song> temp_data = new ArrayList<Song>(); 
    	 
    	Song temp1 = new Song();
 		temp1.age=0;
 		temp1.id= 35;
 		temp1.artist="ZMetric";
 		temp1.title="YBlack Sheep";
 		temp1.playCount=56;
 		temp1.path = "/sdcard/Music/12 Black Sheep.mp3";
 		
 		temp_data.add(temp1);
 		Song temp2 = new Song();
 		temp2.age=1;
 		temp2.id= 28;
 		temp2.artist="YMuse";
 		temp2.title="ZFeeling Good";
 		temp2.playCount=8;
 		temp2.path = "/sdcard/Music/08 Crown On The Ground.mp3";
 		
 		temp_data.add(temp2);
 		Song temp3 = new Song();
 		temp3.age=2;
 		temp3.id= 89;
 		temp3.artist="XSex Bob-omb";
 		temp3.title="XThreshold";
 		temp3.playCount=102;
 		temp3.path = "/sdcard/Music/13 Threshold.mp3";
 		
 		temp_data.add(temp3);
 		return temp_data;   	

    	   	 
     }
     
     ////////////////// PREFERENCES ////////////////

     
     public void writePref(String pref, int value){
     	SharedPreferences.Editor prefEditor = preferences.edit();
     	prefEditor.putInt(pref, value);
     	prefEditor.commit();
     }
     
     //////SERVICE COMMUNICATION ///////////
     
     public void startService(){
    	 startService(new Intent(this, EntropyService.class));   	 
     }
     
     public void stopService(){    	 
    	 stopService(new Intent(this, EntropyService.class));
     }
     
     public void sendIntent(String type){
    	 Log.v("Entropy", "Sending intent with type "+ type);
    	 Intent intent = new Intent(this, EntropyService.class);
 		 intent.putExtra("type",type);  		
  		 startService(intent);  		
     }
     
     //Plays a song 
     public void sendSongIntent(int songId){
    	 Log.v("Entropy", "Sending Song intent with id "+ songId);
    	 btnPlay.setBackgroundResource(R.drawable.pause);
 		 isPlaying = true;
    	 Intent intent = new Intent(this, EntropyService.class);
 		 intent.putExtra("type",Constants.INTENT_PLAYSONG);  
 		 intent.putExtra("songId", songId);
  		 startService(intent);  		
     }
     
     //Adds a song to the service
     public void sendAddSongIntent(Song song){
    	 Log.v("Entropy", "Sending Add Song intent with id "+ song.id);
    	 Intent intent = new Intent(this, EntropyService.class);
 		 intent.putExtra("type",Constants.INTENT_ADDSONG);  
 		 intent.putExtra("songId", song.id);
 		 intent.putExtra("path", song.path);
 		 intent.putExtra("artist", song.artist);
		 intent.putExtra("title", song.title);
  		 startService(intent);      	 
     }
     
     public void sendDeleteSongIntent(int songId){
    	 Log.v("Entropy", "Sending Add Song intent with id "+ songId);
    	 Intent intent = new Intent(this, EntropyService.class);
 		 intent.putExtra("type",Constants.INTENT_DELETE);  
 		 intent.putExtra("songId", songId);
  		 startService(intent);      	 
     }
     
     public void sendUpdateOrderIntent(int playOrder){
    	 Log.v("Entropy", "Sending update play order intent with id "+ playOrder);
    	 Intent intent = new Intent(this, EntropyService.class);
 		 intent.putExtra("type",Constants.INTENT_UPDATEORDER);  
 		 intent.putExtra("playOrder", playOrder);
  		 startService(intent);      	 
     }
    


    	 
     ///////////// OTHER COOL THINGS /////////////
     
     
     
     
}

/////////////// COMPARATORs /////////////////
class AscendingTitle implements Comparator<Song>{
	public int compare(Song s1, Song s2){
		return s1.title.compareTo(s2.title);
	}	
}

class AscendingArtist implements Comparator<Song>{
	public int compare(Song s1, Song s2){
		return s1.artist.compareTo(s2.artist);
	}	
}

class AscendingAge implements Comparator<Song>{
	public int compare(Song s1, Song s2){
		return s2.age - s1.age;
	}	
}



