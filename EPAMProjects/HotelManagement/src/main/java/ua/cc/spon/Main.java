package ua.cc.spon;

import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.exception.IllegalPasswordException;
import ua.cc.spon.exception.NoUserFoundException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;

public class Main {

    public static void main(String[] args) throws IllegalPasswordException, NoUserFoundException, UserIsAlreadyRegisteredException {
        DAOFactory factory = DAOFactory.getInstance();
        RoomDAO roomDAO = factory.getRoomDAO();
       /*       User user = new User();

        user.setEmail("s.stefaniv@gmail.com");
        user.setFirstName("Sergiy");
        user.setLastName("Ponomarenko");
        user.setHashPassword("1234");
        user.setRole(User.Role.ADMINISTRATOR);
*/
//        userDAO.insert(user);
//
//        System.out.println(user.getId());

//        System.out.println(userDAO.findByEmailAndPassword("s.stefaniv@gmail.com", "111"));

        System.out.println(roomDAO.findALL("uk"));


    }

}


