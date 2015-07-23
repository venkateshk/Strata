/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.impl.option;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.strata.basics.PutCall;
import com.opengamma.strata.collect.ArgChecker;

/**
 * Simple representation of a European-style vanilla option.
 */
@BeanDefinition(style = "minimal", builderScope = "private")
public final class EuropeanVanillaOption
    implements ImmutableBean, Serializable {

  /**
   * The strike.
   */
  @PropertyDefinition
  private final double strike;
  /**
   * The time to expiry, year fraction.
   */
  @PropertyDefinition(validate = "ArgChecker.notNegative")
  private final double timeToExpiry;
  /**
   * Call or put, true if call, false if put.
   */
  @PropertyDefinition(validate = "notNull")
  private final PutCall putCall;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance.
   * 
   * @param strike  the strike
   * @param timeToExpiry  the time to expiry, year fraction
   * @param putCall  whether the option is put or call.
   * @return the option definition
   */
  public static EuropeanVanillaOption of(double strike, double timeToExpiry, PutCall putCall) {
    return new EuropeanVanillaOption(strike, timeToExpiry, putCall);
  }

  //-------------------------------------------------------------------------
  /**
   * Checks if the option is call.
   * 
   * @return true if call, false if put
   */
  public boolean isCall() {
    return putCall == PutCall.CALL;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code EuropeanVanillaOption}.
   * @return the meta-bean, not null
   */
  public static EuropeanVanillaOption.Meta meta() {
    return EuropeanVanillaOption.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(EuropeanVanillaOption.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private EuropeanVanillaOption(
      double strike,
      double timeToExpiry,
      PutCall putCall) {
    ArgChecker.notNegative(timeToExpiry, "timeToExpiry");
    JodaBeanUtils.notNull(putCall, "putCall");
    this.strike = strike;
    this.timeToExpiry = timeToExpiry;
    this.putCall = putCall;
  }

  @Override
  public EuropeanVanillaOption.Meta metaBean() {
    return EuropeanVanillaOption.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the strike.
   * @return the value of the property
   */
  public double getStrike() {
    return strike;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the time to expiry, year fraction.
   * @return the value of the property
   */
  public double getTimeToExpiry() {
    return timeToExpiry;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets call or put, true if call, false if put.
   * @return the value of the property, not null
   */
  public PutCall getPutCall() {
    return putCall;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      EuropeanVanillaOption other = (EuropeanVanillaOption) obj;
      return JodaBeanUtils.equal(getStrike(), other.getStrike()) &&
          JodaBeanUtils.equal(getTimeToExpiry(), other.getTimeToExpiry()) &&
          JodaBeanUtils.equal(getPutCall(), other.getPutCall());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getStrike());
    hash = hash * 31 + JodaBeanUtils.hashCode(getTimeToExpiry());
    hash = hash * 31 + JodaBeanUtils.hashCode(getPutCall());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("EuropeanVanillaOption{");
    buf.append("strike").append('=').append(getStrike()).append(',').append(' ');
    buf.append("timeToExpiry").append('=').append(getTimeToExpiry()).append(',').append(' ');
    buf.append("putCall").append('=').append(JodaBeanUtils.toString(getPutCall()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code EuropeanVanillaOption}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code strike} property.
     */
    private final MetaProperty<Double> strike = DirectMetaProperty.ofImmutable(
        this, "strike", EuropeanVanillaOption.class, Double.TYPE);
    /**
     * The meta-property for the {@code timeToExpiry} property.
     */
    private final MetaProperty<Double> timeToExpiry = DirectMetaProperty.ofImmutable(
        this, "timeToExpiry", EuropeanVanillaOption.class, Double.TYPE);
    /**
     * The meta-property for the {@code putCall} property.
     */
    private final MetaProperty<PutCall> putCall = DirectMetaProperty.ofImmutable(
        this, "putCall", EuropeanVanillaOption.class, PutCall.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "strike",
        "timeToExpiry",
        "putCall");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -891985998:  // strike
          return strike;
        case -1831499397:  // timeToExpiry
          return timeToExpiry;
        case -219971059:  // putCall
          return putCall;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends EuropeanVanillaOption> builder() {
      return new EuropeanVanillaOption.Builder();
    }

    @Override
    public Class<? extends EuropeanVanillaOption> beanType() {
      return EuropeanVanillaOption.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -891985998:  // strike
          return ((EuropeanVanillaOption) bean).getStrike();
        case -1831499397:  // timeToExpiry
          return ((EuropeanVanillaOption) bean).getTimeToExpiry();
        case -219971059:  // putCall
          return ((EuropeanVanillaOption) bean).getPutCall();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code EuropeanVanillaOption}.
   */
  private static final class Builder extends DirectFieldsBeanBuilder<EuropeanVanillaOption> {

    private double strike;
    private double timeToExpiry;
    private PutCall putCall;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -891985998:  // strike
          return strike;
        case -1831499397:  // timeToExpiry
          return timeToExpiry;
        case -219971059:  // putCall
          return putCall;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -891985998:  // strike
          this.strike = (Double) newValue;
          break;
        case -1831499397:  // timeToExpiry
          this.timeToExpiry = (Double) newValue;
          break;
        case -219971059:  // putCall
          this.putCall = (PutCall) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public EuropeanVanillaOption build() {
      return new EuropeanVanillaOption(
          strike,
          timeToExpiry,
          putCall);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("EuropeanVanillaOption.Builder{");
      buf.append("strike").append('=').append(JodaBeanUtils.toString(strike)).append(',').append(' ');
      buf.append("timeToExpiry").append('=').append(JodaBeanUtils.toString(timeToExpiry)).append(',').append(' ');
      buf.append("putCall").append('=').append(JodaBeanUtils.toString(putCall));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}