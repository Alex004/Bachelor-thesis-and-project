package com.example.myapplication.Data.Local;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myapplication.Data.Local.Code.CodeDao;
import com.example.myapplication.Data.Local.Code.CodeEntity;
import com.example.myapplication.Data.Local.Element.ElementDao;
import com.example.myapplication.Data.Local.Element.ElementEntity;
import com.example.myapplication.Data.Local.Element.ElementRegionDao;
import com.example.myapplication.Data.Local.Element.ElementRegionEntity;
import com.example.myapplication.Data.Local.Region.RegionDao;
import com.example.myapplication.Data.Local.Region.RegionEntity;
import com.example.myapplication.Data.Local.Route.RouteDao;
import com.example.myapplication.Data.Local.Route.RouteEntity;
import com.example.myapplication.Flows.DataConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(
        entities = {
                RouteEntity.class,
                CodeEntity.class,
                ElementEntity.class,
                ElementRegionEntity.class,
                RegionEntity.class
        },
        version = 3
)


@TypeConverters({DataConverter.class})
public abstract class Database extends RoomDatabase {

    public abstract RouteDao routeDao();
    public abstract CodeDao codeDao();
    public abstract ElementDao elementDao();
    public abstract ElementRegionDao elementRegionDao();
    public abstract RegionDao regionDao();

    private static volatile Database INSTANCE;

//    private static final int NUMBER_OF_THREADS = 4;
//
//    static final ExecutorService databaseWriteExecutor =
//            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

        }
    };

    public static Database getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class, "local_database")
                            .addMigrations(MIGRATION_1_2,MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
