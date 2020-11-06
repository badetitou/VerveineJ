// Automagically generated code, please do not change
package org.moosetechnology.model.famix.famixtraits;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FamixTraits")
@FameDescription("TWithComments")
public interface TWithComments  {

        @FameProperty(name = "hasComments", derived = true)
    public Boolean getHasComments();

    @FameProperty(name = "comments", opposite = "container", derived = true)
    public Collection<TComment> getComments();

    public void setComments(Collection<? extends TComment> comments);

    public void addComments(TComment one);

    public void addComments(TComment one, TComment... many);

    public void addComments(Iterable<? extends TComment> many);

    public void addComments(TComment[] many);

    public int numberOfComments();

    public boolean hasComments();

    @FameProperty(name = "numberOfComments", derived = true)
    public Number getNumberOfComments();



}

