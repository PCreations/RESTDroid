package fr.pcreations.labs.RESTDroid.modules.defaultModule;

import java.sql.SQLException;

import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import fr.pcreations.labs.RESTDroid.core.DaoAccess;
import fr.pcreations.labs.RESTDroid.core.DaoFactory;
import fr.pcreations.labs.RESTDroid.core.ResourceRepresentation;
import fr.pcreations.labs.RESTDroid.core.RestService;
import fr.pcreations.labs.RESTDroid.exceptions.DatabaseManagerNotInitializedException;

public class ORMLiteDaoFactory extends DaoFactory {

	private OrmLiteSqliteOpenHelper mHelper;
    
    public ORMLiteDaoFactory() {
            try {
                    mHelper = DatabaseManager.getInstance().getHelper();
            } catch (DatabaseManagerNotInitializedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <D extends DaoAccess<T>, T extends ResourceRepresentation<?>> D getDao(
                    Class<T> clazz) {
    	Log.i(RestService.TAG, "getDao OF : " + clazz.getSimpleName());
		D dao;
		
		if(mDaos.containsKey(clazz)) {
			return (D) mDaos.get(clazz);
		}
		
		try {
			dao = (D) mHelper.getDao(clazz);
			mDaos.put(clazz, dao);
			return dao;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
    }
	
}
