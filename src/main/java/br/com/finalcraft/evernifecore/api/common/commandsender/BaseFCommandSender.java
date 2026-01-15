package br.com.finalcraft.evernifecore.api.common.commandsender;

public abstract class BaseFCommandSender<DELEGATE> implements FCommandSender {

    protected final DELEGATE delegate;

    public BaseFCommandSender(DELEGATE delegate) {
        this.delegate = delegate;
    }

    public DELEGATE getDelegate() {
        return delegate;
    }

    @Override
    public int hashCode() {
        return getDelegate().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
