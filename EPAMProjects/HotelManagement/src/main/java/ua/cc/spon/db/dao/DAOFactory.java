package ua.cc.spon.db.dao;

import ua.cc.spon.util.HotelHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class DAOFactory {
	
	private static DAOFactory instance;

	public static synchronized DAOFactory getInstance() {
		if (instance == null) {
			
			try {
				String daoFQN = HotelHelper.getProperty("dao.factory.fqn");
				Class<?> c = Class.forName(daoFQN);
				Constructor<?> constr = c.getDeclaredConstructor();
				instance = (DAOFactory) constr.newInstance();
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);        // TODO: 23.08.2022
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}

		}
		return instance;
	}


	protected DAOFactory() {}

	public abstract UserDAO getUserDAO();
	public abstract UserSettingsDAO getUserSettingsDAO();
	public abstract RoomDAO getRoomDAO();
	public abstract RoomCategoryDAO getRoomCategoryDAO();


}
