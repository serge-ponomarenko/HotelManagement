package ua.cc.spon.db.dao;

import ua.cc.spon.exception.DBException;
import ua.cc.spon.util.HotelHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public abstract class DAOFactory {
	
	private static DAOFactory instance;

	protected DAOFactory() {   }

	/**
	 * @return
	 * @throws DBException
	 */
	public static synchronized DAOFactory getInstance() throws DBException {
		if (instance == null) {
			
			try {
				String daoFQN = HotelHelper.getProperty("dao.factory.fqn");
				Class<?> c = Class.forName(daoFQN);
				Constructor<?> constr = c.getDeclaredConstructor();
				instance = (DAOFactory) constr.newInstance();
			} catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | InstantiationException |
					NoSuchMethodException e) {
				throw new DBException(e);
			}

		}
		return instance;
	}

	public abstract UserDAO getUserDAO();
	public abstract UserSettingsDAO getUserSettingsDAO();
	public abstract RoomDAO getRoomDAO();
	public abstract RoomCategoryDAO getRoomCategoryDAO();
	public abstract ReservationDAO getReservationDAO();
	public abstract StatusDAO getStatusDAO();
	public abstract RequestDAO getRequestDAO();
	public abstract LocaleDAO getLocaleDAO();


}
