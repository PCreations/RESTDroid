package fr.pcreations.labs.RESTDroid.modules.ORMliteJacksonModule;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import fr.pcreations.labs.RESTDroid.core.DaoFactory;
import fr.pcreations.labs.RESTDroid.core.Module;
import fr.pcreations.labs.RESTDroid.core.ParserFactory;
import fr.pcreations.labs.RESTDroid.core.Processor;

public class ORMliteJacksonModule extends Module {
	
	private OrmLiteSqliteOpenHelper mDatabaseHelper;
	
	public ORMliteJacksonModule(OrmLiteSqliteOpenHelper helper) {
		mDatabaseHelper = helper;
	}
	
	@Override
	public Processor setProcessor() {
		return new ORMliteJacksonProcessor();
	}

	@Override
	public ParserFactory setParserFactory() {
		return new SimpleJacksonParserFactory();
	}

	@Override
	public DaoFactory setDaoFactory() {
		return new ORMLiteDaoFactory(mDatabaseHelper);
	}

}
