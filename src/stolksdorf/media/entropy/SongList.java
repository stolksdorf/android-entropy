package stolksdorf.media.entropy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import stolksdorf.media.entropy.R;

import android.widget.AdapterView;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SongList{
	
	private ListView songList;	
	private List<Song> library;
	
	//Remove
	private Context alertBuilder;
	
	public SongList(ListView songView){		
		songList = songView;  
     	library =  new ArrayList<Song>();	
	}
	
	public SongList(ListView songView, List<Song> preloadedLibrary, Context temp){		
		songList = songView;  
     	library =  preloadedLibrary;
     	
     	alertBuilder = temp;
     	
     	setupListeners();
     	populateList();
	}
	
	//Make adapter to SQLlite database
	
    public void setupListeners(){
    	//When you click on a song, it plays your song!
    	songList.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
								///On songlist item click!	
							new AlertDialog.Builder(alertBuilder)
				            .setMessage(library.get(position).title)
				            .setPositiveButton("OK", null)
				            .show();
						}
				  });   			    	
    }
    
    public void populateList(){
		/*ArrayList<HashMap<String, String>> temp_list = new ArrayList<HashMap<String, String>>();
		
		for(Song song : temp_library){
			HashMap<String, String> infoMap = new HashMap<String, String>();
			infoMap.put("title", song.title);
			infoMap.put("artist", song.artist);
			temp_list.add(infoMap);
		}	*/	
		
		ArrayAdapter<Song> temp = new ArrayAdapter<Song>(alertBuilder, R.layout.song, library); 
		
		songList.setAdapter(temp);
		  
		 	
    	
    }
    
    public void sortSongs(int type){ 
    	switch(type) {
	        case Settings.SORT_TITLE:
	        	Collections.sort(library, new Comparator<Song>() {
     				public int compare(Song s1, Song s2) {
						return s1.title.compareToIgnoreCase(s2.title);
					}});   
	            break;
	        case Settings.SORT_ARTIST:
	        	Collections.sort(library, new Comparator<Song>() {
     				public int compare(Song s1, Song s2) {
						return s1.artist.compareToIgnoreCase(s2.artist);
					}});    
	        	break;   
           //add in other sort options
        }
    }
    
    //Sets the font of the song name in the song list
    public void setFontSize(float fontSize){   	
    	//Set each item in the list to it's new size
        for(int i = 0; i < songList.getChildCount(); i++) {
            ((TextView)((LinearLayout)songList.getChildAt(i)).findViewById(R.id.title)).setTextSize(fontSize);
        }
    }
    
    //For testing
    
    public void makeAlert(String text){
    	new AlertDialog.Builder(alertBuilder)
		            .setMessage(text)
		            .setPositiveButton("OK", null)
		            .show();
		
     }
    
     public void setTestData(){
        library = new ArrayList<Song>();
    	 
    	Song temp1 = new Song();
 		temp1.age=0;
 		temp1.id= 35;
 		temp1.artist="Metric";
 		temp1.title="Black Sheep";
 		temp1.playCount=56;
 		
 		library.add(temp1);
 		Song temp2 = new Song();
 		temp2.age=1;
 		temp2.id= 28;
 		temp2.artist="Muse";
 		temp2.title="Feeling Good";
 		temp2.playCount=8;
 		
 		library.add(temp2);
 		Song temp3 = new Song();
 		temp3.age=2;
 		temp3.id= 89;
 		temp3.artist="Sex Bob-omb";
 		temp3.title="Threshold";
 		temp3.playCount=102;
 		
 		library.add(temp3);  	 
    	   	 
     }
    
    
}
