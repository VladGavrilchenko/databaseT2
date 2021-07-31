package menu;

import javax.persistence.*;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class App {
    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {


            emf = Persistence.createEntityManagerFactory("JPAMenu");
            em = emf.createEntityManager();
            try {


                while (true) {
                    System.out.println("1: add dishes");
                    System.out.println("2: view all");
                    System.out.println("3: view cost from-to");
                    System.out.println("4: view dishes with discounts");
                    System.out.println("5: add random dishes");
                    System.out.println("6: set a discount dishes");
                    System.out.println("7: remove a discount dishes");
                    System.out.println("8: make order");
                    System.out.println("9: exit");


                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addDishes(sc);
                            break;
                        case "2":
                            viewDishes();
                            break;
                        case "3":
                            viewDishesForPrice(sc);
                            break;
                        case "4":
                            viewDishesWithDiscounts();
                        case "5":
                            insertRandomDishes(sc);
                            break;
                        case "6":
                            setADiscountDishes(sc);
                            break;

                        case "7":
                            removeADiscountDishes(sc);
                            break;

                        case "8":
                            makeOrder(sc);
                            break;

                        case "9":
                            return;

                        default:
                            System.out.println("Invalid command");

                    }

                }

            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }


    private static void addDishes(Scanner sc) {
        System.out.println("Enter dishes name:");
        String name = sc.nextLine();
        System.out.print("Enter dishes weight: ");
        String sWeight = sc.nextLine();
        System.out.print("Enter dishes price: ");
        String sPrice = sc.nextLine();

        Double price = Double.parseDouble(sPrice);
        Double weight = Double.parseDouble(sWeight);
        boolean isSell = false;

        while (true) {
            System.out.print("Enter is in sell(y = true , n = false: ");
            String sIsSell = sc.nextLine();


            if (sIsSell.equals("y") || sIsSell.equals("Y")) {
                isSell = true;
                break;
            }

            if (sIsSell.equals("n") || sIsSell.equals("N")) {
                isSell = false;
                break;
            }
        }


        em.getTransaction().begin();
        try {
            Dishes c = new Dishes(name, price, weight, isSell);
            em.persist(c);
            em.getTransaction().commit();

            System.out.println(c.getId());
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void viewDishes() {
        Query query = em.createQuery(
                "SELECT d FROM Dishes d", Dishes.class);
        List<Dishes> list = (List<Dishes>) query.getResultList();

        for (Dishes d : list)
            System.out.println(d);
    }


    private static void viewDishesWithDiscounts() {
        Query query = em.createQuery(
                "SELECT d FROM Dishes  d WHERE d.discount != 0 ", Dishes.class);
        List<Dishes> list = (List<Dishes>) query.getResultList();

        for (Dishes d : list)
            System.out.println(d);
    }

    private static void viewDishesForPrice(Scanner sc) {
        System.out.println("Choice from minimum to maximum(m) or maximum minimum (mx)");

        String s = sc.nextLine();

        if (s.equals("mx")) {
            Query query = em.createQuery(
                    "SELECT d FROM Dishes d ORDER BY d.price DESC", Dishes.class);
            List<Dishes> list = (List<Dishes>) query.getResultList();

            for (Dishes d : list)
                System.out.println(d);

            return;
        }

        if (s.equals("m")) {
            Query query = em.createQuery(
                    "SELECT d FROM Dishes d ORDER BY d.price ASC", Dishes.class);
            List<Dishes> list = (List<Dishes>) query.getResultList();

            for (Dishes d : list)
                System.out.println(d);
            return;
        }
    }

    private static void insertRandomDishes(Scanner sc) {
        Random rn = new Random();
        System.out.print("Enter dishes count: ");
        String sCount = sc.nextLine();
        int count = Integer.parseInt(sCount);

        em.getTransaction().begin();
        try {
            for (int i = 0; i < count; i++) {
                Dishes d = new Dishes("Name" + i, Math.pow(0.5 + rn.nextDouble() * 50, 1),
                        Math.pow(0.100 + rn.nextDouble() * 0.500, 1), rn.nextBoolean());
                em.persist(d);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void setADiscountDishes(Scanner sc) {
        System.out.print("Enter id: ");
        String sId = sc.nextLine();

        System.out.print("Enter discount : ");
        String sDiscount = sc.nextLine();

        long id = Long.parseLong(sId);
        int discount = Integer.parseInt(sDiscount);

        Dishes d = null;
        try {
            Query query = em.createQuery(
                    "SELECT d FROM Dishes d WHERE d.id = :id and d.discount = 0", Dishes.class);
            query.setParameter("id", id);
            d = (Dishes) query.getSingleResult();
        } catch (NoResultException ex) {
            System.out.println("Dishes not found!");
            return;
        } catch (NonUniqueResultException ex) {
            System.out.println("Non unique result!");
            return;
        }

        em.getTransaction().begin();
        try {
            d.setADiscountToPrice(discount);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void removeADiscountDishes(Scanner sc) {
        System.out.print("Enter id: ");
        String sId = sc.nextLine();



        long id = Long.parseLong(sId);


        Dishes d = null;
        try {
            Query query = em.createQuery(
                    "SELECT d FROM Dishes d WHERE d.id = :id and d.discount != 0", Dishes.class);
            query.setParameter("id", id);
            d = (Dishes) query.getSingleResult();
        } catch (NoResultException ex) {
            System.out.println("Dishes not found!");
            return;
        } catch (NonUniqueResultException ex) {
            System.out.println("Non unique result!");
            return;
        }

        em.getTransaction().begin();
        try {
            d.removeADiscountToPrice();
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }
    private static void makeOrder(Scanner sc) {

        Double coutOfWeight = 0.0;
        Double coutToPay = 0.0;
        String allNameInOrder = "";
        boolean isDondOrederEnd= true;
        boolean select = true;


        while (isDondOrederEnd) {

            while (select) {

                System.out.println("1: view all");
                System.out.println("2: view cost from-to");
                System.out.println("3: view dishes with discounts");


                String s = sc.nextLine();
                switch (s) {
                    case "1":
                        viewDishes();
                        select = false;
                        break;
                    case "2":
                        viewDishesForPrice(sc);
                        select = false;
                        break;
                    case "3":
                        viewDishesWithDiscounts();
                        select = false;
                        break;
                    default:
                        System.out.println("Invalid command");
                }
            }

            System.out.println(" Enter id for add a dish to the order:");
            String sS = sc.nextLine();
            long s = Long.parseLong(sS);



            try {
                Query query = em.createQuery(
                        "SELECT d FROM Dishes d WHERE d.id = :s and d.isSell =true ", Dishes.class);
                query.setParameter("s", s);
                List<Dishes> list = (List<Dishes>) query.getResultList();

                if ((coutOfWeight + list.get(0).getWeight()) <= 1) {
                    coutOfWeight += list.get(0).getWeight();
                    coutToPay += list.get(0).getPrice();
                    allNameInOrder += list.get(0).getNameDishes();
                } else {
                    System.out.println("Your order more for 1 kg!");
                }
            }catch (NoResultException ex) {
                System.out.println("Dishes not found!");
                return;
            }



            System.out.println("You order: " + allNameInOrder + " price of all " + coutToPay +
                    " a  wight: "  + coutOfWeight);

            System.out.println("Its all( if yes tap enter): ");
            String exit = sc.nextLine();

            if (exit.equals(""))
                return;


        }


    }
}


