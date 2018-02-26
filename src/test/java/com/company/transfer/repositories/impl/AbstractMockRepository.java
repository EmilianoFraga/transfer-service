/**
 * 
 */
package com.company.transfer.repositories.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Emiliano
 *
 */
public abstract class AbstractMockRepository<T extends Object, S extends Serializable> implements CrudRepository<T, S> {
	
	protected LinkedHashMap<S, T> mapRepository;

	public AbstractMockRepository() {
		super();
		this.mapRepository = new LinkedHashMap<>();
	}

	@Override
	public long count() {
		return this.mapRepository.size();
	}

	@Override
	public void delete(S key) {
		this.mapRepository.remove(key);
	}

	@Override
	public void delete(T value) {
		for (S s : this.mapRepository.keySet()) {
			if (this.mapRepository.get(s).equals(value)) {
				this.mapRepository.remove(s);
				return;
			}
		}		
	}

	@Override
	public void delete(Iterable<? extends T> values) {
		for (T t : values) {
			this.delete(t);
		}
	}

	@Override
	public void deleteAll() {
		this.mapRepository.clear();
	}

	@Override
	public boolean exists(S key) {
		return this.mapRepository.containsKey(key);
	}

	@Override
	public Iterable<T> findAll() {
		return new ArrayList<>(this.mapRepository.values());
	}

	@Override
	public Iterable<T> findAll(Iterable<S> key) {
		return this.mapRepository.keySet().stream()
				.filter(key::equals)
				.map(this.mapRepository::get)
				.collect(Collectors.toList());
	}

	@Override
	public T findOne(S key) {
		return this.mapRepository.get(key);
	}

	@Override
	public <R extends T> R save(R value) {
		S key = this.getValueKey(value);
		
		if (key == null) {
			key = this.createNewKey();
			this.setValueID(value, key);
		}
		
		this.mapRepository.put(key, value);
		
		return value;
	}

	@Override
	public <R extends T> Iterable<R> save(Iterable<R> values) {
		for (R r : values) {
			this.save(r);
		}
		
		return values;
	}

	protected abstract <R extends T> R setValueID(R value, S key);

	protected abstract S createNewKey();

	protected abstract <R extends T> S getValueKey(R value);
}
