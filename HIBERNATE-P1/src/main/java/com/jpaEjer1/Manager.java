package com.jpaEjer1;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class Manager {
    private static SessionFactory sessionFactory;

    // distinta modo de config que ejer 0 zzzz
    public static void createSessionFactory() {
        try {
            // config vacia
            Configuration configuration = new Configuration();
            
            // cargar manualmente (recuerda para evitar prob)
            Properties props = new Properties();
            props.load(Manager.class.getClassLoader().getResourceAsStream("hibernate.properties"));
            configuration.setProperties(props);

            // add class ejer1
            configuration.addAnnotatedClass(Ciutat.class); 
            configuration.addAnnotatedClass(Ciutada.class);

            sessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Error al crear SessionFactory (JPA): " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    // CREATE
    public static Ciutat addCiutat(String nom, String pais, int poblacio) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Ciutat ciutat = new Ciutat(nom, pais, poblacio);
        try {
            tx = session.beginTransaction();
            session.persist(ciutat); // Usamos persist, la convención JPA
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ciutat;
    }

    public static Ciutada addCiutada(String nom, String cognom, int edat) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Ciutada ciutada = new Ciutada(nom, cognom, edat);
        try {
            tx = session.beginTransaction();
            session.persist(ciutada); // Usamos persist
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ciutada;
    }

    // UPDATE CIUTAT
    public static void updateCiutat(long id, String nom, String pais, int poblacio, Set<Ciutada> nuevosCiutadans) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Ciutat ciutat = session.get(Ciutat.class, id);
            if (ciutat != null) {
                ciutat.setNom(nom);
                ciutat.setPais(pais);
                ciutat.setPoblacio(poblacio);
                
                // actualizar lista
                if (nuevosCiutadans != null) {
                    ciutat.setCiutadans(nuevosCiutadans);
                }
                session.merge(ciutat);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // UPDATE CIUTADA
    public static void updateCiutada(long id, String nom, String cognom, int edat) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Ciutada ciutada = session.get(Ciutada.class, id);
            if (ciutada != null) {
                ciutada.setNom(nom);
                ciutada.setCognom(cognom);
                ciutada.setEdat(edat);
                session.merge(ciutada);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // DELETE
    public static void delete(Class<?> type, long id) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Object object = session.get(type, id); // tipo clase que sera elimianda
            if (object != null) {
                session.remove(object); 
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // READ - LIST --> hql
    public static List<?> listCollection(Class<?> type, String condition) {
        Session session = sessionFactory.openSession();
        List<?> list = null;
        try {
        
            String entityName = sessionFactory.getMetamodel().entity(type).getName(); 
            
            String hql = "FROM " + entityName + " " + condition;
            
            Query<?> query = session.createQuery(hql, type); 
            list = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return list;
    }

    public static String collectionToString(Class<?> type, List<?> collection) {
        StringBuilder sb = new StringBuilder();
        sb.append("Llista de ").append(type.getSimpleName()).append(":\n");
        for (Object obj : collection) {
            sb.append(obj.toString()).append("\n");
        }
        return sb.toString();
    }

    public static Ciutat getCiutatWithCiutadans(long id) {
        Session session = sessionFactory.openSession();
        Ciutat ciutat = null;
        try {
            ciutat = session.get(Ciutat.class, id);
            if (ciutat != null) {
                ciutat.getCiutadans().size(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ciutat;
    }
}