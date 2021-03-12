package de.daricari.thehaunted.util;

import java.util.function.Predicate;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class EntityPredicate implements Predicate<Entity>{

	private EntityType entityType;
	
	public EntityPredicate(EntityType e) {
		this.entityType = e;
	}
	
	@Override
	public boolean test(Entity t) {
		if(t.getType().equals(entityType))
			return true;
		return false;
	}

}
