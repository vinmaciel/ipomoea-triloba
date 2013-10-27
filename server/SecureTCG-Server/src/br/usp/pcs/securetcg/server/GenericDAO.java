package br.usp.pcs.securetcg.server;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class GenericDAO<T> {

	private Class<T> entityClass;
	
	public GenericDAO(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
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
	
	
	public long add(T obj) {
		Transaction transaction = null;
		Session session = getSessionFactory().openSession();
		long result = -1;
		
		try {
			transaction = session.beginTransaction();
			result = Long.parseLong(session.save(obj).toString());
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
		
		return result;
	}
	
	public T get(long id) {
		Transaction transaction = null;
		Session session = getSessionFactory().openSession();
		T result = null;
		
		try {
			transaction = session.beginTransaction();
			Query query = session.createQuery("from " + entityClass.getSimpleName() + " as o where o.id = :id").setLong("id", id);
			@SuppressWarnings("unchecked")
			List<T> list = query.list();
			if(list != null && !list.isEmpty())
				result = list.get(0);
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
		
		return result;
	}
	
}
