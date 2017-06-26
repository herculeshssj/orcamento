package br.com.hslife.orcamento.rest;

import java.util.List;

public class AbstractRest<T> {

	protected List<T> listEntity;

	public List<T> getListEntity() {
		return listEntity;
	}

	public void setListEntity(List<T> listEntity) {
		this.listEntity = listEntity;
	}
}