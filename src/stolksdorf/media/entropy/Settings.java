package stolksdorf.media.entropy;

import android.content.SharedPreferences;

//Stores all the settings in the application from the shared preference of the service
//And sets the Shared Preferences of the service

public class Settings{	
	public static String BASE_PATH = "/sdcard/Music/";
	public static String APP_NAME = "Entropy v0.3a";
	public static String PREFS_NAME = "EntropyPrefs";
	
	public static String ORDER_PREF = "OrderingPref";
	public static String VIEW_PREF = "ViewPref";
	public static String SORT_PREF = "SortPref";
	
	private SharedPreferences preferences;
	
	public int CURRENT_SORT=-1;
	public int CURRENT_ORDER;
	public int CURRENT_VIEW;
	
	final static int MENU_SORT = 0;
	final static int MENU_ORDER = 1;
	final static int MENU_VIEW = 2;
	
	final static int SORT_TITLE = 0;
	final static int SORT_ARTIST = 1;
	
	final static int ORDER_SHUFFLE = 0;
	final static int ORDER_RANDOM = 1;
	final static int ORDER_LOOP = 2;
	
	final static int VIEW_TINY = 0;
	final static int VIEW_NORMAL = 1;
	final static int VIEW_BIG = 2;
	final static int VIEW_BIBLICAL = 3;
	
	
	public Settings(){
		
		
	}
	
	//Set adapter to database!
	
	public void loadPrefs(){
	
    	//preferences = getSharedPreferences(Settings.PREFS_NAME, 0);    ///Look into why we can't find this function
    	CURRENT_ORDER = preferences.getInt(Settings.ORDER_PREF, 0);
    	CURRENT_VIEW = preferences.getInt(Settings.VIEW_PREF, 2);   
    	CURRENT_SORT = preferences.getInt(Settings.SORT_PREF, -1); 
    }
    
    public void writePrefs(String pref, int value){
    	SharedPreferences.Editor prefEditor = preferences.edit();
    	prefEditor.putInt(pref, value);
    	prefEditor.commit();   
    //	try {
			//eInterface.updatePrefs();
	/*	} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }
	
	
}
	