package fr.pcreations.labs.RESTDroid.modules.ORMliteJacksonModule;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import fr.pcreations.labs.RESTDroid.exceptions.DatabaseManagerNotInitializedException;

public interface helperGetter {

	abstract OrmLiteSqliteOpenHelper getHelper() throws DatabaseManagerNotInitializedException;
	
}
