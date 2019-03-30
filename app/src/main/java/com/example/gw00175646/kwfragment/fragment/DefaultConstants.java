package com.example.gw00175646.kwfragment.fragment;

public class DefaultConstants {
    public static final int UNDEFINED = -1;

    public static final int MAX_PROGRESS = 1000;

    /** The low battery check base value that is 1% (2013.6.19) */
    public static final int LOW_BATTERY_THRESHOLD = 1;

    /** A distance between count and duration is 12dp. */
    public static final String LIST_ITEM_COUNT_DURATION_DISTANCE = "   ";

    public interface Extra {

        String REORDER_TYPE = "reorder_type";

        String LIST_TYPE = "extra_list_type";

        String KEY_WORD = "extra_key_word";

        String TITLE = "extra_title";

        String GROUP_TYPE = "extra_group_type";

        String ARTIST = "extra_artist";

        String CHECKED_ITEM_IDS = "extra_checked_item_ids";

        String CHECKED_ITEM_CP_ATTRS = "extra_checked_item_cp_attrs";

        String URI_STRING = "extra_uri_string";

        String AUDIO_ID = "extra_audio_id";

        String LIST_IDS = "extra_list_ids";

        String LIST_POSITION = "extra_list_position";

        /**
         * To prevent share music files. If we allow to share the music file, it
         * can makes legal problems.
         */
        String FORWARD_LOCK = "FORWARD_LOCK";

        String DEVICE_NAME = "device_name";

        String IS_ENABLE_TRANSITION = "is_enable_transition";

        String LAUNCH_SEARCH_ENABLE = "launch_search_enable";

        String MOVE_RADIO_TAB = "move_radio_tab";

        String ONLINE_ITEMS_ALBUM_ENBLED = "ONLINE_ITEMS_ALBUM_ENBLED";
    }

    public interface RequestCode {
        int PICK_TRACK = 0;
    }

    public interface BundleArgs {
        String KEY_WORD = "args_key_word";

        String TITLE = "args_title";

        String CHECKED_ITEM_IDS = "args_checked_item_ids";

        String CHECKED_ITEM_POSITIONS = "args_checked_item_positions";

        String CHECKED_ITEM_COUNT = "args_checked_item_count";

        String CHECKED_ITEM_CP_ATTRS = "item_cp_attrs";

        String TAG = "args_tag";

        String INITIAL_VIEW_TYPE = "args_initial_view_type";

        String FINISH_ACTIVITY = "args_finish_activity";

        String LAUNCH_TRACK_ACTIVITY = "args_launch_track_activity";

        String LAUNCH_PICKER_ACTIVITY = "args_launch_picker_activity";

        String PLAYLIST_ID = "playlist_id";

        String REORDER_ICON_POSITION = "reorder_icon_position";

        String SEARCH_TEXT = "search_text";

        String SEARCH_TYPE = "search_type";

        String DURATION = "duration";

        String LIST_TYPE = "list_type";

        String URI = "uri";

        String AUDIO_ID = "audio_id";

        String LIST_ITEMS = "list_items";

        String PATH = "path";

        String MOVE_TO_PRIVATE = "move_to_private";

        String IS_FOLDER = "is_folder";

        String IS_PLAYING = "is_playing";

        String LAUNCH_FULL_PLAYER = "launch_full_player";

        String TV_LIST = "args_tv_list";

        String REQUIRED_PERMISSIONS = "required_permissions";

        String NOW_PLAYLING_LIST_QUEUE_TYPE = "now_playing_list_mode";

        String FORCE_UPDATE = "force_update";

        String APP_NAME = "app_name";

        String ADD_TO_QUEUE = "add_to_queue";

        String ADD_TO_FAVOURITES = "add_to_favourites";

        String REQUEST_CODE = "reqeust_code";

        String WHITE_THEME = "white_theme";

        String SELECTION_ARGS = "selectionArgs";
    }
}
