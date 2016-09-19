package io.dynam.game.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.dynam.game.domain.Cosmetic;
import io.dynam.game.domain.Inventory;
import io.dynam.game.domain.Item;
import io.dynam.game.domain.MysteryBox;
import io.dynam.game.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class PersistenceManager {
	
	private static EntityManagerFactory _factory = null;
	
	public static EntityManagerFactory getFactory() {
		if (_factory == null) {
			_factory = Persistence.createEntityManagerFactory("io.dynam.game");
		}
		return _factory;
	}
	
	public static Cosmetic getCosmeticByName(String name) {
		EntityManager em = getFactory().createEntityManager();
		Cosmetic cosmetic = null;
		try {
			TypedQuery<Cosmetic> query = em.createQuery(
					"from Cosmetic c where c._name = :iname", Cosmetic.class)
					.setParameter("iname", name);
			cosmetic = query.getSingleResult();
		} catch (NoResultException e) {
			throw new WebApplicationException(404);
		}
		em.close();
		return cosmetic;
	}
	
	public static MysteryBox getMysteryBoxByName(String name) {
		EntityManager em = getFactory().createEntityManager();
		MysteryBox mysteryBox = null;
		try {
			TypedQuery<MysteryBox> query = em.createQuery(
					"from MysteryBox m where m._name = :iname", MysteryBox.class)
					.setParameter("iname", name);
			mysteryBox = query.getSingleResult();
		} catch (NoResultException e) {
			throw new WebApplicationException(404);
		}
		return mysteryBox;
	}
	
	public static List<Cosmetic> getMysteryBoxContents(String boxName) {
		EntityManager em = getFactory().createEntityManager();
		MysteryBox mysteryBox = getMysteryBoxByName(boxName);
		MysteryBox realMysteryBox = em.find(MysteryBox.class, mysteryBox.getId());
		Set<Cosmetic> inventSet = realMysteryBox.getContents();
		List<Cosmetic> invent = new ArrayList<Cosmetic>(inventSet);
		return invent;
	}
	
	public static Inventory getUserInventory(String userName) {
		EntityManager em = getFactory().createEntityManager();
		Inventory inventory = null;
		try { //inner join u._invent i
			TypedQuery<Integer> query = em.createQuery(
					"select u._id from User u where u._name = :uname", Integer.class)
					.setParameter("uname", userName);
			int id = query.getSingleResult();
			User u = em.find(User.class, id);
			inventory = u.getInventory();
		} catch (NoResultException e) {
			throw new WebApplicationException(404);
		}
		return inventory;
	}
	
	public static User getUserByName(String name) {
		EntityManager em = getFactory().createEntityManager();
		User user = null;
		try {
			TypedQuery<User> query = em.createQuery(
					"from User u where u._name = :uname", User.class)
					.setParameter("uname", name);
			user = query.getSingleResult();
		} catch (NoResultException e) {
			throw new WebApplicationException(404);
		}
		return user;
	}
}
