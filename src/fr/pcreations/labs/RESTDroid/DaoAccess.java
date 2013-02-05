package fr.pcreations.labs.RESTDroid;

import java.sql.SQLException;
import java.util.List;

public interface DaoAccess<T extends ResourceRepresentation<?>> {

	abstract public void updateOrCreate(T resource)throws SQLException;
	abstract public <ID> T findById(ID resourceId) throws SQLException;
	abstract public List<T> queryForAll() throws SQLException;
	
}
