package br.com.finalcraft.evernifecore.api.common;

import org.apache.commons.lang3.Validate;

public interface IFHasDelegate {

    public Object getDelegate();

    public default <DELEGATE> DELEGATE getDelegate(Class<DELEGATE> delegateClass) {
        Validate.isTrue(delegateClass.isAssignableFrom(this.getDelegate().getClass()));
        return (DELEGATE) getDelegate();
    }

}
