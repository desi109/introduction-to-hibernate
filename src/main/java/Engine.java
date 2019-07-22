import entities.Address;
import entities.Employee;
import entities.Project;
import entities.Town;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Scanner;

public class Engine implements Runnable {

    private final EntityManager entityManager;

    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void run() {
        this.commandRun();
    }


    private void commandRun() {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("Work with database soft_uni.%n" +
                "Commands which can be used:%n" +
                "- Check for employee /then write employee name/.%n" +
                "- Add address to employee /first write town name, write new address then write employee's name whose address you want to be updated/.%n" +
                "- Find (this command finds 10 addresses, ordered by the number of employees).%n" +
                "- Find projects(this command finds the last 10 started projects, prints their name, description, start and end date and sort them by name lexicographically).%n");

        String command = scanner.nextLine();

        switch (command.toLowerCase()) {
            case "check for employee":
                checkForEmployee();
                break;
            case "add address to employee":
                addAddressAndUpdateEmployee();
                break;
            case "find":
                addressesWithEmployeeCount();
                break;
            case "find projects":
                findLatest10Projects();
                break;
        }
    }

    /**
     * 3. Contains Employee
     * Use the soft_uni database.
     * Write a program that checks if a given employee name is contained in the database.
     * Write command &quot;check for employee&quot; to find if the name exists in database soft_uni.
     */
    private void checkForEmployee() {
        Scanner scanner = new Scanner(System.in);
        String nameInput = scanner.nextLine();

        this.entityManager.getTransaction().begin();

        try {
            Employee employee = (Employee) this.entityManager
                    .createQuery("FROM Employee WHERE concat(first_name,' ', last_name) = :name", Employee.class)
                    .setParameter("name", nameInput)
                    .getSingleResult();

            System.out.println("Yes, this name exists in database.");
        } catch (NoResultException nre) {
            System.out.println("No, this name does not exist in database.");
        }

        this.entityManager.getTransaction().commit();
    }


    /**
     * 6. Adding a New Address and Updating Employee
     * Create a new address.
     * Set that address to an employee with a last name, given as an input.
     * Write command &quot;add address to employee&quot; to create  a new address in database soft_uni.
     */
    private void addAddressAndUpdateEmployee() {
        Scanner scanner = new Scanner(System.in);

        String townInput = scanner.nextLine();
        String addressInput = scanner.nextLine();
        String nameInput = scanner.nextLine();

        this.entityManager.getTransaction().begin();

        Address address = new Address();
        address.setText(addressInput);


        Town town = (Town) this.entityManager
                .createQuery("FROM Town WHERE name = :name", Town.class)
                .setParameter("name", townInput)
                .getSingleResult();
        address.setTown(town);

        this.entityManager.persist(address);


        Employee employee = this.entityManager
                .createQuery("FROM Employee WHERE concat(first_name,' ', last_name) = :name", Employee.class)
                .setParameter("name", nameInput)
                .getSingleResult();

        this.entityManager.detach(employee.getAddress());
        employee.setAddress(address);
        this.entityManager.merge(employee);

        this.entityManager.getTransaction().commit();

        System.out.println("Address was changed.");
    }


    /**
     * 7. Addresses with Employee Count
     * Find all addresses, ordered by the number of employees who live there (descending), then by town id (ascending).
     * Take only the first 10 addresses and print their address text, town name and employee count.
     * Write command &quot;find&quot;.
     */
    private void addressesWithEmployeeCount() {
        this.entityManager.getTransaction().begin();

        List<Address> employees = this.entityManager
                .createQuery("FROM Address ORDER BY size(employees) DESC, town.id", Address.class)
                .setMaxResults(10)
                .getResultList();

        employees.forEach(e -> System.out.printf("%s, %s - %s employees",
                e.getText(), e.getTown().getName(), e.getEmployees()
                        .size())
                .println());

        this.entityManager.getTransaction().commit();

    }

    /**
     * 9. Find Latest 10 Projects
     * Write a program that prints the last 10 started projects. Print their name, description, start and end date and sort
     * them by name lexicographically. For the output, check the format from the example.
     * Write command &quot;find projects&quot;.
     */
    private void findLatest10Projects(){

        this.entityManager.getTransaction().begin();

        List<Project> projects = this.entityManager
                .createQuery("FROM Project ORDER BY startDate DESC, name", Project.class)
                .setMaxResults(10)
                .getResultList();

        projects.forEach(p -> System.out.printf("Project name: %s%n" +
                "Project Description: %s%n" +
                "Project Start Date: %s%n" +
                "Project End Date: %s%n%n",
                p.getName(), p.getDescription(), p.getStartDate(), p.getEndDate()));

        this.entityManager.getTransaction().commit();
    }

}
