package stolksdorf.media.entropy;

public class Constants {
	
	public static String BASE_PATH = "/sdcard/Music/";
	public static String APP_NAME = "Entropy v0.3a";
	public static String PREFS_NAME = "EntropyPrefs";
	
	public static String PREF_ORDER = "EntropyPlayOrderPref";
	public static String PREF_VIEW = "EntropyViewPref";
	public static String PREF_SORT = "EntropySortPref";
	public static String PREF_ISPLAYING = "EntropyIsPlaying";
	public static String PREF_RUNNING = "EntropyServiceRunning";
	
	public static String INTENT_STARTUP = "startup";
	public static String INTENT_PLAYSONG = "song";
	public static String INTENT_RESUME = "resume";
	public static String INTENT_PAUSE = "pause";
	public static String INTENT_PREVIOUS = "previous";
	public static String INTENT_NEXT = "next";
	public static String INTENT_UPDATEORDER = "update_play_order";
	public static String INTENT_ADDSONG = "addsong";
	public static String INTENT_DELETE = "delete";
	
	//Move to Settings? Make a Constants class?
	final static CharSequence[] SORT_OPTIONS = {"By Title", "By Artist", "By Newest"};	
	final static CharSequence[] ORDER_OPTIONS = {"Shuffle", "Random", "Single Loop"};	
	final static CharSequence[] VIEW_OPTIONS = {"Tiny", "Normal", "Big", "Biblical"};
	final static CharSequence[] CONTEXT_OPTIONS = {"Edit", "Repeat Indefinitely", "Delete"};
	
	
	
	final static int MENU_SORT = 0;
	final static int MENU_ORDER = 1;
	final static int MENU_VIEW = 2;
	
	final static int SORT_TITLE = 0;
	final static int SORT_ARTIST = 1;
	final static int SORT_AGE = 2;
	
	final static int ORDER_SHUFFLE = 0;
	final static int ORDER_RANDOM = 1;
	final static int ORDER_LOOP = 2;
	
	final static int VIEW_TINY = 0;
	final static int VIEW_NORMAL = 1;
	final static int VIEW_BIG = 2;
	final static int VIEW_BIBLICAL = 3;
	
	final static int FONT_TINY = 20;
	final static int FONT_NORMAL = 35;
	final static int FONT_BIG = 55;
	final static int FONT_BIBLICAL = 70;

}
