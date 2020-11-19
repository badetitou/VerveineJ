// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famix;

import ch.akuhn.fame.internal.MultivalueSet;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import java.util.stream.Collectors;

import ch.akuhn.fame.FamePackage;
import eu.synectique.verveine.core.gen.famix.Type;
import org.moosetechnology.model.famix.famixtraits.TParameterizedType;
import org.moosetechnology.model.famix.famixtraits.TWithParameterizedTypes;


@FamePackage("FAMIX")
@FameDescription("ParameterizableClass")
public class ParameterizableClass extends Class implements TWithParameterizedTypes {

    private Collection<TParameterizedType> parameterizedTypes; 



    @FameProperty(name = "parameters", derived = true)
    public Collection<ParameterType> getParameters() {
        return this.getTypes().stream().filter(tType -> tType instanceof ParameterType)
                .map(tType -> (ParameterType) tType).collect(Collectors.toList());
    }
        
    @FameProperty(name = "parameterizedTypes", opposite = "parameterizableClass", derived = true)
    public Collection<TParameterizedType> getParameterizedTypes() {
        if (parameterizedTypes == null) {
            parameterizedTypes = new MultivalueSet<TParameterizedType>() {
                @Override
                protected void clearOpposite(TParameterizedType e) {
                    e.setParameterizableClass(null);
                }
                @Override
                protected void setOpposite(TParameterizedType e) {
                    e.setParameterizableClass(ParameterizableClass.this);
                }
            };
        }
        return parameterizedTypes;
    }
    
    public void setParameterizedTypes(Collection<? extends TParameterizedType> parameterizedTypes) {
        this.getParameterizedTypes().clear();
        this.getParameterizedTypes().addAll(parameterizedTypes);
    }                    
    
        
    public void addParameterizedTypes(TParameterizedType one) {
        this.getParameterizedTypes().add(one);
    }   
    
    public void addParameterizedTypes(TParameterizedType one, TParameterizedType... many) {
        this.getParameterizedTypes().add(one);
        for (TParameterizedType each : many)
            this.getParameterizedTypes().add(each);
    }   
    
    public void addParameterizedTypes(Iterable<? extends TParameterizedType> many) {
        for (TParameterizedType each : many)
            this.getParameterizedTypes().add(each);
    }   
                
    public void addParameterizedTypes(TParameterizedType[] many) {
        for (TParameterizedType each : many)
            this.getParameterizedTypes().add(each);
    }
    
    public int numberOfParameterizedTypes() {
        return getParameterizedTypes().size();
    }

    public boolean hasParameterizedTypes() {
        return !getParameterizedTypes().isEmpty();
    }



}

