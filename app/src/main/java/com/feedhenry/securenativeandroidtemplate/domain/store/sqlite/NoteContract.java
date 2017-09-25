package com.feedhenry.securenativeandroidtemplate.domain.store.sqlite;

import android.provider.BaseColumns;

/**
 * Created by weili on 25/09/2017.
 */

public class NoteContract {

    private NoteContract() {

    }

    public static class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "note";

        public static final String COLUMN_CREATED_AT = "COLUMN_CREATED_AT";
        public static final String COLUMN_UUID = "uuid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
    }
}
