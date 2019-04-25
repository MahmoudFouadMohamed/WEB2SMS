/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import com.edafa.web2sms.dalayer.exception.DBException;

/**
 * 
 * @author yyaseen
 */
public abstract class AbstractDao<T> {

	private Class<T> entityClass;

	public AbstractDao(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	protected abstract EntityManager getEntityManager();

	public void create(T entity) throws DBException {
		try {
			getEntityManager().persist(entity);
		} catch (Exception e) {
			throw new DBException(e);

		}
	}

	public void edit(T entity) throws DBException {
		try {
			getEntityManager().merge(entity);
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	public void remove(T entity) throws DBException {
		try {
			getEntityManager().remove(getEntityManager().merge(entity));
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	public T find(Object id) throws DBException {
		try {
			return getEntityManager().find(entityClass, id);
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	public T refresh(T entity) throws DBException {
		try {
			getEntityManager().refresh(entity);
			return entity;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	public List<T> findAll() throws DBException {
		try {
			javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
			cq.select(cq.from(entityClass));
			return getEntityManager().createQuery(cq).getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	public List<T> findRange(int[] range) throws DBException {
		try {
			javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
			cq.select(cq.from(entityClass));
			javax.persistence.Query q = getEntityManager().createQuery(cq);
			q.setMaxResults(range[1] - range[0]);
			q.setFirstResult(range[0]);
			return q.getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	public List<T> findRange(int frist, int max, String order) throws DBException {
		javax.persistence.criteria.CriteriaQuery criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery();
		Root root = criteriaQuery.from(entityClass);
		criteriaQuery.select(root);
		criteriaQuery.orderBy(getEntityManager().getCriteriaBuilder().asc(root.get(order)));
		javax.persistence.Query query = getEntityManager().createQuery(criteriaQuery);
		query.setMaxResults(max);
		query.setFirstResult(frist);
		return query.getResultList();
	}

	public List<T> findRange(int frist, int max, String order, List<T> list, String condition) throws DBException {
		javax.persistence.criteria.CriteriaQuery criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery();
		Root root = criteriaQuery.from(entityClass);
		criteriaQuery.select(root);
		Expression<String> exp = root.get(condition);
		Predicate predicate = exp.in(list);
		criteriaQuery.where(predicate);
		criteriaQuery.orderBy(getEntityManager().getCriteriaBuilder().asc(root.get(order)));
		javax.persistence.Query query = getEntityManager().createQuery(criteriaQuery);
		query.setMaxResults(max);
		query.setFirstResult(frist);
		return query.getResultList();
	}

	public int count() throws DBException {
		try {
			javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
			javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
			cq.select(getEntityManager().getCriteriaBuilder().count(rt));
			javax.persistence.Query q = getEntityManager().createQuery(cq);
			return ((Long) q.getSingleResult()).intValue();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	public int count(Object id) throws DBException {
		try {
			return ((Long) getEntityManager().createQuery("SELECT COUNT(a) FROM " + entityClass.getSimpleName()
					+ " a WHERE a." + getIdProperty(entityClass) + " = :id").setParameter("id", id).getSingleResult())
							.intValue();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	public String getIdProperty(Class<T> entityClass) {
		String idProperty = null;
		Metamodel metamodel = getEntityManager().getMetamodel();
		EntityType entity = metamodel.entity(entityClass);
		Set<SingularAttribute> singularAttributes = entity.getSingularAttributes();
		for (SingularAttribute singularAttribute : singularAttributes) {
			if (singularAttribute.isId()) {
				idProperty = singularAttribute.getName();
				break;
			}
		}
		if (idProperty == null)
			throw new RuntimeException("id field not found");
		return idProperty;
	}

}
