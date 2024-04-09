package com.redhat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.microprofile.config.ConfigProvider;

import com.redhat.ConfigurationModel.Variable;

public class ContextMap implements Map<String, Object> {

    // 2 scopes: Global, local
    private Map<String, Object> local = new HashMap<>();
    private Map<String, Object> global = new HashMap<>();

    // ContextMap specific methods

    public void initializeGlobalVariables(List<Variable> vars) {
        for (Variable var : vars) {
            global.put(var.name,
                ConfigProvider.getConfig().getOptionalValue(var.name,String.class)
                .orElse(var.value));
            
        }
    }

    public void initializeLocalVariables(List<Variable> vars) {
        local = new HashMap<>();
        if (vars != null) {
            for (Variable var : vars) {
                local.put(var.name,
                ConfigProvider.getConfig().getOptionalValue(var.name,String.class)
                .orElse(var.value));
            }
        }
    }

    // Implementation of the Map<> interface

    @Override
    public int size() {
        return local.size() + global.size();
    }

    @Override
    public boolean isEmpty() {
        return local.isEmpty() && global.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return local.containsKey(key) || global.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return local.containsValue(value) || global.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        if(local.containsKey(key)) {
            return local.get(key);
        } else {
            return global.get(key);
        }
    }

    @Override
    public Object put(String key, Object value) {
        Object res = local.put(key, value);
        return res != null? res : global.get(key);
    }

    @Override
    public Object remove(Object key) {
        return local.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        local.putAll(m);
    }

    @Override
    public void clear() {
        local.clear();
    }

    @Override
    public Set<String> keySet() {
        Set<String> res = new HashSet<>();
        res.addAll(global.keySet());
        res.addAll(local.keySet());

        return res;
    }

    @Override
    public Collection<Object> values() {
        Collection<Object> res = new ArrayList<>();
        res.addAll(global.values());
        res.addAll(local.values());

        return res;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String,Object>> res = new HashSet<>();
        res.addAll(global.entrySet());
        res.addAll(local.entrySet());
        return res;
    }
}
