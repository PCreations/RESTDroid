package fr.pcreations.labs.RESTDroid.modules.defaultModule;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import fr.pcreations.labs.RESTDroid.exceptions.DatabaseManagerNotInitializedException;

public class DatabaseManager implements helperGetter{
	
	static private DatabaseManager instance;
	private Class<?> databaseHelperClass;
	private OrmLiteSqliteOpenHelper helper;
	 
    static public <H extends OrmLiteSqliteOpenHelper> void init(Context context, Class<H> databaseHelper) {
            if(null==instance) {
                    instance = new DatabaseManager(context, databaseHelper);
            }
    }

    static public DatabaseManager getInstance() {
            return instance;
    }

    @SuppressWarnings("unchecked")
	private <H extends OrmLiteSqliteOpenHelper> DatabaseManager(Context context, Class<H> databaseHelper) {
		Class<H> _tempClass;
		try {
			_tempClass = (Class<H>) Class.forName(databaseHelper.getName());
			Constructor<H> ctor = _tempClass.getDeclaredConstructor(Context.class);  
	    	helper = ctor.newInstance(context);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public OrmLiteSqliteOpenHelper getHelper() throws DatabaseManagerNotInitializedException {
            if(null == instance)
                    throw new DatabaseManagerNotInitializedException();
            return helper;
    }
	
}
