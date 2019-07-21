import entities.Employee;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Scanner;

public class Engine implements Runnable {

    private final EntityManager entityManager;

    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void run(){
        this.containsEmployee();
    }


    /**
     * 3. Contains Employee
     * Use the soft_uni database.
     * Write a program that checks if a given employee name is contained in the database.
     */
    private void  containsEmployee(){
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();

        this.entityManager.getTransaction().begin();

        try {
            Employee employee = (Employee) this.entityManager
                    .createQuery("FROM Employee WHERE concat(first_name,' ', last_name) = :name", Employee.class)
                    .setParameter("name", name)
                    .getSingleResult();

            System.out.println("Yes");
        }catch (NoResultException nre){
            System.out.println("No");
        }

        this.entityManager.getTransaction().commit();
    }
}
