package com.walker.socket.server.chat.plugin.aop;

import java.util.HashMap;
import java.util.Map;

public class ModelGroup{
	Map<String, Model> modelMap = new HashMap<>();

	public Map<String, Model> getModelMap() {
		return modelMap;
	}

	public ModelGroup setModelMap(Map<String, Model> modelMap) {
		this.modelMap = modelMap;
		return this;
	}

	public ModelGroup incr(String type, int count, long cost) {
		Model model = modelMap.get(type);
		if(model == null){
			model = new Model();
			this.modelMap.put(type, model);
		}
		model.incr(count, cost);
		return this;
	}
}
