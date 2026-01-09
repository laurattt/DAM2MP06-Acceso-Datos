package com.xmlEjer0;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Set;

public class Manager {
    private static SessionFactory sessionFactory;

    public static void createSessionFactory() {
        try {
            // carga hibernate.cfg.xml por defecto
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Error al crear SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    // CREATE
    @SuppressWarnings("deprecation")
    public static Ciutat addCiutat(String nom, String pais, int poblacio) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Ciutat ciutat = new Ciutat(nom, pais, poblacio);
        try {
            tx = session.beginTransaction();
            session.save(ciutat);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ciutat;
    }

    @SuppressWarnings("deprecation")
    public static Ciutada addCiutada(String nom, String cognom, int edat) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Ciutada ciutada = new Ciutada(nom, cognom, edat);
        try {
            tx = session.beginTransaction();
            session.save(ciutada);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ciutada;
    }

    // UPDATE CIUDAAAD (y sus ciudadanos)
    @SuppressWarnings("deprecation")
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
                
                // Actualizamos la lista de ciudadanos
                if (nuevosCiutadans != null) {
                    ciutat.setCiutadans(nuevosCiutadans);
                }
                session.update(ciutat);
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
    @SuppressWarnings("deprecation")
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
                session.update(ciutada);
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
    @SuppressWarnings("deprecation")
    public static void delete(Class<?> type, long id) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Object object = session.get(type, id);
            if (object != null) {
                session.delete(object);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // READ 
    @SuppressWarnings("deprecation")
    public static List<?> listCollection(Class<?> type, String condition) {
        Session session = sessionFactory.openSession();
        List<?> list = null;
        try {
            String hql = "FROM " + type.getSimpleName() + " " + condition;
            Query<?> query = session.createQuery(hql);
            list = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return list;
    }

    // Helper para imprimir listas (usado en Main)
    public static String collectionToString(Class<?> type, List<?> collection) {
        StringBuilder sb = new StringBuilder();
        sb.append("Llista de ").append(type.getSimpleName()).append(":\n");
        for (Object obj : collection) {
            sb.append(obj.toString()).append("\n");
        }
        return sb.toString();
    }

    // READ Específico con Fetch de la colección
    public static Ciutat getCiutatWithCiutadans(long id) {
        Session session = sessionFactory.openSession();
        Ciutat ciutat = null;
        try {
            ciutat = session.get(Ciutat.class, id);
            if (ciutat != null) {
                // Inicializamos la colección perezosa (lazy) accediendo a ella dentro de la sesión
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