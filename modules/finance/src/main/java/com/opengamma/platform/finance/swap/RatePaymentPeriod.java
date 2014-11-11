/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.platform.finance.swap;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableDefaults;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ImmutableList;
import com.opengamma.basics.currency.Currency;

/**
 * A period over which a fixed or floating rate is paid.
 * <p>
 * A swap leg consists of one or more periods that are the basis of accrual.
 * The payment period is formed from one or more accrual periods
 * <p>
 * This class specifies the data necessary to calculate the value of the period.
 * Any combination of accrual periods is supported in the data model, however
 * there is no guarantee that exotic combinations will price sensibly.
 */
@BeanDefinition
public final class RatePaymentPeriod
    implements PaymentPeriod, ImmutableBean, Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The date that payment occurs.
   * <p>
   * The date that payment is made for the accrual periods.
   * If the schedule adjusts for business days, then this is the adjusted date.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final LocalDate paymentDate;
  /**
   * The accrual periods that combine to form the payment period.
   * <p>
   * If there is more than one accrual period then compounding may apply.
   * All accrual periods must have the same currency.
   */
  @PropertyDefinition(validate = "notEmpty", overrideGet = true)
  private final ImmutableList<RateAccrualPeriod> accrualPeriods;
  /**
   * The primary currency of the payment period.
   * <p>
   * This is the currency of the swap leg and the currency that interest calculation is made in.
   * <p>
   * The amounts of the notional are usually expressed in terms of this currency,
   * however they can be converted from amounts in a different currency.
   * See the optional {@code fxReset} property.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final Currency currency;
  /**
   * The FX reset definition, optional.
   * <p>
   * This property is used when the defined amount of the notional is specified in
   * a currency other than the currency of the swap leg. When this occurs, the notional
   * amount has to be converted using an FX rate to the swap leg currency.
   */
  @PropertyDefinition
  private final FxReset fxReset;
  /**
   * The notional amount, positive if receiving, negative if paying.
   * <p>
   * The notional amount applicable during the period.
   */
  @PropertyDefinition(validate = "notNull")
  private final double notional;
  /**
   * The negative rate method, defaulted to 'AllowNegative'.
   * <p>
   * This is used when the interest rate, observed or calculated, goes negative.
   * <p>
   * Defined by the 2006 ISDA definitions article 6.4.
   */
  @PropertyDefinition(validate = "notNull")
  private final NegativeRateMethod negativeRateMethod;
  /**
   * The compounding method to use when there is more than one accrual period, default is 'None'.
   * <p>
   * Compounding is used when combining accrual periods.
   */
  @PropertyDefinition(validate = "notNull")
  private final CompoundingMethod compoundingMethod;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance of the payment period from a single accrual period.
   * <p>
   * No compounding will apply.
   * 
   * @param paymentDate  the actual payment date, adjusted for business days
   * @param accrualPeriod  the single accrual period forming the payment period
   * @return the payment period
   */
  public static RatePaymentPeriod of(LocalDate paymentDate, RateAccrualPeriod accrualPeriod) {
    return RatePaymentPeriod.builder()
        .paymentDate(paymentDate)
        .accrualPeriods(ImmutableList.of(accrualPeriod))
        .build();
  }

  /**
   * Obtains an instance of the payment period with no compounding.
   * 
   * @param paymentDate  the actual payment date, adjusted for business days
   * @param accrualPeriods  the accrual periods forming the payment period
   * @param compoundingMethod  the compounding method
   * @return the payment period
   */
  public static RatePaymentPeriod of(
      LocalDate paymentDate, List<RateAccrualPeriod> accrualPeriods, CompoundingMethod compoundingMethod) {
    return RatePaymentPeriod.builder()
        .paymentDate(paymentDate)
        .accrualPeriods(accrualPeriods)
        .compoundingMethod(compoundingMethod)
        .build();
  }

  //-------------------------------------------------------------------------
  @ImmutableDefaults
  private static void applyDefaults(Builder builder) {
    builder.negativeRateMethod(NegativeRateMethod.ALLOW_NEGATIVE);
    builder.compoundingMethod(CompoundingMethod.NONE);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets an accrual period by index.
   * <p>
   * This returns a period using a zero-based index.
   * 
   * @param index  the zero-based period index
   * @return the accrual period
   * @throws IndexOutOfBoundsException if the index is invalid
   */
  public RateAccrualPeriod getAccrualPeriod(int index) {
    return accrualPeriods.get(index);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the start date of the period.
   * <p>
   * This is the first accrual date in the period.
   * This date has been adjusted to be a valid business day.
   * 
   * @return the start date of the period
   */
  @Override
  public LocalDate getStartDate() {
    return getAccrualPeriod(0).getStartDate();
  }

  /**
   * Gets the end date of the period.
   * <p>
   * This is the last accrual date in the period.
   * This date has been adjusted to be a valid business day.
   * 
   * @return the end date of the period
   */
  @Override
  public LocalDate getEndDate() {
    return getAccrualPeriod(accrualPeriods.size() - 1).getEndDate();
  }

  /**
   * Checks whether compounding applies.
   * <p>
   * Compounding applies if there is more than one accrual period and the
   * compounding method is not 'None'.
   * 
   * @return true if compounding applies
   */
  public boolean isCompounding() {
    return accrualPeriods.size() > 1 && compoundingMethod != CompoundingMethod.NONE;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code RatePaymentPeriod}.
   * @return the meta-bean, not null
   */
  public static RatePaymentPeriod.Meta meta() {
    return RatePaymentPeriod.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(RatePaymentPeriod.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static RatePaymentPeriod.Builder builder() {
    return new RatePaymentPeriod.Builder();
  }

  private RatePaymentPeriod(
      LocalDate paymentDate,
      List<RateAccrualPeriod> accrualPeriods,
      Currency currency,
      FxReset fxReset,
      double notional,
      NegativeRateMethod negativeRateMethod,
      CompoundingMethod compoundingMethod) {
    JodaBeanUtils.notNull(paymentDate, "paymentDate");
    JodaBeanUtils.notEmpty(accrualPeriods, "accrualPeriods");
    JodaBeanUtils.notNull(currency, "currency");
    JodaBeanUtils.notNull(notional, "notional");
    JodaBeanUtils.notNull(negativeRateMethod, "negativeRateMethod");
    JodaBeanUtils.notNull(compoundingMethod, "compoundingMethod");
    this.paymentDate = paymentDate;
    this.accrualPeriods = ImmutableList.copyOf(accrualPeriods);
    this.currency = currency;
    this.fxReset = fxReset;
    this.notional = notional;
    this.negativeRateMethod = negativeRateMethod;
    this.compoundingMethod = compoundingMethod;
  }

  @Override
  public RatePaymentPeriod.Meta metaBean() {
    return RatePaymentPeriod.Meta.INSTANCE;
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
   * Gets the date that payment occurs.
   * <p>
   * The date that payment is made for the accrual periods.
   * If the schedule adjusts for business days, then this is the adjusted date.
   * @return the value of the property, not null
   */
  @Override
  public LocalDate getPaymentDate() {
    return paymentDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the accrual periods that combine to form the payment period.
   * <p>
   * If there is more than one accrual period then compounding may apply.
   * All accrual periods must have the same currency.
   * @return the value of the property, not empty
   */
  @Override
  public ImmutableList<RateAccrualPeriod> getAccrualPeriods() {
    return accrualPeriods;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the primary currency of the payment period.
   * <p>
   * This is the currency of the swap leg and the currency that interest calculation is made in.
   * <p>
   * The amounts of the notional are usually expressed in terms of this currency,
   * however they can be converted from amounts in a different currency.
   * See the optional {@code fxReset} property.
   * @return the value of the property, not null
   */
  @Override
  public Currency getCurrency() {
    return currency;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the FX reset definition, optional.
   * <p>
   * This property is used when the defined amount of the notional is specified in
   * a currency other than the currency of the swap leg. When this occurs, the notional
   * amount has to be converted using an FX rate to the swap leg currency.
   * @return the value of the property
   */
  public FxReset getFxReset() {
    return fxReset;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the notional amount, positive if receiving, negative if paying.
   * <p>
   * The notional amount applicable during the period.
   * @return the value of the property, not null
   */
  public double getNotional() {
    return notional;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the negative rate method, defaulted to 'AllowNegative'.
   * <p>
   * This is used when the interest rate, observed or calculated, goes negative.
   * <p>
   * Defined by the 2006 ISDA definitions article 6.4.
   * @return the value of the property, not null
   */
  public NegativeRateMethod getNegativeRateMethod() {
    return negativeRateMethod;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the compounding method to use when there is more than one accrual period, default is 'None'.
   * <p>
   * Compounding is used when combining accrual periods.
   * @return the value of the property, not null
   */
  public CompoundingMethod getCompoundingMethod() {
    return compoundingMethod;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      RatePaymentPeriod other = (RatePaymentPeriod) obj;
      return JodaBeanUtils.equal(getPaymentDate(), other.getPaymentDate()) &&
          JodaBeanUtils.equal(getAccrualPeriods(), other.getAccrualPeriods()) &&
          JodaBeanUtils.equal(getCurrency(), other.getCurrency()) &&
          JodaBeanUtils.equal(getFxReset(), other.getFxReset()) &&
          JodaBeanUtils.equal(getNotional(), other.getNotional()) &&
          JodaBeanUtils.equal(getNegativeRateMethod(), other.getNegativeRateMethod()) &&
          JodaBeanUtils.equal(getCompoundingMethod(), other.getCompoundingMethod());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getPaymentDate());
    hash += hash * 31 + JodaBeanUtils.hashCode(getAccrualPeriods());
    hash += hash * 31 + JodaBeanUtils.hashCode(getCurrency());
    hash += hash * 31 + JodaBeanUtils.hashCode(getFxReset());
    hash += hash * 31 + JodaBeanUtils.hashCode(getNotional());
    hash += hash * 31 + JodaBeanUtils.hashCode(getNegativeRateMethod());
    hash += hash * 31 + JodaBeanUtils.hashCode(getCompoundingMethod());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(256);
    buf.append("RatePaymentPeriod{");
    buf.append("paymentDate").append('=').append(getPaymentDate()).append(',').append(' ');
    buf.append("accrualPeriods").append('=').append(getAccrualPeriods()).append(',').append(' ');
    buf.append("currency").append('=').append(getCurrency()).append(',').append(' ');
    buf.append("fxReset").append('=').append(getFxReset()).append(',').append(' ');
    buf.append("notional").append('=').append(getNotional()).append(',').append(' ');
    buf.append("negativeRateMethod").append('=').append(getNegativeRateMethod()).append(',').append(' ');
    buf.append("compoundingMethod").append('=').append(JodaBeanUtils.toString(getCompoundingMethod()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code RatePaymentPeriod}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code paymentDate} property.
     */
    private final MetaProperty<LocalDate> paymentDate = DirectMetaProperty.ofImmutable(
        this, "paymentDate", RatePaymentPeriod.class, LocalDate.class);
    /**
     * The meta-property for the {@code accrualPeriods} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableList<RateAccrualPeriod>> accrualPeriods = DirectMetaProperty.ofImmutable(
        this, "accrualPeriods", RatePaymentPeriod.class, (Class) ImmutableList.class);
    /**
     * The meta-property for the {@code currency} property.
     */
    private final MetaProperty<Currency> currency = DirectMetaProperty.ofImmutable(
        this, "currency", RatePaymentPeriod.class, Currency.class);
    /**
     * The meta-property for the {@code fxReset} property.
     */
    private final MetaProperty<FxReset> fxReset = DirectMetaProperty.ofImmutable(
        this, "fxReset", RatePaymentPeriod.class, FxReset.class);
    /**
     * The meta-property for the {@code notional} property.
     */
    private final MetaProperty<Double> notional = DirectMetaProperty.ofImmutable(
        this, "notional", RatePaymentPeriod.class, Double.TYPE);
    /**
     * The meta-property for the {@code negativeRateMethod} property.
     */
    private final MetaProperty<NegativeRateMethod> negativeRateMethod = DirectMetaProperty.ofImmutable(
        this, "negativeRateMethod", RatePaymentPeriod.class, NegativeRateMethod.class);
    /**
     * The meta-property for the {@code compoundingMethod} property.
     */
    private final MetaProperty<CompoundingMethod> compoundingMethod = DirectMetaProperty.ofImmutable(
        this, "compoundingMethod", RatePaymentPeriod.class, CompoundingMethod.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "paymentDate",
        "accrualPeriods",
        "currency",
        "fxReset",
        "notional",
        "negativeRateMethod",
        "compoundingMethod");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1540873516:  // paymentDate
          return paymentDate;
        case -92208605:  // accrualPeriods
          return accrualPeriods;
        case 575402001:  // currency
          return currency;
        case -449555555:  // fxReset
          return fxReset;
        case 1585636160:  // notional
          return notional;
        case 1969081334:  // negativeRateMethod
          return negativeRateMethod;
        case -1376171496:  // compoundingMethod
          return compoundingMethod;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public RatePaymentPeriod.Builder builder() {
      return new RatePaymentPeriod.Builder();
    }

    @Override
    public Class<? extends RatePaymentPeriod> beanType() {
      return RatePaymentPeriod.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code paymentDate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LocalDate> paymentDate() {
      return paymentDate;
    }

    /**
     * The meta-property for the {@code accrualPeriods} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableList<RateAccrualPeriod>> accrualPeriods() {
      return accrualPeriods;
    }

    /**
     * The meta-property for the {@code currency} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Currency> currency() {
      return currency;
    }

    /**
     * The meta-property for the {@code fxReset} property.
     * @return the meta-property, not null
     */
    public MetaProperty<FxReset> fxReset() {
      return fxReset;
    }

    /**
     * The meta-property for the {@code notional} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> notional() {
      return notional;
    }

    /**
     * The meta-property for the {@code negativeRateMethod} property.
     * @return the meta-property, not null
     */
    public MetaProperty<NegativeRateMethod> negativeRateMethod() {
      return negativeRateMethod;
    }

    /**
     * The meta-property for the {@code compoundingMethod} property.
     * @return the meta-property, not null
     */
    public MetaProperty<CompoundingMethod> compoundingMethod() {
      return compoundingMethod;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1540873516:  // paymentDate
          return ((RatePaymentPeriod) bean).getPaymentDate();
        case -92208605:  // accrualPeriods
          return ((RatePaymentPeriod) bean).getAccrualPeriods();
        case 575402001:  // currency
          return ((RatePaymentPeriod) bean).getCurrency();
        case -449555555:  // fxReset
          return ((RatePaymentPeriod) bean).getFxReset();
        case 1585636160:  // notional
          return ((RatePaymentPeriod) bean).getNotional();
        case 1969081334:  // negativeRateMethod
          return ((RatePaymentPeriod) bean).getNegativeRateMethod();
        case -1376171496:  // compoundingMethod
          return ((RatePaymentPeriod) bean).getCompoundingMethod();
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
   * The bean-builder for {@code RatePaymentPeriod}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<RatePaymentPeriod> {

    private LocalDate paymentDate;
    private List<RateAccrualPeriod> accrualPeriods = new ArrayList<RateAccrualPeriod>();
    private Currency currency;
    private FxReset fxReset;
    private double notional;
    private NegativeRateMethod negativeRateMethod;
    private CompoundingMethod compoundingMethod;

    /**
     * Restricted constructor.
     */
    private Builder() {
      applyDefaults(this);
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(RatePaymentPeriod beanToCopy) {
      this.paymentDate = beanToCopy.getPaymentDate();
      this.accrualPeriods = new ArrayList<RateAccrualPeriod>(beanToCopy.getAccrualPeriods());
      this.currency = beanToCopy.getCurrency();
      this.fxReset = beanToCopy.getFxReset();
      this.notional = beanToCopy.getNotional();
      this.negativeRateMethod = beanToCopy.getNegativeRateMethod();
      this.compoundingMethod = beanToCopy.getCompoundingMethod();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1540873516:  // paymentDate
          return paymentDate;
        case -92208605:  // accrualPeriods
          return accrualPeriods;
        case 575402001:  // currency
          return currency;
        case -449555555:  // fxReset
          return fxReset;
        case 1585636160:  // notional
          return notional;
        case 1969081334:  // negativeRateMethod
          return negativeRateMethod;
        case -1376171496:  // compoundingMethod
          return compoundingMethod;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1540873516:  // paymentDate
          this.paymentDate = (LocalDate) newValue;
          break;
        case -92208605:  // accrualPeriods
          this.accrualPeriods = (List<RateAccrualPeriod>) newValue;
          break;
        case 575402001:  // currency
          this.currency = (Currency) newValue;
          break;
        case -449555555:  // fxReset
          this.fxReset = (FxReset) newValue;
          break;
        case 1585636160:  // notional
          this.notional = (Double) newValue;
          break;
        case 1969081334:  // negativeRateMethod
          this.negativeRateMethod = (NegativeRateMethod) newValue;
          break;
        case -1376171496:  // compoundingMethod
          this.compoundingMethod = (CompoundingMethod) newValue;
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
    public RatePaymentPeriod build() {
      return new RatePaymentPeriod(
          paymentDate,
          accrualPeriods,
          currency,
          fxReset,
          notional,
          negativeRateMethod,
          compoundingMethod);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code paymentDate} property in the builder.
     * @param paymentDate  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder paymentDate(LocalDate paymentDate) {
      JodaBeanUtils.notNull(paymentDate, "paymentDate");
      this.paymentDate = paymentDate;
      return this;
    }

    /**
     * Sets the {@code accrualPeriods} property in the builder.
     * @param accrualPeriods  the new value, not empty
     * @return this, for chaining, not null
     */
    public Builder accrualPeriods(List<RateAccrualPeriod> accrualPeriods) {
      JodaBeanUtils.notEmpty(accrualPeriods, "accrualPeriods");
      this.accrualPeriods = accrualPeriods;
      return this;
    }

    /**
     * Sets the {@code currency} property in the builder.
     * @param currency  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder currency(Currency currency) {
      JodaBeanUtils.notNull(currency, "currency");
      this.currency = currency;
      return this;
    }

    /**
     * Sets the {@code fxReset} property in the builder.
     * @param fxReset  the new value
     * @return this, for chaining, not null
     */
    public Builder fxReset(FxReset fxReset) {
      this.fxReset = fxReset;
      return this;
    }

    /**
     * Sets the {@code notional} property in the builder.
     * @param notional  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder notional(double notional) {
      JodaBeanUtils.notNull(notional, "notional");
      this.notional = notional;
      return this;
    }

    /**
     * Sets the {@code negativeRateMethod} property in the builder.
     * @param negativeRateMethod  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder negativeRateMethod(NegativeRateMethod negativeRateMethod) {
      JodaBeanUtils.notNull(negativeRateMethod, "negativeRateMethod");
      this.negativeRateMethod = negativeRateMethod;
      return this;
    }

    /**
     * Sets the {@code compoundingMethod} property in the builder.
     * @param compoundingMethod  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder compoundingMethod(CompoundingMethod compoundingMethod) {
      JodaBeanUtils.notNull(compoundingMethod, "compoundingMethod");
      this.compoundingMethod = compoundingMethod;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(256);
      buf.append("RatePaymentPeriod.Builder{");
      buf.append("paymentDate").append('=').append(JodaBeanUtils.toString(paymentDate)).append(',').append(' ');
      buf.append("accrualPeriods").append('=').append(JodaBeanUtils.toString(accrualPeriods)).append(',').append(' ');
      buf.append("currency").append('=').append(JodaBeanUtils.toString(currency)).append(',').append(' ');
      buf.append("fxReset").append('=').append(JodaBeanUtils.toString(fxReset)).append(',').append(' ');
      buf.append("notional").append('=').append(JodaBeanUtils.toString(notional)).append(',').append(' ');
      buf.append("negativeRateMethod").append('=').append(JodaBeanUtils.toString(negativeRateMethod)).append(',').append(' ');
      buf.append("compoundingMethod").append('=').append(JodaBeanUtils.toString(compoundingMethod));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}