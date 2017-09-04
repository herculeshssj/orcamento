package br.com.hslife.orcamento.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.hslife.orcamento.util.Util;

public class AbstractRest<T> {

	protected List<T> listEntity;
	
	protected T entity;

	protected String successMessage() {
		return responseMessage("Registro alterado com sucesso!");
	}
	
	protected String responseMessage(String message) {
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put("message", message);
		return Util.gerarJson(messageMap);
	}
	
	public List<T> getListEntity() {
		return listEntity;
	}

	public void setListEntity(List<T> listEntity) {
		this.listEntity = listEntity;
	}
}