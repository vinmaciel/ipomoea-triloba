package br.usp.pcs.securetcg.server;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class GenericDAO<T> {

	private static final SessionFactory sessionFactory = buildSessionFactory();
	
	private static SessionFactory buildSessionFactory() {
		try {
			Configuration configuration = new Configuration().configure();
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			return configuration.buildSessionFactory(serviceRegistry);
		}
		catch(Throwable t) {
			System.err.println("Initial SessionFactory creation failed. " + t);
			throw new ExceptionInInitializerError(t);
		}
	}
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	
	public static <T> void add(T obj) {
		Transaction transaction = null;
		Session session = getSessionFactory().openSession();
		
		try {
			transaction = session.beginTransaction();
			session.save(obj);
			transaction.commit();
		}
		catch(RuntimeException e) {
			if(transaction != null)
				transaction.rollback();
			e.printStackTrace();
		}
		finally {
			session.flush();
			session.close();
		}
	}
	
}
